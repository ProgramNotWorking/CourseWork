package com.example.coursework.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coursework.OptionsActivity
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityStudentBinding
import com.example.coursework.student.db.StudentDatabaseManager

class StudentActivity : AppCompatActivity(),
    CoupleAdapter.OnLayoutClickListener, CoupleAdapter.OnTrashCanClickListener {
    private lateinit var binding: ActivityStudentBinding

    private lateinit var couplesList: MutableList<CoupleData>

    private val db = StudentDatabaseManager(this)
    private lateinit var editCoupleInfoLauncher: ActivityResultLauncher<Intent>

    private val mondayAdapter = CoupleAdapter(
        this@StudentActivity, this@StudentActivity
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db.open()
        couplesList = db.read()
        db.close()

        binding.apply {
            mondayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            mondayRcView.adapter = mondayAdapter

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        // TODO: make saveData() method there

                        val intent = Intent(
                            this@StudentActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(StudentIntentConstants.FROM_STUDENT, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        // TODO: YEAH

                        Toast.makeText(
                            this@StudentActivity, "SUS?", Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                true
            }

            openMondayButton.setOnClickListener {
                if (addMondayButton.visibility == View.GONE) {
                    mondayRcView.visibility = View.VISIBLE
                    addMondayButton.visibility = View.VISIBLE
                    openMondayButton.setImageResource(R.drawable.arrow_up_icon)

                    for (couple in couplesList) {
                        if (couple.day.equals(DaysConstants.MONDAY)) {
                            val addedCouple = Couple(
                                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
                            )
                            mondayAdapter.addCouple(addedCouple)
                        }
                    }
                } else {
                    mondayRcView.visibility = View.GONE
                    addMondayButton.visibility = View.GONE
                    openMondayButton.setImageResource(R.drawable.arrow_down_icon)

                    var tempIndex = -1
                    for (couple in couplesList) {
                        if (couple.day.equals(DaysConstants.MONDAY)) {
                            tempIndex++
                        }
                    }

                    if (tempIndex != -1) {
                        for (item in tempIndex downTo 0) {
                            mondayAdapter.removeCouple(item)
                        }
                    }
                }
            }

            addMondayButton.setOnClickListener {
                val intent = Intent(
                    this@StudentActivity, EditCoupleInfoActivity::class.java
                )
                intent.putExtra(StudentIntentConstants.IS_EDIT, false)
                intent.putExtra(StudentIntentConstants.WHAT_DAY, DaysConstants.MONDAY)
                editCoupleInfoLauncher.launch(intent)
            }

            editCoupleInfoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        intent = result.data

                        if (intent.getBooleanExtra(StudentIntentConstants.IS_ADDED, false)) {
                            val coupleTitle = intent.getStringExtra(StudentIntentConstants.COUPLE_TITLE)
                            val coupleTime = intent.getStringExtra(StudentIntentConstants.COUPLE_TIME)
                            val audienceNumber = intent.getStringExtra(StudentIntentConstants.AUDIENCE_NUMBER)
                            val day = intent.getStringExtra(StudentIntentConstants.WHAT_DAY)

                            val couple = Couple(
                                coupleTitle, coupleTime, audienceNumber
                            )
                            mondayAdapter.addCouple(couple)

                            val addedCouple = CoupleData(
                                coupleTitle, coupleTime, audienceNumber, day
                            )
                            couplesList.add(addedCouple)
                        }
                    }

                }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

        db.open()
        db.repopulate(couplesList)
        db.close()
    }

    override fun onLayoutClick(couple: Couple) {

    }

    override fun onTrashCanClick(couple: Couple) {

    }
}