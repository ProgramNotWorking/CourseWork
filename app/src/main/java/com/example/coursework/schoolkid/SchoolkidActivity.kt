package com.example.coursework.schoolkid

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
import com.example.coursework.alldays.AllDaysActivity
import com.example.coursework.constants.*
import com.example.coursework.databinding.ActivitySchoolkidBinding
import com.example.coursework.db.DatabaseManager
import com.example.coursework.helpers.AnimationsHelperClass
import com.example.coursework.helpers.DatabaseHelperClass
import com.example.coursework.helpers.StudentsHelper
import com.example.coursework.search_system.SearchActivity
import java.time.LocalTime

class SchoolkidActivity : AppCompatActivity(),
    SchoolAdapter.OnLayoutClickListener, SchoolAdapter.OnTrashCanClickListener,
    OnAnimationsDataReceivedListener {
    private lateinit var binding: ActivitySchoolkidBinding

    private lateinit var editLessonInfoLauncher: ActivityResultLauncher<Intent>

    private lateinit var lessonsList: MutableList<LessonData>

    private val adaptersList = convertAdaptersIntoList()
    private lateinit var rcViewsList: ArrayList<RecyclerView>

    private val db = DatabaseManager(this)
    private val databaseHelper = DatabaseHelperClass(db)

    private var editLessonTitle = "STUB!"
    private var editLessonTime = "STUB!"
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

    private val helper = StudentsHelper(this@SchoolkidActivity)

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolkidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAnimationHelperClass()

        addButtonAnimationSet.addAnimation(alphaInAnimation)
        addButtonAnimationSet.addAnimation(addButtonRotateAnimation)

        addButtonOutAnimationSet.addAnimation(alphaOutAnimations)
        addButtonOutAnimationSet.addAnimation(addButtonRotationOutAnimation)

        rcViewsList = getRcViewsList()
        lessonsList = databaseHelper.getSchoolkidData()

        binding.apply {
            connectAdapterToRcViews()

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        databaseHelper.saveData(
                            SharedPreferencesConstants.SCHOOLKID, null,
                            lessonsList, null
                        )

                        val intent = Intent(
                            this@SchoolkidActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        databaseHelper.saveData(
                            SharedPreferencesConstants.SCHOOLKID, null,
                            lessonsList, null
                        )

                        val intent = Intent(
                            this@SchoolkidActivity, AllDaysActivity::class.java
                        )

                        val titlesList = ArrayList<String>()
                        val timesList = ArrayList<String>()
                        val audiencesList = ArrayList<String>()
                        val daysList = ArrayList<String>()

                        for (lesson in lessonsList) {
                            lesson.lessonTitle?.let { it1 -> titlesList.add(it1) }
                            lesson.lessonTime?.let { it2 -> timesList.add(it2) }
                            lesson.audienceNumber?.let { it3 -> audiencesList.add(it3) }
                            lesson.day?.let { it4 -> daysList.add(it4) }
                        }

                        intent.putExtra(AllDaysConstants.TITLE, titlesList)
                        intent.putExtra(AllDaysConstants.TIME, timesList)
                        intent.putExtra(AllDaysConstants.AUDIENCE, audiencesList)
                        intent.putExtra(AllDaysConstants.DAY, daysList)

                        startActivity(intent)
                    }
                    R.id.search_bottom_nav -> {
                        val intent = Intent(
                            this@SchoolkidActivity, SearchActivity::class.java
                        )
                        intent.putExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, true)

                        val titlesList = ArrayList<String>()
                        val timesList = ArrayList<String>()
                        val audiencesList = ArrayList<String>()
                        val daysList = ArrayList<String>()

                        for (lesson in lessonsList) {
                            lesson.lessonTitle?.let { it1 -> titlesList.add(it1) }
                            lesson.lessonTime?.let { it2 -> timesList.add(it2) }
                            lesson.audienceNumber?.let { it3 -> audiencesList.add(it3) }
                            lesson.day?.let { it4 -> daysList.add(it4) }
                        }

                        intent.putExtra(SearchIntentConstants.TITLES_LIST, titlesList)
                        intent.putExtra(SearchIntentConstants.TIMES_LIST, timesList)
                        intent.putExtra(SearchIntentConstants.AUDIENCES_LIST, audiencesList)
                        intent.putExtra(SearchIntentConstants.DAYS_LIST, daysList)

                        startActivity(intent)
                    }
                }

                true
            }

            buttonsClickListenersInit()

            editLessonInfoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                    intent = result.data

                    if (result.resultCode == RESULT_OK) {
                        val dayIntent = helper.getSchoolkidResult(
                            intent,
                            adaptersList,
                            lessonsList,
                            editLessonTitle,
                            editLessonTime,
                            editAudienceNumber
                        )

                        clearRcViewOnClose(dayIntent!!)
                        displayOnOpen(dayIntent)
                    }
                }
        }
    }

    override fun onStop() {
        super.onStop()

        databaseHelper.saveData(
            SharedPreferencesConstants.SCHOOLKID, null,
            lessonsList, null
        )
    }

    override fun onLayoutClick(lesson: SchoolLesson) {
        editLessonTitle = lesson.lessonTitle.toString()
        editLessonTime = lesson.lessonTitle.toString()
        editAudienceNumber = lesson.audienceNumber.toString()

        helper.startEdit(
            false, editLessonInfoLauncher, null, lesson
        )
    }

    override fun onTrashCanClick(lesson: SchoolLesson) {
        helper.deleteLesson(adaptersList, lesson, lessonsList)
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
        lessonsList.sortBy {
            LocalTime.parse(it.lessonTime)
        }

        for (lesson in lessonsList) {
            if (lesson.day.equals(day)) {
                val showLesson = SchoolLesson(
                    lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber, lesson.day
                )
                when (day) {
                    DaysConstants.MONDAY -> adaptersList[0].addLesson(showLesson)
                    DaysConstants.TUESDAY -> adaptersList[1].addLesson(showLesson)
                    DaysConstants.WEDNESDAY -> adaptersList[2].addLesson(showLesson)
                    DaysConstants.THURSDAY -> adaptersList[3].addLesson(showLesson)
                    DaysConstants.FRIDAY -> adaptersList[4].addLesson(showLesson)
                    DaysConstants.SATURDAY -> adaptersList[5].addLesson(showLesson)
                }
            }
        }
    }

    private fun clearRcViewOnClose(day: String) {
        var tempIndex = -1
        for (lesson in lessonsList) {
            if (lesson.day.equals(day)) {
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
                tempAdapter.removeLesson(item)
            }
        }
    }

    private fun startEditActivity(day: String) {
        val intent = Intent(
            this@SchoolkidActivity, EditLessonInfoActivity::class.java
        )
        intent.putExtra(SchoolkidIntentConstants.IS_EDIT, false)
        intent.putExtra(SchoolkidIntentConstants.WHAT_DAY, day)
        editLessonInfoLauncher.launch(intent)
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

    private fun convertAdaptersIntoList(): ArrayList<SchoolAdapter> {
        val mondayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )
        val tuesdayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )
        val wednesdayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )
        val thursdayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )
        val fridayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )
        val saturdayAdapter = SchoolAdapter(
            this@SchoolkidActivity, this@SchoolkidActivity
        )

        return arrayListOf(
            mondayAdapter, tuesdayAdapter, wednesdayAdapter, thursdayAdapter,
            fridayAdapter, saturdayAdapter
        )
    }

    private fun connectAdapterToRcViews() {
        binding.apply {
            mondayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            mondayRcView.adapter = adaptersList[0]
            tuesdayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            tuesdayRcView.adapter = adaptersList[1]
            wednesdayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            wednesdayRcView.adapter = adaptersList[2]
            thursdayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            thursdayRcView.adapter = adaptersList[3]
            fridayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            fridayRcView.adapter = adaptersList[4]
            saturdayRcView.layoutManager = GridLayoutManager(this@SchoolkidActivity, 1)
            saturdayRcView.adapter = adaptersList[5]
        }
    }

    private fun openAnimationHelperClass() {
        val animationsHelperClass = AnimationsHelperClass(this@SchoolkidActivity)
        animationsHelperClass.setAnimationsDataReceivedListener(this@SchoolkidActivity)

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