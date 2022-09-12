package com.juanantbuit.weatherproject.framework.ui.main

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.framework.ui.search_list.SearchListActivity
import com.juanantbuit.weatherproject.utils.DAYS_Of_WEEK
import com.juanantbuit.weatherproject.utils.GPS_REQUEST_CODE

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.getCurrentDay()

        binding.gpsButton.setOnClickListener {
            viewModel.checkGPSPermission(this)
        }

        binding.citySearcher.setOnClickListener {
            val intent = Intent(this, SearchListActivity::class.java)
            intent.flags = FLAG_ACTIVITY_NO_ANIMATION
            startActivity(intent)
        }

        viewModel.currentDay.observe(this) { currentDay ->
            if(currentDay != null) {
                binding.nextDay1.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 1)]
                binding.nextDay2.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 2)]
                binding.nextDay3.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 3)]
                binding.nextDay4.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 4)]
                binding.nextDay5.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 5)]
            }
        }

        viewModel.coordinates.observe(this) { coordinates ->
            if(coordinates != null) {
                viewModel.getCityInfo(coordinates["latitude"], coordinates["longitude"])
                viewModel.getForecastResponse(coordinates["latitude"], coordinates["longitude"])
            }
        }

        viewModel.cityInfo.observe(this) { cityInfo ->
            if (cityInfo != null) {
                refreshIcon(viewModel.getImageUrl(cityInfo.iconId[0].idIcon), binding.currentImage)
                binding.cityName.text = cityInfo.name + ", " + cityInfo.country.countryId
                binding.currentTemp.text = " " + cityInfo.mainInfo.temp.toInt().toString() + "º"
                binding.tempFeelsLike.text = " " + cityInfo.mainInfo.thermalSensation.toInt().toString() + "º"
                binding.humidityPercentage.text = " " + cityInfo.mainInfo.humidity.toString() + "%"
                binding.windVelocity.text = cityInfo.windVelocity.speed.toInt().toString() + " km/hr"
            }
        }

        viewModel.forecastResponse.observe(this) { forecastResponse ->

            val nextDaysInfo: MutableList<NextDayInfoModel> = viewModel.getNextDaysInfo(forecastResponse!!)

            binding.nextDayTemp1.text = " " + nextDaysInfo[0].averageTemp.toString() + "º"
            binding.nextDayTemp2.text = " " + nextDaysInfo[1].averageTemp.toString() + "º"
            binding.nextDayTemp3.text = " " + nextDaysInfo[2].averageTemp.toString() + "º"
            binding.nextDayTemp4.text = " " + nextDaysInfo[3].averageTemp.toString() + "º"
            binding.nextDayTemp5.text = " " + nextDaysInfo[4].averageTemp.toString() + "º"

            refreshIcon(viewModel.getImageUrl(nextDaysInfo[0].iconId), binding.nextDayImage1)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[1].iconId), binding.nextDayImage2)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[2].iconId), binding.nextDayImage3)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[3].iconId), binding.nextDayImage4)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[4].iconId), binding.nextDayImage5)
        }
    }

    override fun onResume() {
        super.onResume()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        viewModel.setCoordinates(prefs.getFloat("lastLatitude", 0.0f), prefs.getFloat("lastLongitude", 0.0f))
    }

    private fun refreshIcon(url:String, imageView: ImageView) {

        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(imageView)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            GPS_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    viewModel.getCoordinatesFromGPS(this)
                }
                return
            }
        }

    }
}