package com.example.coursework.coach

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import com.example.coursework.databinding.ActivityCoachBinding

class CoachActivity : AppCompatActivity(),
    LessonAdapter.OnDeleteClickListener,
    LessonAdapter.OnEditClickListener
{
    private lateinit var binding: ActivityCoachBinding
    private val adapter = LessonAdapter(this@CoachActivity, this@CoachActivity)

    private var editStudentInfoLauncher: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachBinding.inflate(layoutInflater)
        setContentView(binding.root)

        CoachHelper(binding, adapter, this@CoachActivity, editStudentInfoLauncher)
    }

    override fun onDeleteClick(lesson: Lesson) {

    }

    override fun onEditClick(lesson: Lesson) {

    }
}