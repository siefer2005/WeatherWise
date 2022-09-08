package com.juanantbuit.weatherproject.framework.ui.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.cityInfo.observe(this) { cityInfo ->
            refreshIcon(viewModel.getImageUrl())
            binding.cityName.text = cityInfo?.name + ", " + cityInfo?.country?.countryId
            binding.currentTemp.text = cityInfo?.mainInfo?.temp?.toInt().toString() + "º"
            binding.tempFeelsLike.text = cityInfo?.mainInfo?.thermalSensation?.toInt().toString() + "º"
            binding.humidityPercentage.text = cityInfo?.mainInfo?.humidity.toString() + "%"
            binding.windVelocity.text = cityInfo?.windVelocity?.speed?.toInt().toString() + " km/hr"

        }

    }

    private fun refreshIcon(url:String) {

        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(binding.currentImage)
    }

}