package com.juanantbuit.weatherproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.juanantbuit.weatherproject.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(this.layoutInflater)
        setContentView(binding.root)
    }
}