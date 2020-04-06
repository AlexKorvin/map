package ua.mykhailenko.maps

import androidx.databinding.ObservableField
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ua.mykhailenko.maps.model.Location
import ua.mykhailenko.maps.rest.IVPNService
import ua.mykhailenko.maps.rest.model.LocationResponse
import ua.mykhailenko.maps.rest.model.ServersListResponse

class MapViewModel {

    companion object {
        const val BASE_URL = "api.ivpn.net"
    }

    val currentLocation: ObservableField<Location> = ObservableField()

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(IVPNService::class.java)

        val locationCall = service.getLocation()
        val serversCall = service.getServers()

        locationCall.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(call: Call<LocationResponse>, response: Response<LocationResponse>) {
                if (response.code() == 200) {

                }
            }
            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {

            }
        })

        serversCall.enqueue(object : Callback<ServersListResponse> {
            override fun onResponse(call: Call<ServersListResponse>, response: Response<ServersListResponse>) {
                if (response.code() == 200) {

                }
            }
            override fun onFailure(call: Call<ServersListResponse>, t: Throwable) {

            }
        })

    }
}