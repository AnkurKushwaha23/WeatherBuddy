package com.example.weatherbuddy.API

import com.example.weatherbuddy.MODELS.WeatherDataModel
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    @GET("weather")
    fun getWeatherData(
        @Query("q") city :String,
        @Query("appid") apiKey: String,
        @Query("units") units : String
    ) : Call<WeatherDataModel>
}