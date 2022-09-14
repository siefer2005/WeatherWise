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
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.juanantbuit.weatherproject.databinding.FragmentDailyDetailsBinding


class DailyDetailsFragment: BottomSheetDialogFragment() {

    private lateinit var binding: FragmentDailyDetailsBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentDailyDetailsBinding.inflate(inflater, container, false)

        val imageBitmap = BitmapFactory.decodeStream(context?.openFileInput("dayImage"))

        binding.dayName.text = arguments?.getString("dayname")
        binding.dayImage.setImageBitmap(imageBitmap)

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
}