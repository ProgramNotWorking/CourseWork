package com.example.coursework

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val from = if (intent.getBooleanExtra(CoachIntentConstants.FROM_COACH, false))
            OptionsDataNames.COACH
        else
            OptionsDataNames.ITS_BAD

        binding.apply {
            when (from) {
                OptionsDataNames.COACH ->
                    currentTypeOfActivityImage.setImageResource(R.drawable.coach)
                OptionsDataNames.STUDENT ->
                    currentTypeOfActivityImage.setImageResource(R.drawable.student)
                OptionsDataNames.SCHOOLKID ->
                    currentTypeOfActivityImage.setImageResource(R.drawable.schoolkid)
                OptionsDataNames.ITS_BAD ->
                    Log.d("From options:", "Trouble with image")
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

                    }
                    OptionsDataNames.SCHOOLKID -> {

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
}

object OptionsDataNames {
    const val COACH = "coach"
    const val STUDENT = "student"
    const val SCHOOLKID = "schoolkid"

    const val ITS_BAD = "Error"
}