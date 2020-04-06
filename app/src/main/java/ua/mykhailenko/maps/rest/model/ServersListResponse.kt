package ua.mykhailenko.maps.rest.model

import com.google.gson.annotations.SerializedName

data class ServersListResponse (

	@SerializedName("wireguard") val wireguard : List<Wireguard>,
	@SerializedName("openvpn") val openvpn : List<Openvpn>,
	@SerializedName("config") val config : Config
)