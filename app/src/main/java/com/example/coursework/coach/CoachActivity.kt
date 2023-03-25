package com.example.coursework.coach

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.indices
import androidx.core.view.isNotEmpty
import androidx.core.view.iterator
import androidx.core.view.size
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coursework.R
import com.example.coursework.coach.db.CoachDatabaseManager
import com.example.coursework.constants.CoachIntentConstants
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.databinding.ActivityCoachBinding

class CoachActivity : AppCompatActivity(),
    LessonAdapter.OnDeleteClickListener,
    LessonAdapter.OnEditClickListener
{
    private lateinit var binding: ActivityCoachBinding
    private val adapter = LessonAdapter(this@CoachActivity, this@CoachActivity)

    private lateinit var editStudentInfoLauncher: ActivityResultLauncher<Intent>
    private val db = CoachDatabaseManager(this)

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor

    private var whatDayIndex = -1
    private lateinit var studentsList: MutableList<StudentData>

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
        studentsList = db.read()
        db.close()

        displayLessons(true)

        binding.apply {
            rcView.layoutManager = GridLayoutManager(this@CoachActivity, 1)
            rcView.adapter = adapter

            nextDayButton.setOnClickListener {
                if (whatDayIndex == 5) {
                    whatDayIndex = 0
                } else {
                    whatDayIndex++
                }

                setDayText()
                displayLessons(false)
            }
            previousDayButton.setOnClickListener {
                if (whatDayIndex == 0) {
                    whatDayIndex = 5
                } else {
                    whatDayIndex--
                }

                setDayText()
                displayLessons(false)
            }

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        // TODO: Options activity and save data too
                    }
                    R.id.add_student -> {
                        val intent = Intent(this@CoachActivity, EditStudentInfoActivity::class.java)
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

                        if (intent.getBooleanExtra(CoachIntentConstants.IS_ADDED, false)) {
                            val studentName = intent.getStringExtra(CoachIntentConstants.STUDENT_NAME)
                            val lessonTime = intent.getStringExtra(CoachIntentConstants.LESSON_TIME)

                            val student = StudentData(studentName, lessonTime, getDay())
                            studentsList?.add(student)

                            val lesson = Lesson(studentName, lessonTime)
                            adapter.addLesson(lesson)
                        } else {

                        }
                    }
                    if (result.resultCode == RESULT_CANCELED) {
                        // TODO: Think may be?...
                    }
                }
        }
    }

    override fun onStop() {
        super.onStop()

        editor.putInt(SharedPreferencesConstants.COACH_WHAT_DAY, whatDayIndex)
        editor.apply()

        db.open()
        db.repopulate(studentsList)
        db.close()
    }

    override fun onDeleteClick(lesson: Lesson) {
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

    }

    private fun displayLessons(isStart: Boolean) {
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
}
