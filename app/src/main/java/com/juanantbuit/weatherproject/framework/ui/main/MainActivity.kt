package com.juanantbuit.weatherproject.framework.ui.main

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.utils.DAYS_Of_WEEK

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        viewModel.getCityInfo(38.2391, -1.41877)
        viewModel.getForecastResponse(38.2391,-1.41877)
        viewModel.getCurrentDay()

        viewModel.currentDay.observe(this) { currentDay ->
            if(currentDay != null) {
                binding.nextDay1.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 1)]
                binding.nextDay2.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 2)]
                binding.nextDay3.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 3)]
                binding.nextDay4.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 4)]
                binding.nextDay5.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 5)]
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

            val nextDaysInfo: MutableList<NextDayInfoModel> =
                viewModel.getNextDaysInfo(forecastResponse!!)

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

    private fun refreshIcon(url:String, imageView: ImageView) {

        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(imageView)
    }

}