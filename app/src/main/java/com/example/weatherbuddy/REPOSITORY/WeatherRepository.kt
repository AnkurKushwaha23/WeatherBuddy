package com.example.weatherbuddy.REPOSITORY

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.weatherbuddy.API.WeatherService
import com.example.weatherbuddy.MODELS.WeatherDataModel
import com.example.weatherbuddy.MainActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WeatherRepository(private val weatherService: WeatherService) {

    private val weatherData = MutableLiveData<WeatherDataModel>()
    val _weatherData: LiveData<WeatherDataModel> = weatherData

    suspend fun getWeather(city: String, apiKey: String, units: String) {
        // Make the network request
        weatherService.getWeatherData(city, apiKey, units)
            .enqueue(object : Callback<WeatherDataModel> {
                override fun onResponse(
                    call: Call<WeatherDataModel>,
                    response: Response<WeatherDataModel>
                ) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        weatherData.postValue(responseBody!!) // Update LiveData with weather data

                    } else {
                        // Handle unsuccessful response (e.g., show an error message)
                    }
                }

                override fun onFailure(call: Call<WeatherDataModel>, t: Throwable) {
                    Log.d("WEATHERREPOSITORY", t.message.toString())
                }
            })
    }
}
