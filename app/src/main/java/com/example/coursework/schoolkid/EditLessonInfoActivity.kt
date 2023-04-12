package com.example.coursework.schoolkid

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.databinding.ActivityEditLessonInfoBinding

class EditLessonInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditLessonInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditLessonInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        if (intent.getBooleanExtra(SchoolkidIntentConstants.IS_EDIT, false)) {
            binding.lessonTitleEditTextField.setText(
                intent.getStringExtra(SchoolkidIntentConstants.LESSON_TITLE)
            )
            binding.enterLessonTimeButton.text = (
                intent.getStringExtra(SchoolkidIntentConstants.LESSON_TIME)
            )
            binding.lessonAudienceNumberEditTextField.setText(
                intent.getStringExtra(SchoolkidIntentConstants.AUDIENCE_NUMBER)
            )

            intent.putExtra(SchoolkidIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(SchoolkidIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            dayTitleCoupleTextView.text = setDayText()

            enterLessonTimeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this@EditLessonInfoActivity, { _, hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    enterLessonTimeButton.text = time
                }, 0, 0, true)
                timePickerDialog.window?.setBackgroundDrawableResource(
                    R.color.white
                )

                timePickerDialog.show()

                val okButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                val cancelButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                okButton.setTextColor(
                    ContextCompat.getColor(this@EditLessonInfoActivity, R.color.black)
                )
                cancelButton.setTextColor(
                    ContextCompat.getColor(this@EditLessonInfoActivity, R.color.black)
                )
            }

            saveCoupleButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        SchoolkidIntentConstants.LESSON_TITLE,
                        lessonTitleEditTextField.text.toString()
                    )
                    intent.putExtra(
                        SchoolkidIntentConstants.LESSON_TIME,
                        enterLessonTimeButton.text.toString()
                    )
                    intent.putExtra(
                        SchoolkidIntentConstants.AUDIENCE_NUMBER,
                        lessonAudienceNumberEditTextField.text.toString()
                    )
                    setDay()

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goBack()
    }

    private fun goBack() {
        setResult(RESULT_CANCELED)
        finish()
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
            return if (lessonTitleEditTextField.text.toString().isEmpty()) {
                showText(getString(R.string.couple_title_field_is_empty))

                false
            } else if (enterLessonTimeButton.text.toString() == getString(R.string.enter_time)) {
                showText(getString(R.string.couple_time_field_is_empty))

                false
            } else if (lessonAudienceNumberEditTextField.text.toString().isEmpty()) {
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