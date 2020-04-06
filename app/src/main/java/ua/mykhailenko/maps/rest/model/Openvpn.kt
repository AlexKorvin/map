package ua.mykhailenko.maps.rest.model


import com.google.gson.annotations.SerializedName

data class Openvpn (

	@SerializedName("gateway") val gateway : String,
	@SerializedName("country_code") val country_code : String,
	@SerializedName("country") val country : String,
	@SerializedName("city") val city : String,
	@SerializedName("latitude") val latitude : Double,
	@SerializedName("longitude") val longitude : Double,
	@SerializedName("ip_addresses") val ip_addresses : List<String>
)