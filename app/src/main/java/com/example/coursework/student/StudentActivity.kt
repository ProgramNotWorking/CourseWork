package com.example.coursework.student

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.animation.*
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.*
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.databinding.ActivityStudentBinding
import com.example.coursework.db.DatabaseManager
import java.time.LocalTime

class StudentActivity : AppCompatActivity(),
    CoupleAdapter.OnLayoutClickListener, CoupleAdapter.OnTrashCanClickListener,
    OnAnimationsDataReceivedListener {
    private lateinit var binding: ActivityStudentBinding

    private lateinit var couplesList: MutableList<CoupleData>

    private val db = DatabaseManager(this)
    private val databaseHelper = DatabaseHelperClass(db)
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
    private lateinit var animationController: LayoutAnimationController
    private lateinit var addButtonRotateAnimation: RotateAnimation
    private lateinit var addButtonRotationOutAnimation: RotateAnimation

    private val addButtonAnimationSet = AnimationSet(true)
    private val addButtonOutAnimationSet = AnimationSet(true)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAnimationHelperClass()

        addButtonAnimationSet.addAnimation(alphaInAnimation)
        addButtonAnimationSet.addAnimation(addButtonRotateAnimation)

        addButtonOutAnimationSet.addAnimation(alphaOutAnimations)
        addButtonOutAnimationSet.addAnimation(addButtonRotationOutAnimation)

        rcViewsList = getRcViewsList()
        couplesList = databaseHelper.getStudentData()

        binding.apply {
            connectAdaptersToRcViews()

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        databaseHelper.saveData(SharedPreferencesConstants.STUDENT, couplesList, null)

                        val intent = Intent(
                            this@StudentActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(StudentIntentConstants.FROM_STUDENT, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        databaseHelper.saveData(SharedPreferencesConstants.STUDENT, couplesList, null)

                        // TODO: YEAH
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

                            clearRcViewOnClose(dayIntent!!)
                            displayOnOpen(dayIntent)
                        } else {
                            for (couple in couplesList.indices) {
                                if (
                                    couplesList[couple].coupleTitle.equals(editCoupleTitle) &&
                                    couplesList[couple].coupleTime.equals(editCoupleTime) &&
                                    couplesList[couple].audienceNumber.equals(editAudienceNumber) &&
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

                            clearRcViewOnClose(dayIntent!!)
                            displayOnOpen(dayIntent)
                        }
                    }

                }
        }
    }

    override fun onStop() {
        super.onStop()

        databaseHelper.saveData(SharedPreferencesConstants.STUDENT, couplesList, null)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
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

    @RequiresApi(Build.VERSION_CODES.O)
    private fun changeItemAppearance(
        addButton: ImageView, rcView: RecyclerView, openButton: ImageView, day: String
    ) {
        if (addButton.visibility == View.GONE) {
            addButton.startAnimation(addButtonAnimationSet)
            addButton.visibility = View.VISIBLE

            openButton.startAnimation(rotateAnimation)
            openButton.setImageResource(R.drawable.arrow_up_icon)

            rcView.visibility = View.VISIBLE

            rcView.layoutAnimation = animationController
            rcView.scheduleLayoutAnimation()

            displayOnOpen(day)
        } else {
            addButton.startAnimation(addButtonOutAnimationSet)
            addButton.visibility = View.GONE

            openButton.startAnimation(rotateBackAnimation)
            openButton.setImageResource(R.drawable.arrow_down_icon)

            rcView.visibility = View.GONE

            clearRcViewOnClose(day)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayOnOpen(day: String) {
        couplesList.sortBy {
            LocalTime.parse(it.coupleTime)
        }

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

    private fun openAnimationHelperClass() {
        val animationsHelperClass = AnimationsHelperClass(this@StudentActivity)
        animationsHelperClass.setAnimationsDataReceivedListener(this@StudentActivity)

        animationsHelperClass.headersAnimationInit(headersList = arrayListOf(
            binding.mondayHeader, binding.tuesdayHeader, binding.wednesdayHeader,
            binding.thursdayHeader, binding.fridayHeader, binding.saturdayHeader
        ))
    }

    override fun rotateInit(
        rotate: RotateAnimation,
        rotateBack: RotateAnimation,
        addButtonRotate: RotateAnimation,
        addButtonBackRotation: RotateAnimation
    ) {
        rotateAnimation = rotate
        rotateBackAnimation = rotateBack
        addButtonRotateAnimation = addButtonRotate
        addButtonRotationOutAnimation = addButtonBackRotation
    }

    override fun alphaInit(alphaIn: AlphaAnimation, alphaOut: AlphaAnimation) {
        alphaInAnimation = alphaIn
        alphaOutAnimations = alphaOut
    }

    override fun layoutAnimationInit(controller: LayoutAnimationController) {
        animationController = controller
    }
}