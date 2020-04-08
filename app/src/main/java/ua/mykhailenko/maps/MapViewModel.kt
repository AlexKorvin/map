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
import ua.mykhailenko.maps.rest.model.Openvpn
import ua.mykhailenko.maps.rest.model.ServersListResponse
import kotlin.random.Random

class MapViewModel {

    companion object {
        const val HTTPS = "https://"
        const val SLASH = "/"
        const val BASE_URL = "api.ivpn.net"
    }

    val currentLocation: ObservableField<Location> = ObservableField()
    val buttonText: ObservableField<String> = ObservableField()

    var homeLocation: Location? = null
    var servers: List<Openvpn>? = null
    var state: State = State.DISCONNECTED

    private val retrofit: Retrofit

    init {
        retrofit = Retrofit.Builder()
            .baseUrl(HTTPS + BASE_URL + SLASH)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(IVPNService::class.java)

        val locationCall = service.getLocation()
        val serversCall = service.getServers()

        println("Init MapViewModel")
        locationCall.enqueue(object : Callback<LocationResponse> {
            override fun onResponse(
                call: Call<LocationResponse>,
                response: Response<LocationResponse>
            ) {
                println("Location call, onResponse method: response.code() = ${response.code()}")
                if (response.code() == 200) {
                    homeLocation = Location(
                        response.body().longitude.toFloat(),
                        response.body().latitude.toFloat(),
                        false
                    )
                    currentLocation.set(homeLocation)
                }
            }

            override fun onFailure(call: Call<LocationResponse>, t: Throwable) {
                println("Location call, onFailure method: Throwable = $t")
            }
        })

        serversCall.enqueue(object : Callback<ServersListResponse> {
            override fun onResponse(
                call: Call<ServersListResponse>,
                response: Response<ServersListResponse>
            ) {
                if (response.code() == 200) {
                    servers = response.body().openvpn
                }
            }

            override fun onFailure(call: Call<ServersListResponse>, t: Throwable) {

            }
        })

        buttonText.set(state.actionText)
    }

    fun onConnectButtonClick() {
        if (state == State.DISCONNECTED) {
            servers?.let {
                state = State.CONNECTED
                val openvpn = it[Random.nextInt(it.size)]
                currentLocation.set(Location(
                    openvpn.longitude.toFloat(),
                    openvpn.latitude.toFloat(),
                    true
                ))
            }
        } else {
            state = State.DISCONNECTED
            currentLocation.set(homeLocation)
        }

        buttonText.set(state.actionText)
    }

    enum class State(val actionText: String){
        CONNECTED("Disconnect"),
        DISCONNECTED("Connect")
    }
}