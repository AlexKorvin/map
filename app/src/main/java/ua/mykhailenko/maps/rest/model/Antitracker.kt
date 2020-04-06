package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class Antitracker (

	@SerializedName("default") val default : Default,
	@SerializedName("hardcore") val hardcore : Hardcore
)