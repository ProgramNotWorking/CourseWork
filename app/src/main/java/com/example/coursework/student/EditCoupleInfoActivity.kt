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
            intent.putExtra(StudentIntentConstants.IS_ADDED, false)
        } else {
            intent.putExtra(StudentIntentConstants.IS_ADDED, true)
        }

        binding.apply {
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
                        StudentIntentConstants.COUPLE_TIME, coupleTimeEditTextField.text.toString()
                    )
                    intent.putExtra(
                        StudentIntentConstants.AUDIENCE_NUMBER, audienceNumberEditTextField.text.toString()
                    )

                    setResult(RESULT_OK, intent)
                    finish()
                }
            }
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
}