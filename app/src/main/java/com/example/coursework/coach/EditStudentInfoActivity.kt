package com.example.coursework.coach

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
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.databinding.ActivityEditStudentInfoV2Binding

class EditStudentInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditStudentInfoV2Binding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditStudentInfoV2Binding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        if (intent.getBooleanExtra(CoachIntentConstants.IS_EDIT, false)) {
            binding.editNamePlainTextView.setText(
                intent.getStringExtra(CoachIntentConstants.STUDENT_NAME)
            )
            binding.enterTimeText.text = (
                intent.getStringExtra(CoachIntentConstants.LESSON_TIME)
            )
            binding.editDescriptionTextView.setText(
                intent.getStringExtra(CoachIntentConstants.DESCRIPTION)
            )

            intent.putExtra(CoachIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(CoachIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            enterTimeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(this@EditStudentInfoActivity, { _, hour, minute ->
                    val time = String.format("%02d:%02d", hour, minute)
                    enterTimeText.text = time
                }, 0, 0, true)
                timePickerDialog.window?.setBackgroundDrawableResource(
                    R.color.grey1
                )

                timePickerDialog.show()

                val okButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                val cancelButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                okButton.setTextColor(
                    ContextCompat.getColor(this@EditStudentInfoActivity, R.color.grey5)
                )
                cancelButton.setTextColor(
                    ContextCompat.getColor(this@EditStudentInfoActivity, R.color.grey5)
                )
            }

            saveButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        CoachIntentConstants.STUDENT_NAME, editNamePlainTextView.text.toString()
                    )
                    intent.putExtra(
                        CoachIntentConstants.LESSON_TIME, enterTimeText.text.toString()
                    )
                    intent.putExtra(
                        CoachIntentConstants.DESCRIPTION,
                        editDescriptionTextView.text.toString().ifEmpty {
                            getString(R.string.no_desc_provided)
                        }
                    )

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
    }

    private fun completenessOfInformationTest(): Boolean {
        binding.apply {
            return if (editNamePlainTextView.text.toString().isEmpty()) {
                Toast.makeText(
                    this@EditStudentInfoActivity,
                    getString(R.string.name_field_is_empty),
                    Toast.LENGTH_SHORT
                ).show()

                false
            } else if (enterTimeText.text.toString() == getString(R.string.enter_time)) {
                Toast.makeText(
                    this@EditStudentInfoActivity,
                    getString(R.string.time_field_is_empty),
                    Toast.LENGTH_SHORT
                ).show()

                false
            } else {
                true
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