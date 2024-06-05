package com.example.weatherbuddy.VIEWMODEL

import android.app.Application
import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.weatherbuddy.REPOSITORY.WeatherRepository

class MainViewModelFactory(
    private val application: Application,
    private val repository: WeatherRepository,
    private val sharedPreferences: SharedPreferences
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(application,repository,sharedPreferences) as T
    }
}