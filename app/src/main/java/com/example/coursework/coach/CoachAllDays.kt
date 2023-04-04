package com.example.coursework.coach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coursework.databinding.ActivityCoachAllDaysBinding

class CoachAllDays : AppCompatActivity() {
    private lateinit var binding: ActivityCoachAllDaysBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachAllDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

        }
        // TODO: Check dialog with Sage(valorant) and add horizontal ListView in Gradle
    }
}