package com.example.coursework.coach

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
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
            binding.editTimePlainTextView.setText(
                intent.getStringExtra(CoachIntentConstants.LESSON_TIME)
            )

            intent.putExtra(CoachIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(CoachIntentConstants.IS_ADDED, true)
        }

        binding.apply {
            backButton.setOnClickListener {
                val intent = Intent(
                    this@EditStudentInfoActivity, CoachActivity::class.java
                )
                setResult(RESULT_CANCELED, intent)
                finish()
            }

            // TODO: Work on normal time type work(like if time = 123123 -> save is not working or smth)
            saveButton.setOnClickListener {
                if (editNamePlainTextView.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@EditStudentInfoActivity,
                        getString(R.string.name_field_is_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (editTimePlainTextView.text.toString().isEmpty()) {
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
                        CoachIntentConstants.LESSON_TIME, editTimePlainTextView.text.toString()
                    )

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
        }
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