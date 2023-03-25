package com.example.coursework.student

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.coursework.R
import com.example.coursework.databinding.ActivityStudentBinding

class StudentActivity : AppCompatActivity() {
    private lateinit var binding: ActivityStudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

        }
    }
}