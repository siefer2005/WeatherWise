package com.juanantbuit.weatherproject.framework.ui.daily_details

import android.app.Dialog
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juanantbuit.weatherproject.R
import com.juanantbuit.weatherproject.databinding.FragmentDailyDetailsBinding


class DailyDetailsFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDailyDetailsBinding

    private lateinit var lineList: ArrayList<Entry>
    private lateinit var lineDataSet: LineDataSet
    private lateinit var lineData: LineData
    private lateinit var xAxis: XAxis

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDailyDetailsBinding.inflate(inflater, container, false)

        val imageBitmap = BitmapFactory.decodeStream(context?.openFileInput("dayImage"))

        val temperatures = arguments?.getDoubleArray("temperatures")
        binding.dayName.text = arguments?.getString("dayName")
        binding.averageTemperature.text = getString(R.string.temperature, arguments?.getInt("averageTemp"))
        binding.dayImage.setImageBitmap(imageBitmap)


        setLineChartView()
        setLineListData(temperatures)
        setLineDataSet()

        lineData = LineData(lineDataSet)
        binding.lineChart.data = lineData

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        setWhiteNavigationBar(dialog)

        return dialog
    }

    private fun setWhiteNavigationBar(dialog: Dialog) {
        val window: Window? = dialog.window

        if (window != null) {
            val metrics = DisplayMetrics()

            window.windowManager.defaultDisplay.getMetrics(metrics)

            val dimDrawable = GradientDrawable()
            val navigationBarDrawable = GradientDrawable()
            navigationBarDrawable.shape = GradientDrawable.RECTANGLE
            navigationBarDrawable.setColor(Color.WHITE)

            val layers = arrayOf<Drawable>(dimDrawable, navigationBarDrawable)
            val windowBackground = LayerDrawable(layers)

            windowBackground.setLayerInsetTop(1, metrics.heightPixels)
            window.setBackgroundDrawable(windowBackground)
        }

    }

    private fun setLineChartView() {
        binding.lineChart.setScaleEnabled(false)
        binding.lineChart.legend.isEnabled = false
        binding.lineChart.description.isEnabled = false

        binding.lineChart.axisLeft.setDrawGridLines(false)
        binding.lineChart.axisLeft.setDrawAxisLine(false)
        binding.lineChart.axisLeft.setDrawLabels(false)

        binding.lineChart.axisRight.setDrawGridLines(false)
        binding.lineChart.axisRight.setDrawAxisLine(false)
        binding.lineChart.axisRight.setDrawLabels(false)

        xAxis = binding.lineChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.setDrawAxisLine(false)
    }

    private fun setLineListData(temperatures: DoubleArray?) {
        lineList = ArrayList()
        for(i in 0 until temperatures!!.size) {
            lineList.add(Entry(i.toFloat() + 1, temperatures[i].toFloat()))
        }
    }

    private fun setLineDataSet() {
        lineDataSet = LineDataSet(lineList, "")

        xAxis.valueFormatter = object : ValueFormatter() {
            override fun getAxisLabel(value: Float, axis: AxisBase): String {
                var label = ""
                when (value) {
                    1f -> label = "00:00"
                    2f -> label = "03:00"
                    3f -> label = "06:00"
                    4f -> label = "09:00"
                    5f -> label = "12:00"
                    6f -> label = "15:00"
                    7f -> label = "18:00"
                    8f -> label = "21:00"
                }
                return label
            }
        }

        lineDataSet.color = Color.BLACK
        lineDataSet.valueTextColor = Color.BLUE
        lineDataSet.valueTextSize = 13f
        lineDataSet.setDrawCircles(true)

        lineDataSet.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "º"
            }
        }

        lineDataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
    }
}