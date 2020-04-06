package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class Default (

	@SerializedName("ip") val ip : String,
	@SerializedName("multihop-ip") val multihopIp : String
)