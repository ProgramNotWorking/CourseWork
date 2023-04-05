package com.example.coursework.student

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.view.animation.TranslateAnimation
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.iterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.OptionsActivity
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityStudentBinding
import com.example.coursework.db.DatabaseManager

class StudentActivity : AppCompatActivity(),
    CoupleAdapter.OnLayoutClickListener, CoupleAdapter.OnTrashCanClickListener, Animation.AnimationListener {
    private lateinit var binding: ActivityStudentBinding

    private lateinit var couplesList: MutableList<CoupleData>

    private val db = DatabaseManager(this)
    private lateinit var editCoupleInfoLauncher: ActivityResultLauncher<Intent>

    private val adaptersList = convertAdaptersIntoList()
    private lateinit var rcViewsList: ArrayList<RecyclerView>

    private var editCoupleTitle = "STUB!"
    private var editCoupleTime = "STUB!"
    private var editAudienceNumber = "STUB!"

    private lateinit var rotateAnimation: RotateAnimation
    private lateinit var rotateBackAnimation: RotateAnimation
    private lateinit var alphaInAnimation: AlphaAnimation
    private lateinit var alphaOutAnimations: AlphaAnimation
    private lateinit var slideInAnimation: TranslateAnimation
    private lateinit var slideOutAnimation: TranslateAnimation

    private var whatDay = "STUB!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        animationsInit()

        rcViewsList = getRcViewsList()
        couplesList = getData()

        binding.apply {
            connectAdaptersToRcViews()

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        saveData()

                        val intent = Intent(
                            this@StudentActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(StudentIntentConstants.FROM_STUDENT, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        saveData()

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

                        val coupleTitleIntent = intent.getStringExtra(StudentIntentConstants.COUPLE_TITLE)
                        val coupleTimeIntent = intent.getStringExtra(StudentIntentConstants.COUPLE_TIME)
                        val audienceNumberIntent = intent.getStringExtra(StudentIntentConstants.AUDIENCE_NUMBER)
                        val dayIntent = intent.getStringExtra(StudentIntentConstants.WHAT_DAY)

                        if (intent.getBooleanExtra(StudentIntentConstants.IS_ADDED, false)) {
                            val couple = Couple(
                                coupleTitleIntent, coupleTimeIntent, audienceNumberIntent, dayIntent
                            )

                            when (dayIntent) {
                                DaysConstants.MONDAY -> adaptersList[0].addCouple(couple)
                                DaysConstants.TUESDAY -> adaptersList[1].addCouple(couple)
                                DaysConstants.WEDNESDAY -> adaptersList[2].addCouple(couple)
                                DaysConstants.THURSDAY -> adaptersList[3].addCouple(couple)
                                DaysConstants.FRIDAY -> adaptersList[4].addCouple(couple)
                                DaysConstants.SATURDAY -> adaptersList[5].addCouple(couple)
                            }

                            val addedCouple = CoupleData(
                                coupleTitleIntent, coupleTimeIntent, audienceNumberIntent, dayIntent
                            )
                            couplesList.add(addedCouple)
                        } else {
                            for (couple in couplesList.indices) {
                                if (
                                    couplesList[couple].coupleTitle.equals(coupleTitleIntent) &&
                                    couplesList[couple].coupleTime.equals(coupleTimeIntent) &&
                                    couplesList[couple].audienceNumber.equals(audienceNumberIntent) &&
                                    couplesList[couple].day.equals(dayIntent)
                                ) {
                                    val editedCouple = CoupleData(
                                        coupleTitleIntent, coupleTimeIntent,
                                        audienceNumberIntent, dayIntent
                                    )
                                    couplesList[couple] = editedCouple

                                    break
                                }
                            }

                            val neededRcViewIndex = when (dayIntent) {
                                DaysConstants.MONDAY -> 0
                                DaysConstants.TUESDAY -> 1
                                DaysConstants.WEDNESDAY -> 2
                                DaysConstants.THURSDAY -> 3
                                DaysConstants.FRIDAY -> 4
                                DaysConstants.SATURDAY -> 5
                                else -> -1
                            }

                            for (item in rcViewsList[neededRcViewIndex]) {
                                if (item.findViewById<TextView>(R.id.coupleTitleTextViewItem).text.toString()
                                        == editCoupleTitle &&
                                    item.findViewById<TextView>(R.id.coupleTimeTextViewItem).text.toString()
                                        == editCoupleTime &&
                                    item.findViewById<TextView>(R.id.audienceTextView).text.toString()
                                        == editAudienceNumber) {

                                    item.findViewById<TextView>(
                                        R.id.coupleTitleTextViewItem
                                    ).text = coupleTitleIntent
                                    item.findViewById<TextView>(
                                        R.id.coupleTimeTextViewItem
                                    ).text = coupleTimeIntent
                                    item.findViewById<TextView>(
                                        R.id.audienceTextView
                                    ).text = audienceNumberIntent
                                }
                            }
                        }
                    }

                }
        }
    }

    override fun onStop() {
        super.onStop()

        saveData()
    }

    private fun saveData() {
        db.open()
        db.repopulateStudent(couplesList)
        db.close()
    }

    private fun getData(): MutableList<CoupleData> {
        db.open()
        val data = db.readStudent()
        db.close()

        return data
    }

    override fun onLayoutClick(couple: Couple) {
        val intent = Intent(
            this@StudentActivity, EditCoupleInfoActivity::class.java
        )
        intent.putExtra(StudentIntentConstants.COUPLE_TITLE, couple.coupleTitle)
        intent.putExtra(StudentIntentConstants.COUPLE_TIME, couple.coupleTime)
        intent.putExtra(StudentIntentConstants.AUDIENCE_NUMBER, couple.audienceNumber)
        intent.putExtra(StudentIntentConstants.WHAT_DAY, couple.day)
        intent.putExtra(StudentIntentConstants.IS_EDIT, true)

        editCoupleTitle = couple.coupleTitle.toString()
        editCoupleTime = couple.coupleTime.toString()
        editAudienceNumber = couple.audienceNumber.toString()

        editCoupleInfoLauncher.launch(intent)
    }

    override fun onTrashCanClick(couple: Couple) {
        when (couple.day) {
            DaysConstants.MONDAY -> adaptersList[0].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
            DaysConstants.TUESDAY -> adaptersList[1].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
            DaysConstants.WEDNESDAY -> adaptersList[2].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
            DaysConstants.THURSDAY -> adaptersList[3].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
            DaysConstants.FRIDAY -> adaptersList[4].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
            DaysConstants.SATURDAY -> adaptersList[5].removeCoupleByData(
                couple.coupleTitle, couple.coupleTime, couple.audienceNumber
            )
        }

        for (item in couplesList.indices) {
            if (
                couplesList[item].coupleTitle.equals(couple.coupleTitle) &&
                couplesList[item].coupleTime.equals(couple.coupleTime) &&
                couplesList[item].audienceNumber.equals(couple.audienceNumber) &&
                couplesList[item].day.equals(couple.day)
            ) {
                couplesList.removeAt(item)
                break
            }
        }
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

    private fun getRcViewsList(): ArrayList<RecyclerView> {
        binding.apply {
            return arrayListOf(
                mondayRcView,
                tuesdayRcView,
                wednesdayRcView,
                thursdayRcView,
                fridayRcView,
                saturdayRcView
            )
        }
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
                        addMondayButton, mondayRcView, openMondayButton,
                        DaysConstants.MONDAY
                    )
                }
                DaysConstants.TUESDAY -> {
                    changeItemAppearance(
                        addTuesdayButton, tuesdayRcView, openTuesdayButton,
                        DaysConstants.TUESDAY
                    )
                }
                DaysConstants.WEDNESDAY -> {
                    changeItemAppearance(
                        addWednesdayButton, wednesdayRcView, openWednesdayButton,
                        DaysConstants.WEDNESDAY
                    )
                }
                DaysConstants.THURSDAY -> {
                    changeItemAppearance(
                        addThursdayButton, thursdayRcView, openThursdayButton,
                        DaysConstants.THURSDAY
                    )
                }
                DaysConstants.FRIDAY -> {
                    changeItemAppearance(
                        addFridayButton, fridayRcView, openFridayButton,
                        DaysConstants.FRIDAY
                    )
                }
                DaysConstants.SATURDAY -> {
                    changeItemAppearance(
                        addSaturdayButton, saturdayRcView, openSaturdayButton,
                        DaysConstants.SATURDAY
                    )
                }
            }
        }
    }

    private fun changeItemAppearance(
        addButton: ImageView, rcView: RecyclerView, openButton: ImageView, day: String
    ) {
        slideAnimationInit(day)
        whatDay = day

        if (addButton.visibility == View.GONE) {
            addButton.startAnimation(alphaInAnimation)
            addButton.visibility = View.VISIBLE

            openButton.startAnimation(rotateAnimation)
            openButton.setImageResource(R.drawable.arrow_up_icon)

            rcView.startAnimation(slideInAnimation)

            displayOnOpen(day)
        } else {
            addButton.startAnimation(alphaOutAnimations)
            addButton.visibility = View.GONE

            openButton.startAnimation(rotateBackAnimation)
            openButton.setImageResource(R.drawable.arrow_down_icon)

            clearRcViewOnClose(day)

            rcView.startAnimation(slideOutAnimation)
            rcView.visibility = View.GONE
        }
    }

    private fun displayOnOpen(day: String) {
        for (couple in couplesList) {
            if (couple.day.equals(day)) {
                val showCouple = Couple(
                    couple.coupleTitle, couple.coupleTime, couple.audienceNumber, couple.day
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

    private fun animationsInit() {
        rotateAnimation = RotateAnimation(
            180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateAnimation.duration = 500

        rotateBackAnimation = RotateAnimation(
            -180f,
            0f,
            Animation.RELATIVE_TO_SELF,
            0.5f,
            Animation.RELATIVE_TO_SELF,
            0.5f
        )
        rotateBackAnimation.duration = 500

        alphaInAnimation = AlphaAnimation(0f, 1f)
        alphaInAnimation.duration = 500
        alphaOutAnimations = AlphaAnimation(1f, 0f)
        alphaOutAnimations.duration = 500
    }

    private fun slideAnimationInit(day: String) {
        val neededRcView = when (day) {
            DaysConstants.MONDAY -> binding.mondayRcView
            DaysConstants.TUESDAY -> binding.tuesdayRcView
            DaysConstants.WEDNESDAY -> binding.wednesdayRcView
            DaysConstants.THURSDAY -> binding.thursdayRcView
            DaysConstants.FRIDAY -> binding.fridayRcView
            DaysConstants.SATURDAY -> binding.saturdayRcView
            else -> binding.saturdayRcView
        }

        val neededButton = when (day) {
            DaysConstants.MONDAY -> binding.openMondayButton
            DaysConstants.TUESDAY -> binding.openTuesdayButton
            DaysConstants.WEDNESDAY -> binding.openWednesdayButton
            DaysConstants.THURSDAY -> binding.openThursdayButton
            DaysConstants.FRIDAY -> binding.openFridayButton
            DaysConstants.SATURDAY -> binding.openSaturdayButton
            else -> binding.openSaturdayButton
        }

        Log.d("Test height:", neededRcView.height.toFloat().toString())

        slideInAnimation = TranslateAnimation(
            0f,
            0f,
            -neededRcView.height.toFloat(),
            -neededButton.y
        )
        slideInAnimation.duration = 500

        slideInAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(p0: Animation?) {
                neededRcView.visibility = View.VISIBLE
            }
            override fun onAnimationEnd(p0: Animation?) {
                // displayOnOpen(day) // TODO: Think about this
            }
            override fun onAnimationRepeat(p0: Animation?) {}
        })

        slideOutAnimation = TranslateAnimation(
            0f,
            0f,
            neededRcView.height.toFloat(),
            0f
        )
        slideOutAnimation.duration = 500
    }

    override fun onAnimationStart(p0: Animation?) {

    }

    override fun onAnimationEnd(p0: Animation?) {

    }

    override fun onAnimationRepeat(p0: Animation?) {

    }
}