package com.example.coursework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityOptionsBinding
import com.example.coursework.db.DatabaseManager
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    private val db = DatabaseManager(this)

    private lateinit var from: String

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        from = if (intent.getBooleanExtra(CoachIntentConstants.FROM_COACH, false))
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
                    currentTypeOfActivityTextView.text = getString(R.string.coach)
                }
                OptionsDataNames.STUDENT -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.student)
                    currentTypeOfActivityTextView.text = getString(R.string.student)
                }
                OptionsDataNames.SCHOOLKID -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.schoolkid)
                    currentTypeOfActivityTextView.text = getString(R.string.schoolkid)
                }
                OptionsDataNames.ITS_BAD ->
                    Log.d("From options:", "Trouble with image")
            }

            clearDataTextView.setOnClickListener {
                val builder = AlertDialog.Builder(this@OptionsActivity)
                .setTitle(getString(R.string.data_cleaning))
                .setMessage(getString(R.string.clearing_question))

                .setTitle(
                    Html.fromHtml("<font color='#FF000000'>" +
                            getString(R.string.data_cleaning) +
                            "</font>")
                )

                .setMessage(
                    Html.fromHtml("<font color='#FF000000'>" +
                            getString(R.string.clearing_question) +
                            "</font>")
                )

                .setPositiveButton(getString(R.string.yes)) { _, _ ->
                    Toast.makeText(
                        this@OptionsActivity, getString(R.string.data_cleared), Toast.LENGTH_SHORT
                    ).show()

                    clearData()
                }
                .setNegativeButton(getString(R.string.no), null)

                val dialog = builder.create()
                dialog.window?.setBackgroundDrawableResource(R.color.white)
                dialog.findViewById<TextView>(android.R.id.title)?.setTextColor(R.color.black)
                dialog.show()
            }

            backButtonOptions.setOnClickListener {
                goBack()
            }

            footerLayout.setOnClickListener {
                val sharedPreferences = getSharedPreferences(
                    SharedPreferencesConstants.MAIN_KEY, Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()

                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY,
                    SharedPreferencesConstants.DEFAULT_VALUE
                )
                editor.apply()

                val intent = Intent(
                    this@OptionsActivity, MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goBack()
    }

    private fun clearData() {
        db.open()

        when (from) {
            OptionsDataNames.COACH -> db.clearCoachTable()
            OptionsDataNames.STUDENT -> db.clearStudentTable()
            OptionsDataNames.SCHOOLKID -> db.clearSchoolkidTable()
        }

        db.close()
    }

    private fun goBack() {
        when (from) {
            OptionsDataNames.COACH -> {
                val intent = Intent(
                    this@OptionsActivity, CoachActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            OptionsDataNames.STUDENT -> {
                val intent = Intent(
                    this@OptionsActivity, StudentActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            OptionsDataNames.SCHOOLKID -> {
                val intent = Intent(
                    this@OptionsActivity, SchoolkidActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }
}

object OptionsDataNames {
    const val COACH = "Coach/Teacher"
    const val STUDENT = "Student"
    const val SCHOOLKID = "Schoolkid"

    const val ITS_BAD = "Error"
}