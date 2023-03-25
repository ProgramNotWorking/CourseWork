package com.example.coursework.coach

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coursework.R
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.databinding.ActivityCoachBinding

class CoachHelper(binding: ActivityCoachBinding,
                  adapter: LessonAdapter,
                  context: Context,
                  editStudentInfoLauncher: ActivityResultLauncher<Intent?>):
    LessonAdapter.OnEditClickListener, LessonAdapter.OnDeleteClickListener
{


    init {
        binding.apply {
            rcView.layoutManager = GridLayoutManager(context, 1)
            rcView.adapter = adapter

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.add_student -> {
                        val intent = Intent(context, EditStudentInfoActivity::class.java)
                        intent.putExtra(CoachIntentConstants.IS_EDIT, false)
                        editStudentInfoLauncher.launch(intent)
                    }
                    R.id.all_days -> {

                    }
                }

                true
            }

            // TODO: Work on launcher
        }
    }

    override fun onDeleteClick(lesson: Lesson) {

    }

    override fun onEditClick(lesson: Lesson) {

    }
}