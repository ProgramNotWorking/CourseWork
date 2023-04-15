package com.example.coursework

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coursework.databinding.ActivityEasterEggBinding

class EasterEggActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEasterEggBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEasterEggBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {
            // TODO: SEX
        }
    }
}