package com.example.weatherbuddy

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentActivity

class InternetCheck {

    @SuppressLint("ServiceCast")
    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    fun checkInternetAndShowDialog(context: Context, activity: FragmentActivity) {
        if (!isInternetAvailable(context)) {
            showdialog(context)
        }
    }

    private fun showdialog(context: Context) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle("WeatherBuddy")
            .setMessage("You're offline. Make sure to turn on the Internet to proceed.")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }
        val alertDialog: AlertDialog = builder.create()
        alertDialog.show()
    }
}


