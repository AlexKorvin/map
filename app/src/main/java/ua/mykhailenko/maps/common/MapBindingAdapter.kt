package ua.mykhailenko.maps.common

import androidx.databinding.BindingAdapter
import ua.mykhailenko.maps.MapView
import ua.mykhailenko.maps.model.Location

@BindingAdapter("app:location")
fun setLocation(map: MapView, location: Location?) {
    map.setLocation(location)
}
