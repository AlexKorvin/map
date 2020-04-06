package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class Api (

	@SerializedName("ips") val ips : List<String>
)