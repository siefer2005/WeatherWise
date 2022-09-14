package com.juanantbuit.weatherproject.framework.ui.daily_details

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
}