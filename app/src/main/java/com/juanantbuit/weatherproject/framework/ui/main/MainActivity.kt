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
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding
import com.juanantbuit.weatherproject.domain.models.NextDayInfoModel
import com.juanantbuit.weatherproject.framework.ui.daily_details.DailyDetailsFragment
import com.juanantbuit.weatherproject.framework.ui.search_list.SearchListActivity
import com.juanantbuit.weatherproject.utils.GPS_REQUEST_CODE
import com.juanantbuit.weatherproject.utils.LANG
import com.juanantbuit.weatherproject.utils.UNITS
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
                intent.putExtra("searchType", "none")
                intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)

            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.citySearcher.setOnSearchClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                binding.citySearcher.clearFocus()
                binding.citySearcher.isIconified = true

                val intent = Intent(this, SearchListActivity::class.java)
                intent.putExtra("searchType", "none")
                intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                startActivity(intent)

            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.gpsButton.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                viewModel.checkGPSPermission(this)

            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
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

        binding.swiperefresh.setOnRefreshListener {
            if (viewModel.isNetworkAvailable(this)) {
                if (firstAppStart) {
                    binding.swiperefresh.isRefreshing = false
                    showSpecialMessage()
                    binding.specialMessage.text = getString(R.string.firstStartText)
                } else {
                    setCoordinates()
                }
            } else {
                binding.swiperefresh.isRefreshing = false
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.sidePanel.englishRadioButton.setOnClickListener {
            editor.putString("language", "en")
            editor.apply()

            changeLanguage("en")
        }

        binding.sidePanel.spanishRadioButton.setOnClickListener {
            editor.putString("language", "es")
            editor.apply()

            changeLanguage("es")
        }

        binding.sidePanel.celsiusRadioButton.setOnClickListener {
            editor.putString("units", "metric")
            editor.apply()

            changeUnits("metric")
        }

        binding.sidePanel.fahrenheitRadioButton.setOnClickListener {
            editor.putString("units", "imperial")
            editor.apply()

            changeUnits("imperial")
        }

        binding.sidePanel.kelvinRadioButton.setOnClickListener {
            editor.putString("units", "standard")
            editor.apply()

            changeUnits("standard")
        }

        binding.sidePanel.firstSaveLocation.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                val firstSaveName = prefs.getString("firstSaveName", "none")

                if(firstSaveName != "none") {
                    binding.drawer.closeDrawers()

                    viewModel.setCoordinates(
                        prefs.getFloat("firstSaveLatitude", 0.0f),
                        prefs.getFloat("firstSaveLongitude", 0.0f)
                    )
                } else {

                    val intent = Intent(this, SearchListActivity::class.java)
                    intent.putExtra("searchType", "firstSave")
                    intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                }
            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.sidePanel.secondSaveLocation.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                val secondSaveName = prefs.getString("secondSaveName", "none")

                if(secondSaveName != "none") {
                    binding.drawer.closeDrawers()

                    viewModel.setCoordinates(
                        prefs.getFloat("secondSaveLatitude", 0.0f),
                        prefs.getFloat("secondSaveLongitude", 0.0f)
                    )
                } else {

                    val intent = Intent(this, SearchListActivity::class.java)
                    intent.putExtra("searchType", "secondSave")
                    intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                }

            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.sidePanel.thirdSaveLocation.setOnClickListener {
            if (viewModel.isNetworkAvailable(this)) {

                val thirdSaveName = prefs.getString("thirdSaveName", "none")

                if(thirdSaveName != "none") {
                    binding.drawer.closeDrawers()

                    viewModel.setCoordinates(
                        prefs.getFloat("thirdSaveLatitude", 0.0f),
                        prefs.getFloat("thirdSaveLongitude", 0.0f)
                    )
                } else {

                    val intent = Intent(this, SearchListActivity::class.java)
                    intent.putExtra("searchType", "thirdSave")
                    intent.flags = FLAG_ACTIVITY_NO_ANIMATION
                    startActivity(intent)
                }

            } else {
                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.noInternetMessage)
            }
        }

        binding.sidePanel.cancel1.setOnClickListener {
            editor.remove("firstSaveName")
            editor.apply()
            binding.sidePanel.firstSaveLocationText.text = getString(R.string.touch_to_save_location)
            binding.sidePanel.cancel1.visibility = View.GONE
        }

        binding.sidePanel.cancel2.setOnClickListener {
            editor.remove("secondSaveName")
            editor.apply()
            binding.sidePanel.secondSaveLocationText.text = getString(R.string.touch_to_save_location)
            binding.sidePanel.cancel2.visibility = View.GONE
        }

        binding.sidePanel.cancel3.setOnClickListener {
            editor.remove("thirdSaveName")
            editor.apply()
            binding.sidePanel.thirdSaveLocationText.text = getString(R.string.touch_to_save_location)
            binding.sidePanel.cancel3.visibility = View.GONE
        }

        /*************************OBSERVERS*************************/

        viewModel.currentDay.observe(this) { currentDay ->
            if(currentDay != null) {

                val daysOfWeek: Array<String> = resources.getStringArray(R.array.days_of_week)

                //Dynamically selects the id of the next 4 days, searches for the text by
                //that identifier and adds it to the activity.
                binding.nextDay1.text =  daysOfWeek[viewModel.getCorrectIndex(currentDay + 1)]
                binding.nextDay2.text =  daysOfWeek[viewModel.getCorrectIndex(currentDay + 2)]
                binding.nextDay3.text =  daysOfWeek[viewModel.getCorrectIndex(currentDay + 3)]
                binding.nextDay4.text =  daysOfWeek[viewModel.getCorrectIndex(currentDay + 4)]
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
        }

        viewModel.cityInfo.observe(this) { cityInfo ->
            if (cityInfo != null) {
                refreshIcon(viewModel.getImageUrl(cityInfo.iconId[0].idIcon), binding.currentImage)
                binding.cityName.text = getString(R.string.city_name, cityInfo.name, cityInfo.country.countryId)
                binding.currentTemp.text = getString(R.string.temperature, cityInfo.mainInfo.temp.toInt())
                binding.tempFeelsLike.text = getString(R.string.temperature, cityInfo.mainInfo.thermalSensation.toInt())
                binding.humidityPercentage.text = getString(R.string.humidity_template, cityInfo.mainInfo.humidity)

                if(UNITS == "imperial") {
                    binding.windVelocity.text = getString(R.string.wind_speed_imperial, cityInfo.windVelocity.speed.toInt())
                } else {
                    binding.windVelocity.text = getString(R.string.wind_speed, cityInfo.windVelocity.speed.toInt())
                }
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

            hideProgressBar()
            binding.swiperefresh.isRefreshing = false
        }
    }

    override fun onResume() {
        super.onResume()

        firstAppStart = prefs.getBoolean("firstAppStart", true)

        if (viewModel.isNetworkAvailable(this)) {
            if (firstAppStart) {
                //Selects the user's preferred language as default language
                AppCompatDelegate.getApplicationLocales()

                val languageCode = prefs.getString("language", "en")
                changeLanguage(languageCode!!)

                if (languageCode == "en") {
                    binding.sidePanel.englishRadioButton.isChecked = true
                } else {
                    binding.sidePanel.spanishRadioButton.isChecked = true
                }

                showSpecialMessage()
                binding.specialMessage.text = getString(R.string.firstStartText)
            } else {

                UNITS = prefs.getString("units", "metric")!!
                LANG = prefs.getString("language", "en")!!

                changeLanguage(LANG)

                if (LANG == "en") {
                    binding.sidePanel.englishRadioButton.isChecked = true
                } else {
                    binding.sidePanel.spanishRadioButton.isChecked = true
                }

                when (UNITS) {
                    "metric" -> {
                        binding.sidePanel.celsiusRadioButton.isChecked = true
                    }
                    "imperial" -> {
                        binding.sidePanel.fahrenheitRadioButton.isChecked = true
                    }
                    else -> {
                        binding.sidePanel.kelvinRadioButton.isChecked = true
                    }
                }

                showProgressBar()
                setCoordinates()
                setSaveLocations()
            }
        } else {
            showSpecialMessage()
            binding.specialMessage.text = getString(R.string.noInternetMessage)
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

    private fun showSpecialMessage() {
        binding.cityName.visibility = View.GONE
        binding.principalCardView.visibility = View.GONE
        binding.next4Days.visibility = View.GONE
        binding.horizontalScrollView.visibility = View.GONE
        binding.progressBar.visibility = View.GONE

        binding.specialMessage.visibility = View.VISIBLE
    }

    private fun showProgressBar() {
        binding.cityName.visibility = View.GONE
        binding.principalCardView.visibility = View.GONE
        binding.next4Days.visibility = View.GONE
        binding.horizontalScrollView.visibility = View.GONE
        binding.specialMessage.visibility = View.GONE

        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideProgressBar() {
        binding.cityName.visibility = View.VISIBLE
        binding.principalCardView.visibility = View.VISIBLE
        binding.next4Days.visibility = View.VISIBLE
        binding.horizontalScrollView.visibility = View.VISIBLE
        binding.specialMessage.visibility = View.GONE

        binding.progressBar.visibility = View.GONE
    }

    private fun setCoordinates() {
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        editor = prefs.edit()

        viewModel.setCoordinates(
            prefs.getFloat("lastLatitude", 0.0f),
            prefs.getFloat("lastLongitude", 0.0f)
        )
    }

    private fun setSaveLocations() {
        val firstSaveName = prefs.getString("firstSaveName", "none")
        val secondSaveName = prefs.getString("secondSaveName", "none")
        val thirdSaveName = prefs.getString("thirdSaveName", "none")

        if(firstSaveName != "none") {
            binding.sidePanel.firstSaveLocationText.text = firstSaveName
            binding.sidePanel.cancel1.visibility = View.VISIBLE
        }

        if(secondSaveName != "none") {
            binding.sidePanel.secondSaveLocationText.text = secondSaveName
            binding.sidePanel.cancel2.visibility = View.VISIBLE
        }

        if(thirdSaveName != "none") {
            binding.sidePanel.thirdSaveLocationText.text = thirdSaveName
            binding.sidePanel.cancel3.visibility = View.VISIBLE
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

    private fun showDailyDetails(imageView: ImageView, textView: TextView, temperatures: DoubleArray, averageTemp: Int) {

        val bundle = Bundle()
        val bitMap = (imageView.drawable as BitmapDrawable).bitmap
        saveImageFromBitmap(bitMap)

        bundle.putString("dayName", textView.text as String?)
        bundle.putDoubleArray("temperatures", temperatures)
        bundle.putInt("averageTemp", averageTemp)

        if(dailyDetailsFragment.isAdded) {
            dailyDetailsFragment.dismiss();
        }

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

    private fun changeLanguage(languageCode: String) {
        val appLocale: LocaleListCompat = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    private fun changeUnits(units: String) {
        LANG = units
        recreate()
    }
}