package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class Config (

	@SerializedName("antitracker") val antitracker : Antitracker,
	@SerializedName("api") val api : Api
)