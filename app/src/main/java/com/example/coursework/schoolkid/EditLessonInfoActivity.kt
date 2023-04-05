package com.example.coursework.schoolkid

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.getSystemService
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityEditCoupleInfoBinding
import com.example.coursework.databinding.ActivityEditLessonInfoBinding

class EditLessonInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLessonInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLessonInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        val isEdit = if (intent.getBooleanExtra(SchoolkidIntentConstants.IS_EDIT, false)) {
            intent.putExtra(SchoolkidIntentConstants.IS_ADDED, false)
            true
        } else {
            intent.putExtra(SchoolkidIntentConstants.IS_ADDED, true)
            false
        }

        binding.apply {
            dayTitleCoupleTextView.text = setDayText()

            backButtonCouple.setOnClickListener {
                val intent = Intent(
                    this@EditLessonInfoActivity, SchoolkidActivity::class.java
                )
                setResult(RESULT_CANCELED)
                finish()
            }

            if (isEdit) {
                coupleTitleEditTextField.setText(
                    intent.getStringExtra(SchoolkidIntentConstants.LESSON_TITLE)
                )
                coupleTimeEditTextField.setText(
                    intent.getStringExtra(SchoolkidIntentConstants.LESSON_TIME)
                )
                audienceNumberEditTextField.setText(
                    intent.getStringExtra(SchoolkidIntentConstants.AUDIENCE_NUMBER)
                )
            }

            saveCoupleButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        SchoolkidIntentConstants.LESSON_TITLE, coupleTitleEditTextField.text.toString()
                    )
                    intent.putExtra(
                        SchoolkidIntentConstants.LESSON_TIME, coupleTimeEditTextField.text.toString()
                    )
                    intent.putExtra(
                        SchoolkidIntentConstants.AUDIENCE_NUMBER, audienceNumberEditTextField.text.toString()
                    )
                    setDay()

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun setDay() {
        when (intent.getStringExtra(SchoolkidIntentConstants.WHAT_DAY)) {
            DaysConstants.MONDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.MONDAY
            )
            DaysConstants.TUESDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.TUESDAY
            )
            DaysConstants.WEDNESDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.WEDNESDAY
            )
            DaysConstants.THURSDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.THURSDAY
            )
            DaysConstants.FRIDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.FRIDAY
            )
            DaysConstants.SATURDAY -> intent.putExtra(
                SchoolkidIntentConstants.WHAT_DAY, DaysConstants.SATURDAY
            )
        }
    }

    private fun showText(text: String) {
        Toast.makeText(
            this@EditLessonInfoActivity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun completenessOfInformationTest(): Boolean {
        binding.apply {
            return if (coupleTitleEditTextField.text.toString().isEmpty()) {
                showText(getString(R.string.couple_title_field_is_empty))

                false
            } else if (coupleTimeEditTextField.text.toString().isEmpty()) {
                showText(getString(R.string.couple_time_field_is_empty))

                false
            } else if (audienceNumberEditTextField.text.toString().isEmpty()) {
                showText(getString(R.string.audience_number_field_is_empty))

                false
            } else {
                true
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.mainLessonLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.mainLessonLayout.windowToken, 0)
            }

            true
        }
    }

    private fun setDayText(): String {
        return when (intent.getStringExtra(SchoolkidIntentConstants.WHAT_DAY)) {
            DaysConstants.MONDAY -> getString(R.string.monday)
            DaysConstants.TUESDAY -> getString(R.string.tuesday)
            DaysConstants.WEDNESDAY -> getString(R.string.wednesday)
            DaysConstants.THURSDAY -> getString(R.string.thursday)
            DaysConstants.FRIDAY -> getString(R.string.friday)
            DaysConstants.SATURDAY -> getString(R.string.saturday)
            else -> getString(R.string.stub)
        }
    }
}