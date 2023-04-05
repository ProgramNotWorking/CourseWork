package com.example.coursework.student

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityEditCoupleInfoBinding
import com.example.coursework.schoolkid.SchoolLesson

class EditCoupleInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCoupleInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCoupleInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        val isEditStudent = if (intent.getBooleanExtra(StudentIntentConstants.IS_EDIT, false)) {
            intent.putExtra(StudentIntentConstants.IS_ADDED, false)
            true
        } else {
            intent.putExtra(StudentIntentConstants.IS_ADDED, true)
            false
        }

        binding.apply {
            dayTitleCoupleTextView.text = setDayText()

            backButtonCouple.setOnClickListener {
                val intent = Intent(
                    this@EditCoupleInfoActivity, StudentActivity::class.java
                )
                setResult(RESULT_CANCELED)
                finish()
            }

            if (isEditStudent) {
                coupleTitleEditTextField.setText(
                    intent.getStringExtra(StudentIntentConstants.COUPLE_TITLE)
                )
                coupleTimeEditTextField.setText(
                    intent.getStringExtra(StudentIntentConstants.COUPLE_TIME)
                )
                audienceNumberEditTextField.setText(
                    intent.getStringExtra(StudentIntentConstants.AUDIENCE_NUMBER)
                )
            }

            saveCoupleButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TITLE, coupleTitleEditTextField.text.toString()
                    )
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TIME, coupleTimeEditTextField.text.toString()
                    )
                    intent.putExtra(
                        StudentIntentConstants.AUDIENCE_NUMBER, audienceNumberEditTextField.text.toString()
                    )
                    setDay()

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun setDay() {
        when (intent.getStringExtra(StudentIntentConstants.WHAT_DAY)) {
            DaysConstants.MONDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.MONDAY
            )
            DaysConstants.TUESDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.TUESDAY
            )
            DaysConstants.WEDNESDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.WEDNESDAY
            )
            DaysConstants.THURSDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.THURSDAY
            )
            DaysConstants.FRIDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.FRIDAY
            )
            DaysConstants.SATURDAY -> intent.putExtra(
                StudentIntentConstants.WHAT_DAY, DaysConstants.SATURDAY
            )
        }
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

    private fun showText(text: String) {
        Toast.makeText(
            this@EditCoupleInfoActivity,
            text,
            Toast.LENGTH_SHORT
        ).show()
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.mainCoupleLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.mainCoupleLayout.windowToken, 0)
            }

            true
        }
    }

    private fun setDayText(): String {
        return when (intent.getStringExtra(StudentIntentConstants.WHAT_DAY)) {
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