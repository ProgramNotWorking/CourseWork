package com.example.coursework.schoolkid

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.iterator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.OptionsActivity
import com.example.coursework.R
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.databinding.ActivitySchoolkidBinding
import com.example.coursework.db.DatabaseManager

class SchoolkidActivity : AppCompatActivity(),
    SchoolAdapter.OnLayoutClickListener, SchoolAdapter.OnTrashCanClickListener {
    private lateinit var binding: ActivitySchoolkidBinding

    private lateinit var editLessonInfoLauncher: ActivityResultLauncher<Intent>

    private lateinit var lessonsList: MutableList<LessonData>

    private val adaptersList = convertAdaptersIntoList()
    private lateinit var rcViewsList: ArrayList<RecyclerView>

    private val db = DatabaseManager(this)

    private var editLessonTitle = "STUB!"
    private var editLessonTime = "STUB!"
    private var editAudienceNumber = "STUB!"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySchoolkidBinding.inflate(layoutInflater)
        setContentView(binding.root)

        rcViewsList = getRcViewsList()
        lessonsList = getData()

        binding.apply {
            connectAdapterToRcViews()

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        saveData()

                        val intent = Intent(
                            this@SchoolkidActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        saveData()

                        // YEAH YEAH YEAH YEAH YEAH YEAH YEAH
                    }
                }

                true
            }

            buttonsClickListenersInit()

            editLessonInfoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                    result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        intent = result.data

                        val lessonTitleIntent = intent.getStringExtra(
                            SchoolkidIntentConstants.LESSON_TITLE
                        )
                        val lessonTimeIntent = intent.getStringExtra(
                            SchoolkidIntentConstants.LESSON_TIME
                        )
                        val audienceNumberIntent = intent.getStringExtra(
                            SchoolkidIntentConstants.AUDIENCE_NUMBER
                        )
                        val dayIntent = intent.getStringExtra(
                            SchoolkidIntentConstants.WHAT_DAY
                        )

                        if (intent.getBooleanExtra(SchoolkidIntentConstants.IS_ADDED, false)) {
                            val lesson = SchoolLesson(
                                lessonTitleIntent, lessonTimeIntent, audienceNumberIntent, dayIntent
                            )

                            when (dayIntent) {
                                DaysConstants.MONDAY -> adaptersList[0].addLesson(lesson)
                                DaysConstants.TUESDAY -> adaptersList[1].addLesson(lesson)
                                DaysConstants.WEDNESDAY -> adaptersList[2].addLesson(lesson)
                                DaysConstants.THURSDAY -> adaptersList[3].addLesson(lesson)
                                DaysConstants.FRIDAY -> adaptersList[4].addLesson(lesson)
                                DaysConstants.SATURDAY -> adaptersList[5].addLesson(lesson)
                            }

                            val addedLesson = LessonData(
                                lessonTitleIntent, lessonTimeIntent, audienceNumberIntent, dayIntent
                            )
                            lessonsList.add(addedLesson)
                        } else {
                            for (lesson in lessonsList.indices) {
                                if (
                                    lessonsList[lesson].lessonTitle.equals(lessonTitleIntent) &&
                                    lessonsList[lesson].lessonTime.equals(lessonTimeIntent) &&
                                    lessonsList[lesson].audienceNumber.equals(audienceNumberIntent) &&
                                    lessonsList[lesson].day.equals(dayIntent)
                                ) {
                                    val editedLesson = LessonData(
                                        lessonTitleIntent, lessonTimeIntent,
                                        audienceNumberIntent, dayIntent
                                    )
                                    lessonsList[lesson] = editedLesson

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
                                    == editLessonTitle &&
                                    item.findViewById<TextView>(R.id.coupleTimeTextViewItem).text.toString()
                                    == editLessonTime &&
                                    item.findViewById<TextView>(R.id.audienceTextView).text.toString()
                                    == editAudienceNumber) {

                                    item.findViewById<TextView>(
                                        R.id.coupleTitleTextViewItem
                                    ).text = lessonTitleIntent
                                    item.findViewById<TextView>(
                                        R.id.coupleTimeTextViewItem
                                    ).text = lessonTimeIntent
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

    override fun onLayoutClick(lesson: SchoolLesson) {
        val intent = Intent(
            this@SchoolkidActivity, EditLessonInfoActivity::class.java
        )
        intent.putExtra(SchoolkidIntentConstants.LESSON_TITLE, lesson.lessonTitle)
        intent.putExtra(SchoolkidIntentConstants.LESSON_TIME, lesson.lessonTime)
        intent.putExtra(SchoolkidIntentConstants.AUDIENCE_NUMBER, lesson.audienceNumber)
        intent.putExtra(SchoolkidIntentConstants.WHAT_DAY, lesson.day)
        intent.putExtra(SchoolkidIntentConstants.IS_EDIT, true)

        editLessonTitle = lesson.lessonTitle.toString()
        editLessonTime = lesson.lessonTitle.toString()
        editAudienceNumber = lesson.audienceNumber.toString()

        editLessonInfoLauncher.launch(intent)
    }

    override fun onTrashCanClick(lesson: SchoolLesson) {
        when (lesson.day) {
            DaysConstants.MONDAY -> adaptersList[0].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
            DaysConstants.TUESDAY -> adaptersList[1].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
            DaysConstants.WEDNESDAY -> adaptersList[2].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
            DaysConstants.THURSDAY -> adaptersList[3].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
            DaysConstants.FRIDAY -> adaptersList[4].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
            DaysConstants.SATURDAY -> adaptersList[5].removeLessonByData(
                lesson.lessonTitle, lesson.lessonTime, lesson.audienceNumber
            )
        }

        for (item in lessonsList.indices) {
            if (
                lessonsList[item].lessonTitle.equals(lesson.lessonTitle) &&
                lessonsList[item].lessonTime.equals(lesson.lessonTime) &&
                lessonsList[item].audienceNumber.equals(lesson.audienceNumber) &&
                lessonsList[item].day.equals(lesson.day)
            ) {
                lessonsList.removeAt(item)
                break
            }
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

    private fun saveData() {
        db.open()
        db.repopulateSchoolkid(lessonsList)
        db.close()
    }

    private fun getData(): MutableList<LessonData> {
        db.open()
        val dataList = db.readSchoolkid()
        db.close()

        return dataList
    }
}