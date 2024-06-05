package com.example.weatherbuddy

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.weatherbuddy.API.RetrofitHelper
import com.example.weatherbuddy.API.WeatherService
import com.example.weatherbuddy.REPOSITORY.WeatherRepository
import com.example.weatherbuddy.VIEWMODEL.MainViewModel
import com.example.weatherbuddy.VIEWMODEL.MainViewModelFactory
import com.example.weatherbuddy.databinding.ActivityMainBinding
import com.facebook.shimmer.ShimmerFrameLayout
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var mainViewModel: MainViewModel
    private lateinit var internetCheck: InternetCheck
    private lateinit var shimmerFrameLayout: ShimmerFrameLayout
    private lateinit var contentLayout: View
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var connectivityManager: ConnectivityManager
    private lateinit var networkCallback: ConnectivityManager.NetworkCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        internetCheck = InternetCheck()
        internetCheck.checkInternetAndShowDialog(this, this)

        connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        //        shimmer loader
        shimmerFrameLayout = findViewById(R.id.shimmer_view_container)
        contentLayout = findViewById(R.id.content_layout)

//        current day and date
        binding.tvDay.text = getCurrentDayOfWeek()
        binding.tvDate.text = getCurrentDate()

        showShimmer(true)
        if (internetCheck.isInternetAvailable(this)) {
            searchCity()
        }

        sharedPreferences =
            getSharedPreferences("MyPreferences", AppCompatActivity.MODE_PRIVATE)
        val weatherService = RetrofitHelper.getInstance().create(WeatherService::class.java)
        val repository = WeatherRepository(weatherService)

        mainViewModel =
            ViewModelProvider(this, MainViewModelFactory(application,repository, sharedPreferences)).get(
                MainViewModel::class.java
            )

        registerNetworkCallback()
        mainViewModel.weatherList.observe(this, Observer {

            val temperature = it.main.temp.toString()
            val max = it.main.temp_max.toString()
            val min = it.main.temp_min.toString()
            val humidity = it.main.humidity.toString()
            val windSpeed = it.wind.speed.toString()
            val condition = it.weather.firstOrNull()?.main ?: "Unkown"
            val pressure = it.main.pressure.toString()

            binding.tvCity.text = it.name.capitalize(Locale.getDefault())
            binding.tvTemp.text = "$temperature°C"
            binding.tvMax.text = "Max : $max°C"
            binding.tvMin.text = "Min : $min°C"
            binding.tvHumidity.text = "$humidity%"
            binding.tvWindSpeed.text = "$windSpeed m/s"
            binding.tvWeather.text = condition
            binding.tvPressure.text = "$pressure hPa"

            changeBackground(condition)
            showShimmer(false)

        })

        val backcallback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showAlertDialog()
            }
        }
        onBackPressedDispatcher.addCallback(this, backcallback)
    }

    private fun searchCity() {
        val searchView = binding.idSearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    mainViewModel.fetchData(query)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }
        })
    }

    private fun changeBackground(condition: String) {
        val isNight = isNightTime()
        if (isNight) {
            binding.root.setBackgroundResource(R.drawable.night_bg)
            binding.imgWeather.setBackgroundResource(R.drawable.night)
        } else {
            when (condition) {
                "Clear Sky", "Sunny", "Clear" -> {
                    binding.root.setBackgroundResource(R.drawable.cloudy_bg)
                    binding.imgWeather.setBackgroundResource(R.drawable.sunny)
                }

                "Partly Clouds", "Clouds", "Overcast", "Mist", "Foggy", "Haze" -> {
                    binding.root.setBackgroundResource(R.drawable.cloudy_bg)
                    binding.imgWeather.setBackgroundResource(R.drawable.cloudy)
                }

                "Light Rain", "Drizzle", "Moderate Rain", "Showers", "Heavy Rain", "Rain" -> {
                    binding.root.setBackgroundResource(R.drawable.rain_bg)
                    binding.imgWeather.setBackgroundResource(R.drawable.thunder)
                }

                "Light Snow", "Moderate Snow", "Heavy Snow", "Blizzard" -> {
                    binding.root.setBackgroundResource(R.drawable.winter_bg)
                    binding.imgWeather.setBackgroundResource(R.drawable.snowflake)
                }

                else -> {
                    binding.root.setBackgroundResource(R.drawable.cloudy_bg)
                    binding.imgWeather.setBackgroundResource(R.drawable.sunny)
                }
            }
        }
    }

    private fun isNightTime(): Boolean {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        return hour < 6 || hour >= 20
    }

    fun getCurrentDate(): String {
        val currentDate = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy")
        return currentDate.format(formatter)
    }

    fun getCurrentDayOfWeek(): String {
        val currentDay = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDate.now().dayOfWeek
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        return currentDay.toString()
    }

    private fun showShimmer(show: Boolean) {
        if (show) {
            shimmerFrameLayout.startShimmer()
            shimmerFrameLayout.visibility = View.VISIBLE
            contentLayout.visibility = View.GONE
        } else {
            shimmerFrameLayout.stopShimmer()
            shimmerFrameLayout.visibility = View.GONE
            contentLayout.visibility = View.VISIBLE
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("WeatherBuddy")
            .setMessage("Do you want to exit?")
            .setPositiveButton("Yes") { dialog, _ ->
                finishAffinity()
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()

    }
    override fun onDestroy() {
        super.onDestroy()
        connectivityManager.unregisterNetworkCallback(networkCallback)
    }
    private fun registerNetworkCallback() {
        networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Internet is back, trigger the API request
                val cityName = sharedPreferences.getString("city_name", "Delhi")
                mainViewModel.fetchData(cityName ?: "Delhi")
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Handle network lost scenario if needed
            }
        }

        val networkRequest = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
    }
}