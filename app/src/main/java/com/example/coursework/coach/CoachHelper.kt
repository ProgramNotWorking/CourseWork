package com.example.coursework.coach

import com.example.coursework.R
import com.example.coursework.databinding.ActivityCoachBinding

class CoachHelper(binding: ActivityCoachBinding):
    LessonAdapter.OnEditClickListener, LessonAdapter.OnDeleteClickListener
{
    // TODO: Think about this shit
    val adapter = LessonAdapter(this, this)

    init {
        binding.apply {
            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.add_student -> {

                    }
                    R.id.all_days -> {

                    }
                }

                true
            }
        }
    }

    override fun onDeleteClick(lesson: Lesson) {

    }

    override fun onEditClick(lesson: Lesson) {

    }
}