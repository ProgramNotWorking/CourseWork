package com.example.coursework.coach

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.coursework.databinding.ActivityEditStudentInfoBinding

class EditStudentInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditStudentInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudentInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.apply {

        }
    }
}