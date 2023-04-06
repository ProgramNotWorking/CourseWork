package com.example.coursework.coach

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.view.isNotEmpty
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coursework.OptionsActivity
import com.example.coursework.R
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.databinding.ActivityCoachBinding
import com.example.coursework.db.DatabaseManager
import java.time.LocalTime

class CoachActivity : AppCompatActivity(),
    LessonAdapter.OnDeleteClickListener,
    LessonAdapter.OnEditClickListener
{
    private lateinit var binding: ActivityCoachBinding
    private val adapter = LessonAdapter(this@CoachActivity, this@CoachActivity)

    private lateinit var editStudentInfoLauncher: ActivityResultLauncher<Intent>
    private val db = DatabaseManager(this)

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var whatDayIndex = -1
    private lateinit var studentsList: MutableList<StudentData>
    private var editStudentName = "Stub"
    private var editLessonTime = "Stub"

    @RequiresApi(Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCoachBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sharedPreferences = getSharedPreferences(
            SharedPreferencesConstants.COACH_KEY, Context.MODE_PRIVATE
        )
        editor = sharedPreferences.edit()

        whatDayIndex = sharedPreferences.getInt(
            SharedPreferencesConstants.COACH_WHAT_DAY, SharedPreferencesConstants.COACH_DAY_DEFAULT_VALUE
        )
        setDayText()

        db.open()
        studentsList = db.readCoach()
        db.close()

        displayLessons(true)

        binding.apply {
            rcView.layoutManager = GridLayoutManager(this@CoachActivity, 1)
            rcView.adapter = adapter

            rcView.setOnTouchListener(object : OnSwipeTouchListener(this@CoachActivity) {
                override fun onSwipeRight() = toThePrevious()
                override fun onSwipeLeft() = toTheNext()
            })

            nextDayButton.setOnClickListener {
                toTheNext()
            }
            previousDayButton.setOnClickListener {
                toThePrevious()
            }

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        saveData()

                        val intent = Intent(
                            this@CoachActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(CoachIntentConstants.FROM_COACH, true)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.add_student -> {
                        val intent = Intent(
                            this@CoachActivity, EditStudentInfoActivity::class.java
                        )
                        intent.putExtra(CoachIntentConstants.IS_EDIT, false)
                        editStudentInfoLauncher.launch(intent)
                    }
                    R.id.all_days -> {
                        // TODO: Intent to all days and activity as well
                    }
                }

                true
            }

            editStudentInfoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                        result: ActivityResult ->
                    if (result.resultCode == RESULT_OK) {
                        intent = result.data

                        val studentName = intent.getStringExtra(CoachIntentConstants.STUDENT_NAME)
                        val lessonTime = intent.getStringExtra(CoachIntentConstants.LESSON_TIME)

                        if (intent.getBooleanExtra(CoachIntentConstants.IS_ADDED, false)) {
                            val student = StudentData(studentName, lessonTime, getDay())
                            studentsList.add(student)

                            displayLessons(false)
                        } else {
                            val editStudent = StudentData(
                                studentName, lessonTime, getDay()
                            )
                            for (student in studentsList.indices) {
                                if (
                                    studentsList[student].name.equals(editStudentName) &&
                                    studentsList[student].time.equals(editLessonTime) &&
                                    studentsList[student].day.equals(getDay())
                                ) {
                                    studentsList[student] = editStudent
                                    break
                                }
                            }

                            displayLessons(false)
                        }
                    }
                }
        }
    }

    override fun onStop() {
        super.onStop()
        saveData()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toTheNext() {
        if (whatDayIndex == 5) {
            whatDayIndex = 0
        } else {
            whatDayIndex++
        }

        setDayText()
        displayLessons(false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun toThePrevious() {
        if (whatDayIndex == 0) {
            whatDayIndex = 5
        } else {
            whatDayIndex--
        }

        setDayText()
        displayLessons(false)
    }

    override fun onDeleteClick(lesson: Lesson) { // TODO: Check this
        adapter.removeLessonByData(lesson.name, lesson.time)

        for (student in studentsList.indices) {
            if (studentsList[student].name.equals(lesson.name) &&
                studentsList[student].time.equals(lesson.time) &&
                studentsList[student].day.equals(getDay())
            ) {
                studentsList.removeAt(student)
                break
            }
        }
    }

    override fun onEditClick(lesson: Lesson) {
        val intent = Intent(this@CoachActivity, EditStudentInfoActivity::class.java)
        intent.putExtra(CoachIntentConstants.STUDENT_NAME, lesson.name)
        intent.putExtra(CoachIntentConstants.LESSON_TIME, lesson.time)
        intent.putExtra(CoachIntentConstants.IS_EDIT, true)

        editStudentName = lesson.name.toString()
        editLessonTime = lesson.time.toString()

        editStudentInfoLauncher.launch(intent)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun displayLessons(isStart: Boolean) {
        studentsList.sortBy {
            LocalTime.parse(it.time)
        }

        if (!isStart) {
            if (binding.rcView.isNotEmpty()) {
                for (item in binding.rcView.size - 1 downTo 0) {
                    adapter.removeLesson(item)
                }
            }
        }

        when (whatDayIndex) {
            0 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.MONDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
            1 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.TUESDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
            2 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.WEDNESDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
            3 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.THURSDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
            4 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.FRIDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
            5 -> {
                for (student in studentsList) {
                    if (student.day == DaysConstants.SATURDAY) {
                        val lesson = Lesson(student.name, student.time)
                        adapter.addLesson(lesson)
                    }
                }
            }
        }
    }

    private fun setDayText() {
        when (whatDayIndex) {
            0 -> binding.dayTextView.text = getString(R.string.monday)
            1 -> binding.dayTextView.text = getString(R.string.tuesday)
            2 -> binding.dayTextView.text = getString(R.string.wednesday)
            3 -> binding.dayTextView.text = getString(R.string.thursday)
            4 -> binding.dayTextView.text = getString(R.string.friday)
            5 -> binding.dayTextView.text = getString(R.string.saturday)
        }
    }

    private fun getDay(): String {
        return when (whatDayIndex) {
            0 -> DaysConstants.MONDAY
            1 -> DaysConstants.TUESDAY
            2 -> DaysConstants.WEDNESDAY
            3 -> DaysConstants.THURSDAY
            4 -> DaysConstants.FRIDAY
            5 -> DaysConstants.SATURDAY
            else -> "NONE"
        }
    }

    private fun saveData() {
        editor.putInt(SharedPreferencesConstants.COACH_WHAT_DAY, whatDayIndex)
        editor.apply()

        db.open()
        db.repopulateCoach(studentsList)
        db.close()
    }
}
