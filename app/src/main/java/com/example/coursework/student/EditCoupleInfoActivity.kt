package com.example.coursework.student

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.app.TimePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
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
            binding.enterCoupleTimeText.text = (
                intent.getStringExtra(StudentIntentConstants.COUPLE_TIME)
            )
            binding.audienceNumberEditTextField.setText(
                intent.getStringExtra(StudentIntentConstants.AUDIENCE_NUMBER)
            )
            binding.teacherEditTextField.setText(
                intent.getStringExtra(StudentIntentConstants.TEACHER_NAME)
            )

            intent.putExtra(StudentIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(StudentIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            dayTitleCoupleTextView.text = setDayText()

            enterCoupleTimeButton.setOnClickListener {
                val timePickerDialog = TimePickerDialog(
                    this@EditCoupleInfoActivity,
                    { _, hour, minute ->
                        val endTimePickerDialog = TimePickerDialog(
                            this@EditCoupleInfoActivity,
                            { _, endHour, endMinute ->
                                val timeRange = String.format("%02d:%02d-%02d:%02d", hour, minute, endHour, endMinute)
                                enterCoupleTimeText.text = timeRange
                            },
                            hour,
                            minute,
                            true
                        )
                        endTimePickerDialog.window?.setBackgroundDrawableResource(
                            R.color.grey2
                        )

                        endTimePickerDialog.show()

                        val okButton = endTimePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                        val cancelButton = endTimePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                        okButton.setTextColor(
                            ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.grey5)
                        )
                        cancelButton.setTextColor(
                            ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.grey5)
                        )
                    },
                    12,
                    0,
                    true
                )

                timePickerDialog.window?.setBackgroundDrawableResource(
                    R.color.grey2
                )

                timePickerDialog.show()

                val okButton = timePickerDialog.getButton(Dialog.BUTTON_POSITIVE)
                val cancelButton = timePickerDialog.getButton(Dialog.BUTTON_NEGATIVE)

                okButton.setTextColor(
                    ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.grey5)
                )
                cancelButton.setTextColor(
                    ContextCompat.getColor(this@EditCoupleInfoActivity, R.color.grey5)
                )
            }

            coupleInfoEditButton.setOnClickListener {
                showCoupleAudienceOptionsDialog()
            }

            saveCoupleButton.setOnClickListener {
                if (completenessOfInformationTest()) {
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TITLE, coupleTitleEditTextField.text.toString()
                    )
                    intent.putExtra(
                        StudentIntentConstants.COUPLE_TIME, enterCoupleTimeText.text.toString()
                    )
                    if (audienceNumberEditTextField.text.toString().isEmpty()) {
                        intent.putExtra(
                            StudentIntentConstants.AUDIENCE_NUMBER, getString(R.string.not_specified)
                        )
                    } else {
                        intent.putExtra(
                            StudentIntentConstants.AUDIENCE_NUMBER, audienceNumberEditTextField.text.toString()
                        )
                    }
                    if (teacherEditTextField.text.toString().isEmpty()) {
                        intent.putExtra(
                            StudentIntentConstants.TEACHER_NAME, getString(R.string.teacher_not_specified)
                        )
                    } else {
                        intent.putExtra(
                            StudentIntentConstants.TEACHER_NAME, teacherEditTextField.text.toString()
                        )
                    }
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

    private fun showCoupleAudienceOptionsDialog() {
        val dialogView = LayoutInflater.from(this@EditCoupleInfoActivity).inflate(
            R.layout.dialog_window_for_couple_audience, null
        )

        val dialog = AlertDialog.Builder(this@EditCoupleInfoActivity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<TextView>(R.id.annexeButton).setOnClickListener {
            var text = binding.audienceNumberEditTextField.text.toString()
            text += getString(R.string.annex_reduction)

            binding.audienceNumberEditTextField.setText(text)

            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.distanceButton).setOnClickListener {
            binding.audienceNumberEditTextField.setText(R.string.distance_couple)

            dialog.dismiss()
        }

        dialogView.findViewById<TextView>(R.id.notSpecifiedButton).setOnClickListener {
            binding.audienceNumberEditTextField.setText(getString(R.string.not_specified))

            dialog.dismiss()
        }

        dialogView.findViewById<ImageView>(R.id.closeEditCoupleDialogButton).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun completenessOfInformationTest(): Boolean {
        binding.apply {
            return if (coupleTitleEditTextField.text.toString().isEmpty()) {
                showText(getString(R.string.couple_title_field_is_empty))

                false
            } else if (enterCoupleTimeText.text.toString() == getString(R.string.enter_couple_time)) {
                showText(getString(R.string.couple_time_field_is_empty))

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