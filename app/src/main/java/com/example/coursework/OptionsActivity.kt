package com.example.coursework

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityOptionsBinding
import com.example.coursework.db.DatabaseManager
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOptionsBinding

    private val db = DatabaseManager(this)

    private lateinit var from: String

    private val studentFirebase = FirebaseDatabase.getInstance().getReference("student")
    private val schoolkidFirebase = FirebaseDatabase.getInstance().getReference("schoolkid")

    @SuppressLint("ResourceAsColor")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        from = if (intent.getBooleanExtra(CoachIntentConstants.FROM_COACH, false))
            OptionsDataNames.COACH
        else if (intent.getBooleanExtra(StudentIntentConstants.FROM_STUDENT, false))
            OptionsDataNames.STUDENT
        else if (intent.getBooleanExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, false))
            OptionsDataNames.SCHOOLKID
        else
            OptionsDataNames.ITS_BAD

        binding.apply {
            when (from) {
                OptionsDataNames.COACH -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.coach_gradient)
                    currentTypeOfActivityTextView.text = getString(R.string.coach)
                }
                OptionsDataNames.STUDENT -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.student_gradient)
                    currentTypeOfActivityTextView.text = getString(R.string.student)
                }
                OptionsDataNames.SCHOOLKID -> {
                    currentTypeOfActivityImage.setImageResource(R.drawable.schoolkid_gradient)
                    currentTypeOfActivityTextView.text = getString(R.string.schoolkid)
                }
                OptionsDataNames.ITS_BAD ->
                    Log.d("From options:", "Trouble with image")
            }

            getInfoButton.setOnClickListener {
                showInfoDialogWindow()
            }

            loadTimetableButton.setOnClickListener {
                load()
            }

            clearDataTextView.setOnClickListener {
                showCleaningDialogWindow()
            }

            changeRoleTextView.setOnClickListener {
                val sharedPreferences = getSharedPreferences(
                    SharedPreferencesConstants.MAIN_KEY, Context.MODE_PRIVATE
                )
                val editor = sharedPreferences.edit()

                editor.putString(
                    SharedPreferencesConstants.ROLE_KEY,
                    SharedPreferencesConstants.DEFAULT_VALUE
                )
                editor.apply()

                val intent = Intent(
                    this@OptionsActivity, MainActivity::class.java
                )
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun load() {
        var test = "STUB!"

        if (binding.loadCodeInputField.text.toString().isEmpty()) {
            Toast.makeText(
                this@OptionsActivity,
                getString(R.string.code_field_is_empty),
                Toast.LENGTH_SHORT
            ).show()
        } else {
            if (binding.loadCodeInputField.text.toString() == "CAPYBARA") {
                val intent = Intent(
                    this@OptionsActivity, EasterEggActivity::class.java
                )
                startActivity(intent)
            } else {
                var isFindNeeded = false

                studentFirebase.addListenerForSingleValueEvent(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        for (code in snapshot.children) {
                            if (code.key == binding.loadCodeInputField.text.toString()) {
                                clearData()

                                val sharedPreferences = getSharedPreferences(
                                    SharedPreferencesConstants.LOAD_KEY, Context.MODE_PRIVATE
                                )
                                val editor = sharedPreferences.edit()

                                editor.putBoolean(
                                    SharedPreferencesConstants.IS_NEED_LOAD, true
                                )
                                editor.putString(
                                    SharedPreferencesConstants.LOAD_CODE,
                                    code.key
                                )
                                editor.apply()

                                isFindNeeded = true
                                binding.loadCodeInputField.text = null

                                break
                            }
                        }

                        if (!isFindNeeded) {
                            Toast.makeText(
                                this@OptionsActivity,
                                getString(R.string.wrong_code),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                this@OptionsActivity,
                                androidx.appcompat.R.string.abc_action_mode_done,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e("Firebase:", "Error with getting data")
                    }
                })
            }
        }
    }

    @SuppressLint("InflateParams")
    private fun showInfoDialogWindow() {
        val dialogView = LayoutInflater.from(this@OptionsActivity).inflate(
            R.layout.info_dialog_form, null
        )

        val dialog = AlertDialog.Builder(this@OptionsActivity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()
    }

    @SuppressLint("InflateParams")
    private fun showCleaningDialogWindow() {
        val dialogView = LayoutInflater.from(this@OptionsActivity).inflate(
            R.layout.dialog_window_for_data_cleaning, null
        )

        val dialog = AlertDialog.Builder(this@OptionsActivity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialogView.findViewById<TextView>(R.id.confirmClearingButton).setOnClickListener {
            clearData()

            Toast.makeText(
                this@OptionsActivity, getString(R.string.data_cleared), Toast.LENGTH_SHORT
            ).show()

            dialog.dismiss()
        }
        dialogView.findViewById<TextView>(R.id.refusalClearingButton).setOnClickListener {
            dialog.dismiss()
        }
        dialogView.findViewById<ImageView>(R.id.crossImageView).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goBack()
    }

    private fun clearData() {
        db.open()

        when (from) {
            OptionsDataNames.COACH -> db.clearCoachTable()
            OptionsDataNames.STUDENT -> db.clearStudentTable()
            OptionsDataNames.SCHOOLKID -> db.clearSchoolkidTable()
        }

        db.close()
    }

    private fun goBack() {
        when (from) {
            OptionsDataNames.COACH -> {
                val intent = Intent(
                    this@OptionsActivity, CoachActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            OptionsDataNames.STUDENT -> {
                val intent = Intent(
                    this@OptionsActivity, StudentActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            OptionsDataNames.SCHOOLKID -> {
                val intent = Intent(
                    this@OptionsActivity, SchoolkidActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.optionsMainHolder.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.optionsMainHolder.windowToken, 0)
            }

            true
        }
    }
}

object OptionsDataNames {
    const val COACH = "Coach/Teacher"
    const val STUDENT = "Student"
    const val SCHOOLKID = "Schoolkid"

    const val ITS_BAD = "Error"
}