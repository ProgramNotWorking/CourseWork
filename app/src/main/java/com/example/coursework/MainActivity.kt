package com.example.coursework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.view.animation.TranslateAnimation
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.databinding.ActivityMainBinding
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity

class MainActivity : AppCompatActivity(), Animation.AnimationListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var role: String

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val sharedPreferences = getSharedPreferences(
            SharedPreferencesConstants.MAIN_KEY, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        val slideOutAnimation = TranslateAnimation(
            0f,
            0f,
            0f,
            -1500f
        )
        slideOutAnimation.duration = 750

        val alphaAnimation = AlphaAnimation(1f, 0f)
        alphaAnimation.duration = 650

        val animationSet = AnimationSet(true)
        animationSet.addAnimation(slideOutAnimation)
        animationSet.addAnimation(alphaAnimation)
        animationSet.setAnimationListener(this)

        // Clear this if u want test the result of all app
        //--------------------------------------------------//
        editor.putString(
            SharedPreferencesConstants.ROLE_KEY,
            SharedPreferencesConstants.DEFAULT_VALUE
        )
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

                role = WhatStart.COACH
                playAnimation(animationSet)

                true
            }
            studentImage.setOnTouchListener { _, _ ->
                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.STUDENT
                )
                editor.apply()

                role = WhatStart.STUDENT
                playAnimation(animationSet)

                true
            }
            schoolkidImage.setOnTouchListener { _, _ ->
                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY, SharedPreferencesConstants.SCHOOLKID
                )
                editor.apply()

                role = WhatStart.SCHOOLKID
                playAnimation(animationSet)

                true
            }
        }
    }

    private fun playAnimation(animation: AnimationSet) {
        binding.apply {
            backLayout.startAnimation(animation)
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

    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {
        binding.apply {
            coachImage.visibility = View.INVISIBLE
            coachDesc.visibility = View.INVISIBLE
            studentImage.visibility = View.INVISIBLE
            studentDesc.visibility = View.INVISIBLE
            schoolkidImage.visibility = View.INVISIBLE
            schoolkidDesc.visibility = View.INVISIBLE
            choseTextView.visibility = View.INVISIBLE
        }

        when (role) {
            WhatStart.COACH -> startCoach()
            WhatStart.STUDENT -> startStudent()
            WhatStart.SCHOOLKID -> startSchoolkid()
        }
    }

    override fun onAnimationRepeat(p0: Animation?) {}
}

object WhatStart {
    const val COACH = "coach"
    const val STUDENT = "student"
    const val SCHOOLKID = "schoolkid"
}