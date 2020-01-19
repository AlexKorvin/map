package ua.mykhailenko.maps

import ua.mykhailenko.maps.model.City
import ua.mykhailenko.maps.model.Octopus
import kotlin.math.PI
import kotlin.math.ln
import kotlin.math.tan

class MapMath {
    //Coordinates that are used to understand what part on map should be shown.
    var totalX: Float = 0f
    var totalY: Float = 0f

    //Final width and height of map's bitmap
    var bitmapWidth: Float = 0f
    var bitmapHeight: Float = 0f

    var screenWidth: Float = 0f
    var screenHeight: Float = 0f

    //(longitude, latitude) latitude from - 90 to 90, longitude from -180 to 180;y from -90 to 90; x from -180 t0 180
    //longitude will transform into x coordinate and latitude into y coordinate
    var cities = mutableListOf<City>()

    var octopus1: Octopus? = null
    var octopus2: Octopus? = null

    init {
        cities.add(City("Kyiv", 30.542721f, 50.447731f))
        cities.add(City("Melbourne", 144.96332f, -37.814f))
        cities.add(City("New York", -74.00597f, 40.71427f))
        cities.add(City("London", -0.12574f, 51.50853f))
        cities.add(City("Vienna", 16.37208f, 48.20849f))
        cities.add(City("Sydney", 151.20732f, -33.86785f))
        cities.add(City("Brussels", 4.34878f, 50.85045f))
        cities.add(City("Sofia", 23.32415f, 42.69751f))
        cities.add(City("Franca", -47.40083f, -20.53861f))
        cities.add(City("Montreal", -73.58781f, 45.50884f))

        octopus1 = Octopus(45.50884f, -73.58781f)
        octopus2 = Octopus(160f, 0f)
    }

    fun appendX(distanceX: Float) {
        totalX += distanceX
        validateXCoordinate()
    }

    fun appendY(distanceY: Float) {
        totalY += distanceY
        validateYCoordinate()
    }

    fun setX(distanceX: Float){
        totalX = distanceX
        validateXCoordinate()
    }

    fun setY(distanceY: Float) {
        totalY = distanceY
        validateYCoordinate()
    }

    fun setScreenSize(screenWidth: Float, screenHeight: Float) {
        this.screenWidth = screenWidth
        this.screenHeight = screenHeight

        val rate: Float = 2 * screenHeight / bitmapOriginalHeight.toFloat()
        bitmapWidth = bitmapOriginalWidth * rate
        bitmapHeight = bitmapOriginalHeight * rate

        initPoints()
        initOctopuses()
    }

    private fun validateXCoordinate() {
        if (totalX < 0f) {
            totalX = 0f
        }
        if (totalX > bitmapWidth - screenWidth) {
            totalX = bitmapWidth - screenWidth
        }
    }

    private fun validateYCoordinate() {
        if (totalY < 0f) {
            totalY = 0f
        }
        if (totalY > bitmapHeight - screenHeight) {
            totalY = bitmapHeight - screenHeight
        }
    }

    private fun initPoints() {
        var pair: Pair<Double, Double>

        for (city in cities) {
            pair = getCoordinatesBy(city.longitude, city.latitude)
            with(city){
                x = pair.first
                y = pair.second
            }
        }
    }

    private fun initOctopuses() {
        var pair = getCoordinatesBy(octopus1!!.longitude, octopus1!!.latitude)
        with(octopus1!!) {
            x = pair.first.toFloat()
            y = pair.second.toFloat()
        }

        pair = getCoordinatesBy(octopus2!!.longitude, octopus2!!.latitude)
        with(octopus2!!) {
            x = pair.first.toFloat()
            y = pair.second.toFloat()
        }
    }

    private fun getCoordinatesBy(longitude: Float, latitude: Float) : Pair<Double, Double> {
        var y: Double

        val blackMagicCoef: Double

        //Using this coefficients to compensate for the curvature of the map
        val xMapCoefficient = 0.026
        val yMapCoefficient = 0.965

        //Logic to convert longitude, latitude into x, y. It's enough when we will have a accurate map
        var x: Double = (longitude + 180.0) * (bitmapWidth / 360.0)
        val latRadius: Double = latitude * Math.PI / 180f
        blackMagicCoef = ln(tan((Math.PI / 4) + (latRadius / 2)))
        y = (bitmapHeight / 2) - (bitmapWidth * blackMagicCoef / (2 * PI))

        //Trying to compensate for the curvature of the map
        x -= bitmapWidth * xMapCoefficient
        if (y < bitmapHeight / 2) {
            y *= yMapCoefficient
        }

        return Pair(x, y)
    }

    companion object {
        //Dimensions that are taken from image property
        var bitmapOriginalHeight = 1505
        var bitmapOriginalWidth = 2000
    }
}