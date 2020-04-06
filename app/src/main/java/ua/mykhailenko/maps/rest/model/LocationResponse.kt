package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class LocationResponse (
    @SerializedName("ip_address") val ip_address : String,
    @SerializedName("isp") val isp : String,
    @SerializedName("organization") val organization : String,
    @SerializedName("country") val country : String,
    @SerializedName("country_code") val country_code : String,
    @SerializedName("city") val city : String,
    @SerializedName("latitude") val latitude : Double,
    @SerializedName("longitude") val longitude : Double,
    @SerializedName("isIvpnServer") val isIvpnServer : Boolean
)