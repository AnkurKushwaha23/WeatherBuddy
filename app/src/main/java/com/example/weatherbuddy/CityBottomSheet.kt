package com.example.weatherbuddy

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.weatherbuddy.databinding.BottomSheetCityBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CityBottomSheet : BottomSheetDialogFragment() {
    lateinit var binding: BottomSheetCityBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetCityBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSaveCity.setOnClickListener {
            val cityName = binding.etCityName.text.toString()
            if (!TextUtils.isEmpty(cityName)) {
                saveCityName(cityName)
            }
        }

    }

    private fun saveCityName(cityName: String) {
        val sharedPreferences: SharedPreferences =
            activity?.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) ?: return
        with(sharedPreferences.edit()) {
            putString("city_name", cityName)
            putBoolean("enabled", true) // Enabling item 1
            apply()
        }
        startActivity(Intent(context, MainActivity::class.java))
        dismiss() // Close the BottomSheet after saving
    }
}