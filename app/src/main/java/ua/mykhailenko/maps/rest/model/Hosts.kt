package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class Hosts (

	@SerializedName("hostname") val hostname : String,
	@SerializedName("host") val host : String,
	@SerializedName("public_key") val public_public_key : String,
	@SerializedName("local_ip") val local_ip : String
)