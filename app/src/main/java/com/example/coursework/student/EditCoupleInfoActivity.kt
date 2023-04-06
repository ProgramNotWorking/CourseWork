package com.example.coursework.student

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityEditCoupleInfoBinding

class EditCoupleInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditCoupleInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditCoupleInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        if (intent.getBooleanExtra(StudentIntentConstants.IS_EDIT, false)) {
            binding.coupleTitleEditTextField.setText(
                intent.getStringExtra(StudentIntentConstants.COUPLE_TITLE)
            )
            binding.enterCoupleTimeButton.text = (
                intent.getStringExtra(StudentIntentConstants.COUPLE_TIME)
            )
            binding.audienceNumberEditTextField.setText(
                intent.getStringExtra(StudentIntentConstants.AUDIENCE_NUMBER)
            )

            intent.putExtra(StudentIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(StudentIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            dayTitleCoupleTextView.text = setDayText()

            enterCoupleTimeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this@EditCoupleInfoActivity, { _, hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    enterCoupleTimeButton.text = time
                }, 0, 0, true)
                timePickerDialog.window?.setBackgroundDrawableResource(
                    R.color.white
                )

                timePickerDialog.show()

                val okButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                val cancelButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                okButton.setTextColor(
                    ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.black)
                )
                cancelButton.setTextColor(
                    ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.black)
                )
            }

            backButtonCouple.setOnClickListener {
                val intent = Intent(
                    this@EditCoupleInfoActivity, StudentActivity::class.java
                )
                setResult(RESULT_CANCELED)
                finish()
            }

            saveCoupleButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TITLE, coupleTitleEditTextField.text.toString()
                    )
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TIME, enterCoupleTimeButton.text.toString()
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
            } else if (enterCoupleTimeButton.text.toString() == getString(R.string.enter_couple_time)) {
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