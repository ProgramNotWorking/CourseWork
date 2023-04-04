package com.example.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityOptionsBinding
import com.example.coursework.db.DatabaseManager
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    private val db = DatabaseManager(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val from = if (intent.getBooleanExtra(CoachIntentConstants.FROM_COACH, false))
            OptionsDataNames.COACH
        else if (intent.getBooleanExtra(StudentIntentConstants.FROM_STUDENT, false))
            OptionsDataNames.STUDENT
        else if (intent.getBooleanExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, false))
            OptionsDataNames.SCHOOLKID
        else
            OptionsDataNames.ITS_BAD

        binding.apply {
            when (from) {
                OptionsDataNames.COACH -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.coach)
                    currentTypeOfActivityTextView.text = OptionsDataNames.COACH
                }
                OptionsDataNames.STUDENT -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.student)
                    currentTypeOfActivityTextView.text = OptionsDataNames.STUDENT
                }
                OptionsDataNames.SCHOOLKID -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.schoolkid)
                    currentTypeOfActivityTextView.text = OptionsDataNames.SCHOOLKID
                }
                OptionsDataNames.ITS_BAD ->
                    Log.d("From options:", "Trouble with image")
            }

            deleteButton.setOnClickListener {
                Toast.makeText(
                    this@OptionsActivity, getString(R.string.data_cleared), Toast.LENGTH_SHORT
                ).show()

                clearData(from)
            }

            backButtonOptions.setOnClickListener {
                when (from) {
                    OptionsDataNames.COACH -> {
                        val intent = Intent(
                            this@OptionsActivity, CoachActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    OptionsDataNames.STUDENT -> {
                        val intent = Intent(
                            this@OptionsActivity, StudentActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    OptionsDataNames.SCHOOLKID -> {
                        val intent = Intent(
                            this@OptionsActivity, SchoolkidActivity::class.java
                        )
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                }
            }

            footerHolderLayout.setOnClickListener {
                val intent = Intent(
                    this@OptionsActivity, MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun clearData(from: String) {
        db.open()

        when (from) {
            OptionsDataNames.COACH -> db.clearCoachTable()
            OptionsDataNames.STUDENT -> db.clearStudentTable()
            OptionsDataNames.SCHOOLKID -> db.clearSchoolkidTable()
        }

        db.close()
    }
}

object OptionsDataNames {
    const val COACH = "Coach/Teacher"
    const val STUDENT = "Student"
    const val SCHOOLKID = "Schoolkid"

    const val ITS_BAD = "Error"
}