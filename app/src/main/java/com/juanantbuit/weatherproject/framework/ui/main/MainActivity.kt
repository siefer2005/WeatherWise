package com.juanantbuit.weatherproject.framework.ui.main

import android.content.Context
import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_NO_ANIMATION
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.framework.ui.daily_details.DailyDetailsFragment
import com.juanantbuit.weatherproject.framework.ui.search_list.SearchListActivity
import com.juanantbuit.weatherproject.utils.DAYS_Of_WEEK
import com.juanantbuit.weatherproject.utils.GPS_REQUEST_CODE
import kotlinx.android.synthetic.main.activity_main.*
import java.io.ByteArrayOutputStream
import java.io.FileOutputStream
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<MainViewModel>()
    private lateinit var binding: ActivityMainBinding

    private lateinit var prefs: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var firstAppStart by Delegates.notNull<Boolean>()

    private lateinit var dailyDetailsFragment: DailyDetailsFragment

    private lateinit var nextDaysInfo: MutableList<NextDayInfoModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)

        dailyDetailsFragment = DailyDetailsFragment()

        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        /*************************LISTENERS*************************/

        binding.citySearcher.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                val intent = Intent(this, SearchListActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)

            } else {
                hidePrincipalElements()
                binding.specialMessage?.text = getString(R.string.noInternetMessage)
            }
        }

        binding.citySearcher.setOnSearchClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                citySearcher.clearFocus()
                citySearcher.isIconified = true

                val intent = Intent(this, SearchListActivity::class.java)
                intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)

            } else {
                hidePrincipalElements()
                binding.specialMessage?.text = getString(R.string.noInternetMessage)
            }
        }

        binding.gpsButton.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                viewModel.checkGPSPermission(this)

            } else {
                hidePrincipalElements()
                binding.specialMessage?.text = getString(R.string.noInternetMessage)
            }
        }

        binding.nextDayInfo1.setOnClickListener {
            showDailyDetails(binding.nextDayImage1,  binding.nextDay1, nextDaysInfo[0].temperatures.toDoubleArray(), nextDaysInfo[0].averageTemp)
        }

        binding.nextDayInfo2.setOnClickListener {
            showDailyDetails(binding.nextDayImage2,  binding.nextDay2, nextDaysInfo[1].temperatures.toDoubleArray(), nextDaysInfo[1].averageTemp)
        }

        binding.nextDayInfo3.setOnClickListener {
            showDailyDetails(binding.nextDayImage3,  binding.nextDay3, nextDaysInfo[2].temperatures.toDoubleArray(), nextDaysInfo[2].averageTemp)
        }

        binding.nextDayInfo4.setOnClickListener {
            showDailyDetails(binding.nextDayImage4,  binding.nextDay4, nextDaysInfo[3].temperatures.toDoubleArray(), nextDaysInfo[3].averageTemp)
        }

        /*************************OBSERVERS*************************/

        viewModel.currentDay.observe(this) { currentDay ->
            if(currentDay != null) {
                binding.nextDay1.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 1)]
                binding.nextDay2.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 2)]
                binding.nextDay3.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 3)]
                binding.nextDay4.text = DAYS_Of_WEEK[viewModel.getCorrectIndex(currentDay + 4)]
            }
        }

        viewModel.coordinates.observe(this) { coordinates ->
            if(coordinates != null) {
                editor.putFloat("lastLatitude", coordinates["latitude"]!!)
                editor.putFloat("lastLongitude", coordinates["longitude"]!!)
                editor.apply()

                viewModel.getCityInfo(coordinates["latitude"], coordinates["longitude"])
                viewModel.getForecastResponse(coordinates["latitude"], coordinates["longitude"])
            }
            viewModel.getCurrentDay()
            showPrincipalElements()
        }

        viewModel.cityInfo.observe(this) { cityInfo ->
            if (cityInfo != null) {
                refreshIcon(viewModel.getImageUrl(cityInfo.iconId[0].idIcon), binding.currentImage)
                binding.cityName.text = getString(R.string.city_name, cityInfo.name, cityInfo.country.countryId)
                binding.currentTemp.text = getString(R.string.temperature, cityInfo.mainInfo.temp.toInt())
                binding.tempFeelsLike.text = getString(R.string.temperature, cityInfo.mainInfo.thermalSensation.toInt())
                binding.humidityPercentage.text = getString(R.string.humidity_template, cityInfo.mainInfo.humidity)
                binding.windVelocity.text = getString(R.string.wind_speed, cityInfo.windVelocity.speed.toInt())
            }
        }

        viewModel.forecastResponse.observe(this) { forecastResponse ->

            nextDaysInfo = viewModel.getNextDaysInfo(forecastResponse!!)

            binding.nextDayTemp1.text = getString(R.string.temperature, nextDaysInfo[0].averageTemp)
            binding.nextDayTemp2.text = getString(R.string.temperature, nextDaysInfo[1].averageTemp)
            binding.nextDayTemp3.text = getString(R.string.temperature, nextDaysInfo[2].averageTemp)
            binding.nextDayTemp4.text = getString(R.string.temperature, nextDaysInfo[3].averageTemp)

            refreshIcon(viewModel.getImageUrl(nextDaysInfo[0].iconId), binding.nextDayImage1)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[1].iconId), binding.nextDayImage2)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[2].iconId), binding.nextDayImage3)
            refreshIcon(viewModel.getImageUrl(nextDaysInfo[3].iconId), binding.nextDayImage4)
        }
    }

    override fun onResume() {
        super.onResume()

        firstAppStart = prefs.getBoolean("firstAppStart", true)

        if (viewModel.isNetworkAvailable(this)) {
            if (firstAppStart) {
                hidePrincipalElements()
                binding.specialMessage?.text = getString(R.string.firstStartText)
            } else {
                showPrincipalElements()
                setCoordinates()
            }
        } else {
            hidePrincipalElements()
            binding.specialMessage?.text = getString(R.string.noInternetMessage)
        }
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

    /*************************PRIVATE FUNCTIONS*************************/

    private fun hidePrincipalElements() {
        binding.cityName.visibility = View.GONE
        binding.principalCardView.visibility = View.GONE
        binding.next4Days.visibility = View.GONE
        binding.horizontalScrollView.visibility = View.GONE

        binding.specialMessage?.visibility = View.VISIBLE
    }

    private fun showPrincipalElements() {
        binding.cityName.visibility = View.VISIBLE
        binding.principalCardView.visibility = View.VISIBLE
        binding.next4Days.visibility = View.VISIBLE
        binding.horizontalScrollView.visibility = View.VISIBLE

        binding.specialMessage?.visibility = View.GONE
    }

    private fun setCoordinates() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        viewModel.setCoordinates(
            prefs.getFloat("lastLatitude", 0.0f),
            prefs.getFloat("lastLongitude", 0.0f)
        )
    }

    private fun refreshIcon(url:String, imageView: ImageView) {

        Glide.with(this)
            .load(url)
            .error(R.drawable.ic_launcher_foreground)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .into(imageView)
    }

    private fun showDailyDetails(imageView: ImageView, textView: TextView, temperatures: DoubleArray, averageTemp: Int) {

        val bundle = Bundle()
        val bitMap = (imageView.drawable as BitmapDrawable).bitmap
        saveImageFromBitmap(bitMap)

        bundle.putString("dayName", textView.text as String?)
        bundle.putDoubleArray("temperatures", temperatures)
        bundle.putInt("averageTemp", averageTemp)

        dailyDetailsFragment.arguments = bundle
        dailyDetailsFragment.show(supportFragmentManager, "dailyDetailsFragment")
    }

    private fun saveImageFromBitmap(bitmap: Bitmap) {
        val fileName = "dayImage"

        try {
            val bytes = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)

            val fo: FileOutputStream = openFileOutput(fileName, Context.MODE_PRIVATE)

            fo.write(bytes.toByteArray())
            fo.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}