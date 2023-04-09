package com.example.coursework.coach

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
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.databinding.ActivityEditStudentInfoBinding

class EditStudentInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditStudentInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudentInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        if (intent.getBooleanExtra(CoachIntentConstants.IS_EDIT, false)) {
            binding.editNamePlainTextView.setText(
                intent.getStringExtra(CoachIntentConstants.STUDENT_NAME)
            )
            binding.enterTimeButton.text = (
                intent.getStringExtra(CoachIntentConstants.LESSON_TIME)
            )

            intent.putExtra(CoachIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(CoachIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            backButton.setOnClickListener {
                goBack()
            }

            enterTimeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this@EditStudentInfoActivity, { _, hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    enterTimeButton.text = time
                }, 0, 0, true)
                timePickerDialog.window?.setBackgroundDrawableResource(
                    R.color.white
                )

                timePickerDialog.show()

                val okButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                val cancelButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                okButton.setTextColor(
                    ContextCompat.getColor(this@EditStudentInfoActivity, R.color.black)
                )
                cancelButton.setTextColor(
                    ContextCompat.getColor(this@EditStudentInfoActivity, R.color.black)
                )
            }

            saveButton.setOnClickListener {
                if (editNamePlainTextView.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@EditStudentInfoActivity,
                        getString(R.string.name_field_is_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (enterTimeButton.text.toString() == getString(R.string.enter_time)) {
                    Toast.makeText(
                        this@EditStudentInfoActivity,
                        getString(R.string.time_field_is_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    intent.putExtra(
                        CoachIntentConstants.STUDENT_NAME, editNamePlainTextView.text.toString()
                    )
                    intent.putExtra(
                       CoachIntentConstants.LESSON_TIME, enterTimeButton.text.toString()
                    )

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

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.mainHolder.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.mainHolder.windowToken, 0)
            }

            true
        }
    }
}