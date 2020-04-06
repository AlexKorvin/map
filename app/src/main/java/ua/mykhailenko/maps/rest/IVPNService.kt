package ua.mykhailenko.maps.rest

import retrofit2.Call
import retrofit2.http.GET
import ua.mykhailenko.maps.rest.model.LocationResponse
import ua.mykhailenko.maps.rest.model.ServersListResponse

interface IVPNService {

    @GET("v4/geo-lookup")
    fun getLocation(): Call<LocationResponse>

    @GET("v4/servers.json")
    fun getServers(): Call<ServersListResponse>
}