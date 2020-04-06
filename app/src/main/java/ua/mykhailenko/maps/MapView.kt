package ua.mykhailenko.maps

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.Scroller
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import ua.mykhailenko.maps.model.Location
import ua.mykhailenko.maps.model.Tile

class MapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyle, defStyleRes) {

    //bitmap is using to draw map
    private var bitmap: Bitmap? = null
//    private var bitmapToSend: Bitmap? = null

    //Octopus image is round, so we need only one size
    private val octopusSize: Float
    private val pointRadius: Float

    //object that is used for calculation the x,y coordinates after fling gesture.
    //While fling animation is in process we can ask it for the newest and correct coordinates.
    private var scroller: Scroller = Scroller(this.context)

    private var location: Location? = null

    //Objects that are used for drawing on the canvas
    private val pointPaint = Paint()
    private val bitmapPaint = Paint()
    private var numberPaint = Paint()

    private val math = MapMath()

    private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onDown(e: MotionEvent): Boolean {
            // Aborts any active scroll animations and invalidates.
            scroller.forceFinished(true)
            ViewCompat.postInvalidateOnAnimation(this@MapView)
            return true
        }

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            math.appendX(distanceX)
            math.appendY(distanceY)

            invalidate()
            return true
        }

        override fun onFling(
            e1: MotionEvent, e2: MotionEvent,
            velocityX: Float, velocityY: Float
        ): Boolean {
            fling((-velocityX).toInt(), (-velocityY).toInt())
            return true
        }
    }

    private val gestureDetector = GestureDetector(this.context, gestureListener)

    init {
        with(pointPaint) {
            color = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        with(bitmapPaint) {
            isAntiAlias = true
            style = Paint.Style.FILL
        }

        with(numberPaint) {
            color = ResourcesCompat.getColor(resources, R.color.colorAccent, null)
            strokeWidth = 2f
            textSize = resources.getDimension(R.dimen.city_font_size)
            letterSpacing = 0.15f
            isAntiAlias = true
            style = Paint.Style.FILL_AND_STROKE
        }

        pointRadius = resources.getDimension(R.dimen.point_radius)
        octopusSize = resources.getDimension(R.dimen.octopus_size)
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    //While fling animation is active this function will be periodically called
    override fun computeScroll() {
        super.computeScroll()
        if (scroller.computeScrollOffset()) {
            math.setX(scroller.currX.toFloat())
            math.setY(scroller.currY.toFloat())

            ViewCompat.postInvalidateOnAnimation(this)
        }
    }

    private fun fling(velocityX: Int, velocityY: Int) {
        // Before flinging, aborts the current animation.
        scroller.forceFinished(true)
        // Begins the animation
        scroller.fling(
            // Current scroll position
            math.totalX.toInt(),
            math.totalY.toInt(),
            velocityX,
            velocityY,
            /*
             * Minimum and maximum scroll positions. The minimum scroll
             * position is generally zero and the maximum scroll position
             * is generally the content size less the screen size. So if the
             * content width is 1000 pixels and the screen width is 200
             * pixels, the maximum scroll offset should be 800 pixels.
             */
            0, MapMath.tileWidth * (MapMath.tilesCount + 1) - width,
            0, MapMath.tileHeight * (MapMath.tilesCount + 1) - height
        )
        // Invalidates to trigger computeScroll()
        ViewCompat.postInvalidateOnAnimation(this)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawMap(canvas)
    }

    private fun drawMap(canvas: Canvas?) {
        val srcRect = Rect()

        with(srcRect) {
            left = (math.totalX).toInt()
            right = (math.totalX + width).toInt()
            top = (math.totalY).toInt()
            bottom = (math.totalY + height).toInt()
        }

        val intersectionRect = Rect()
        val relativeRect = Rect()

        for (list in bitmaps) {
            for (tile in list) {

                if (intersectionRect.setIntersect(tile.rect, srcRect)) {
                    with(relativeRect) {
                        left = (intersectionRect.left - math.totalX).toInt()
                        right = (intersectionRect.right - math.totalX).toInt()
                        top = (intersectionRect.top - math.totalY).toInt()
                        bottom = (intersectionRect.bottom -  math.totalY).toInt()
                    }
                    with(intersectionRect) {
                        left -= tile.rect.left
                        right -= tile.rect.left
                        top -= tile.rect.top
                        bottom -= tile.rect.top
                    }
                    canvas!!.drawBitmap(tile.bitmap, intersectionRect, relativeRect, bitmapPaint)
                }
            }
        }
    }

    fun setLocation(location: Location) {
        this.location = location
    }

    private fun printRect(rect: Rect): String{
        return "left = ${rect.left}, right = ${rect.right}, top = ${rect.top}, bottom = ${rect.bottom}"
    }

    private fun drawCities(canvas: Canvas?) {
        val bounds = Rect()
        val srcRect = Rect()

        with(srcRect) {
            left = (math.totalX).toInt()
            right = (math.totalX + width).toInt()
            top = (math.totalY).toInt()
            bottom = (math.totalY + height).toInt()
        }

        for (city in math.cities) {
            canvas!!.drawCircle(
                (city.x - srcRect.left).toFloat(),
                (city.y - srcRect.top).toFloat(), pointRadius, pointPaint
            )

            numberPaint.getTextBounds(city.name, 0, city.name.length, bounds)
            canvas.drawText(
                city.name, ((city.x - srcRect.left - bounds.width() / 2).toFloat()),
                ((city.y - srcRect.top - bounds.height() / 2 - 5).toFloat()), numberPaint
            )
//            }
        }
    }

    override fun onSizeChanged(width: Int, height: Int, oldWidth: Int, oldHeight: Int) {
        super.onSizeChanged(width, height, oldWidth, oldHeight)

        if (width == 0 || height == 0) {
            return
        }

        math.setScreenSize(width.toFloat(), height.toFloat())
        initBitmap()
    }

    private fun initBitmap() {
//        val drawable = ContextCompat.getDrawable(context, R.drawable.map4)
//        bitmap = Bitmap.createScaledBitmap(
//            (drawable as BitmapDrawable).bitmap,
//            math.bitmapWidth.toInt(), math.bitmapHeight.toInt(), false
//        )

        generate()

//        bitmapToSend = Bitmap.createBitmap(math.bitmapWidth.toInt(), math.bitmapHeight.toInt(), Bitmap.Config.ARGB_8888)
//
//        val canvas = Canvas(bitmapToSend!!)
//        val paint = Paint()
//        paint.alpha = 255
//        canvas.drawRect(Rect(0, 0, math.bitmapWidth.toInt(), math.bitmapHeight.toInt()), paint)
    }

    private var bitmaps: Array<Array<Tile>> = arrayOf()

    private fun generate() {
        println("generate")
        var array: Array<Tile>
        for (i in 1..MapMath.tilesCount) {
            array = arrayOf()
            for (j in 1..MapMath.tilesCount) {
                array += Tile(
                    Rect(
                        MapMath.tileWidth * (i - 1), MapMath.tileHeight * (j - 1),
                        MapMath.tileWidth * i,
                        MapMath.tileHeight * j
                    ),
                    getBitmapFrom("tiles/row-${j}-col-${i}.png", MapMath.tileWidth, MapMath.tileHeight)
                )
            }
            bitmaps += array
        }

    }

    private fun getBitmapFrom(assetPath: String, width: Int, height: Int): Bitmap {
        val drawable = Drawable.createFromStream(
            context.assets.open(assetPath), null
        )

        return Bitmap.createScaledBitmap(
            (drawable as BitmapDrawable).bitmap, width,
            height, false
        )
    }

    private fun getBitmap(resId: Int, width: Int, height: Int): Bitmap {
        val vectorDrawable = (ContextCompat.getDrawable(context, resId))
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        vectorDrawable!!.setBounds(0, 0, canvas.width, canvas.height)
        vectorDrawable.draw(canvas)
        return bitmap
    }
}