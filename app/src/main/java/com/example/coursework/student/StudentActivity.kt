package com.example.coursework.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    private val adaptersList = convertAdaptersIntoList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db.open()
        couplesList = db.read() // Fuck this shit
        db.close()

        binding.apply {
            connectAdaptersToRcViews()

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

            buttonsClickListenersInit()

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

                            when (day) {
                                DaysConstants.MONDAY -> adaptersList[0].addCouple(couple)
                                DaysConstants.TUESDAY -> adaptersList[1].addCouple(couple)
                                DaysConstants.WEDNESDAY -> adaptersList[2].addCouple(couple)
                                DaysConstants.THURSDAY -> adaptersList[3].addCouple(couple)
                                DaysConstants.FRIDAY -> adaptersList[4].addCouple(couple)
                                DaysConstants.SATURDAY -> adaptersList[5].addCouple(couple)
                            }

                            val addedCouple = CoupleData(
                                coupleTitle, coupleTime, audienceNumber, day
                            )
                            couplesList.add(addedCouple)
                        }
                    }

                }
        }
    }

    override fun onStop() {
        super.onStop()

        db.open()
        db.repopulate(couplesList)
        db.close()
    }

    override fun onLayoutClick(couple: Couple) { // Edit method

    }

    override fun onTrashCanClick(couple: Couple) {

    }

    private fun convertAdaptersIntoList(): ArrayList<CoupleAdapter> {
        val mondayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )
        val tuesdayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )
        val wednesdayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )
        val thursdayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )
        val fridayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )
        val saturdayAdapter = CoupleAdapter(
            this@StudentActivity, this@StudentActivity
        )

        return arrayListOf(
            mondayAdapter, tuesdayAdapter, wednesdayAdapter, thursdayAdapter,
            fridayAdapter, saturdayAdapter
        )
    }

    private fun connectAdaptersToRcViews() {
        binding.apply {
            mondayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            mondayRcView.adapter = adaptersList[0]
            tuesdayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            tuesdayRcView.adapter = adaptersList[1]
            wednesdayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            wednesdayRcView.adapter = adaptersList[2]
            thursdayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            thursdayRcView.adapter = adaptersList[3]
            fridayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            fridayRcView.adapter = adaptersList[4]
            saturdayRcView.layoutManager = GridLayoutManager(this@StudentActivity, 1)
            saturdayRcView.adapter = adaptersList[5]
        }
    }

    private fun buttonsClickListenersInit() {
        binding.apply {
            openMondayButton.setOnClickListener { openInit(DaysConstants.MONDAY) }
            addMondayButton.setOnClickListener { startEditActivity(DaysConstants.MONDAY) }
            openTuesdayButton.setOnClickListener { openInit(DaysConstants.TUESDAY) }
            addTuesdayButton.setOnClickListener { startEditActivity(DaysConstants.TUESDAY) }
            openWednesdayButton.setOnClickListener { openInit(DaysConstants.WEDNESDAY) }
            addWednesdayButton.setOnClickListener { startEditActivity(DaysConstants.WEDNESDAY) }
            openThursdayButton.setOnClickListener { openInit(DaysConstants.THURSDAY) }
            addThursdayButton.setOnClickListener { startEditActivity(DaysConstants.THURSDAY) }
            openFridayButton.setOnClickListener { openInit(DaysConstants.FRIDAY) }
            addFridayButton.setOnClickListener { startEditActivity(DaysConstants.FRIDAY) }
            openSaturdayButton.setOnClickListener { openInit(DaysConstants.SATURDAY) }
            addSaturdayButton.setOnClickListener { startEditActivity(DaysConstants.SATURDAY) }
        }
    }

    private fun openInit(day: String) {
        binding.apply {
            when (day) {
                DaysConstants.MONDAY -> {
                    changeItemAppearance(
                        addMondayButton, mondayRcView, openMondayButton, DaysConstants.MONDAY
                    )
                }
                DaysConstants.TUESDAY -> {
                    changeItemAppearance(
                        addTuesdayButton, tuesdayRcView, openTuesdayButton, DaysConstants.TUESDAY
                    )
                }
                DaysConstants.WEDNESDAY -> {
                    changeItemAppearance(
                        addWednesdayButton, wednesdayRcView, openWednesdayButton, DaysConstants.WEDNESDAY
                    )
                }
                DaysConstants.THURSDAY -> {
                    changeItemAppearance(
                        addThursdayButton, thursdayRcView, openThursdayButton, DaysConstants.THURSDAY
                    )
                }
                DaysConstants.FRIDAY -> {
                    changeItemAppearance(
                        addFridayButton, fridayRcView, openFridayButton, DaysConstants.FRIDAY
                    )
                }
                DaysConstants.SATURDAY -> {
                    changeItemAppearance(
                        addSaturdayButton, saturdayRcView, openSaturdayButton, DaysConstants.SATURDAY
                    )
                }
            }
        }
    }

    private fun changeItemAppearance(
        addButton: ImageView, rcView: RecyclerView, openButton: ImageView, day: String
    ) {
        if (addButton.visibility == View.GONE) {
            rcView.visibility = View.VISIBLE
            addButton.visibility = View.VISIBLE
            openButton.setImageResource(R.drawable.arrow_up_icon)

            displayOnOpen(day)
        } else {
            rcView.visibility = View.GONE
            addButton.visibility = View.GONE
            openButton.setImageResource(R.drawable.arrow_down_icon)

            clearRcViewOnClose(day)
        }
    }

    private fun displayOnOpen(day: String) {
        for (couple in couplesList) {
            if (couple.day.equals(day)) {
                val showCouple = Couple(
                    couple.coupleTitle, couple.coupleTime, couple.audienceNumber
                )
                when (day) {
                    DaysConstants.MONDAY -> adaptersList[0].addCouple(showCouple)
                    DaysConstants.TUESDAY -> adaptersList[1].addCouple(showCouple)
                    DaysConstants.WEDNESDAY -> adaptersList[2].addCouple(showCouple)
                    DaysConstants.THURSDAY -> adaptersList[3].addCouple(showCouple)
                    DaysConstants.FRIDAY -> adaptersList[4].addCouple(showCouple)
                    DaysConstants.SATURDAY -> adaptersList[5].addCouple(showCouple)
                }
            }
        }
    }

    private fun clearRcViewOnClose(day: String) {
        var tempIndex = -1
        for (couple in couplesList) {
            if (couple.day.equals(day)) {
                tempIndex++
            }
        }

        val tempAdapter = when (day) {
            DaysConstants.MONDAY -> adaptersList[0]
            DaysConstants.TUESDAY -> adaptersList[1]
            DaysConstants.WEDNESDAY -> adaptersList[2]
            DaysConstants.THURSDAY -> adaptersList[3]
            DaysConstants.FRIDAY -> adaptersList[4]
            DaysConstants.SATURDAY -> adaptersList[5]
            else -> adaptersList[5]
        }

        if (tempIndex != -1) {
            for (item in tempIndex downTo 0) {
                tempAdapter.removeCouple(item)
            }
        }
    }

    private fun startEditActivity(day: String) {
        val intent = Intent(
            this@StudentActivity, EditCoupleInfoActivity::class.java
        )
        intent.putExtra(StudentIntentConstants.IS_EDIT, false)
        intent.putExtra(StudentIntentConstants.WHAT_DAY, day)
        editCoupleInfoLauncher.launch(intent)
    }
}