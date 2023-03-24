package com.example.coursework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.databinding.ActivityMainBinding
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(
            SharedPreferencesConstants.MAIN_KEY, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        // Clear this if u want test the result of all app
        //--------------------------------------------------//
        editor.putString(SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.DEFAULT_VALUE)
        editor.apply()
        //--------------------------------------------------//

        when (sharedPreferences.getString(
            SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.DEFAULT_VALUE
        )) {
            SharedPreferencesConstants.COACH -> startCoach()
            SharedPreferencesConstants.STUDENT -> startStudent()
            SharedPreferencesConstants.SCHOOLKID -> startSchoolkid()
            else -> Log.d("Error:", "Error in MainActivity")
        }

        binding.apply {
            coachImage.setOnTouchListener { _, _ ->
                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.COACH
                )
                editor.apply()

                startCoach()

                true
            }
            studentImage.setOnTouchListener { _, _ ->
                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.STUDENT
                )
                editor.apply()

                startStudent()

                true
            }
            schoolkidImage.setOnTouchListener { _, _ ->
                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.SCHOOLKID
                )
                editor.apply()

                startSchoolkid()

                true
            }
        }
    }

    private fun startCoach() {
        val intent = Intent(this@MainActivity, CoachActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun startStudent() {
        val intent = Intent(this@MainActivity, StudentActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun startSchoolkid() {
        val intent = Intent(this@MainActivity, SchoolkidActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}