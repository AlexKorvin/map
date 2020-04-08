package ua.mykhailenko.maps.model

data class Location(val longitude: Float, val latitude: Float, val isConnected: Boolean) {
    var coordinate: Pair<Float, Float>? = null
}