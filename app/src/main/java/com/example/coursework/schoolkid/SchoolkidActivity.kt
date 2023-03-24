package com.example.coursework.schoolkid

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coursework.R
import com.example.coursework.databinding.ActivitySchoolkidBinding

class SchoolkidActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySchoolkidBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolkidBinding.inflate(layoutInflater)
        setContentView(binding.root)


    }
}