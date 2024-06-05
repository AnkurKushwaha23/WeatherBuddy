package com.example.weatherbuddy.VIEWMODEL

import android.app.Application
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.weatherbuddy.InternetCheck
import com.example.weatherbuddy.MODELS.WeatherDataModel
import com.example.weatherbuddy.REPOSITORY.WeatherRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel(
    private val application: Application,
    private val repository: WeatherRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModel() {

    private val internetCheck = InternetCheck()

    init {
        if (internetCheck.isInternetAvailable(application)) {
            val cityName = sharedPreferences.getString("city_name", "Delhi")
            fetchData(cityName ?: "Delhi")
        }
    }

    val weatherList: LiveData<WeatherDataModel> = repository._weatherData

    fun fetchData(city: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getWeather(city, "Apikey", "metric")
        }

    }
}
