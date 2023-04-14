package com.example.coursework.search_system

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.coursework.R
import com.example.coursework.coach.CoachActivity
import com.example.coursework.constants.*
import com.example.coursework.databinding.ActivitySearchBinding
import com.example.coursework.schoolkid.SchoolkidActivity
import com.example.coursework.student.StudentActivity

class SearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySearchBinding
    private val adapter = ItemSearchAdapter()

    private lateinit var from: String
    private var itemsCounter = 0

    private var isFindNeeded = false

    private val handler = Handler()
    private val delay = 350L
    private lateinit var runnable: Runnable

    private lateinit var titlesList: ArrayList<String>
    private lateinit var timesList: ArrayList<String>
    private var audiencesList: ArrayList<String>? = null
    private lateinit var daysList: ArrayList<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        onTouchCloseKeyboard()

        from = guestReceiving()
        runnable = getRunnableObject()
        readData()

        binding.apply {
            rcViewSearch.layoutManager = GridLayoutManager(this@SearchActivity, 1)
            rcViewSearch.adapter = adapter

            searchTextField.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

                override fun onTextChanged(p0: CharSequence?, start: Int, before: Int, count: Int) {
                    handler.removeCallbacks(runnable)
                    handler.postDelayed(runnable, delay)
                    if (p0?.isEmpty() == true) {
                        isFindNeeded = true
                        ifNotFoundTextView.visibility = View.GONE
                    } else {
                        if (!isFindNeeded) {
                            ifNotFoundTextView.visibility = View.VISIBLE
                        } else {
                            ifNotFoundTextView.visibility = View.GONE
                        }
                    }
                }

                override fun afterTextChanged(editText: Editable?) {}
            })
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        goBack()
    }

    private fun readData() {
        titlesList = intent?.getStringArrayListExtra(SearchIntentConstants.TITLES_LIST) as ArrayList<String>
        timesList = intent?.getStringArrayListExtra(SearchIntentConstants.TIMES_LIST) as ArrayList<String>
        audiencesList = intent?.getStringArrayListExtra(SearchIntentConstants.AUDIENCES_LIST)
        daysList = intent?.getStringArrayListExtra(SearchIntentConstants.DAYS_LIST) as ArrayList<String>
    }

    private fun goBack() {
        when (from) {
            SearchConstants.COACH -> {
                val intent = Intent(
                    this@SearchActivity, CoachActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            SearchConstants.STUDENT -> {
                val intent = Intent(
                    this@SearchActivity, StudentActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
            SearchConstants.SCHOOLKID -> {
                val intent = Intent(
                    this@SearchActivity, SchoolkidActivity::class.java
                )
                intent.flags =
                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
    }

    private fun getRunnableObject(): Runnable {
        return Runnable {
            clearScreen()

            val text = binding.searchTextField.text.toString()

            for (item in titlesList.indices) {
                if (titlesList[item] == text ||
                    timesList[item] == text ||
                    audiencesList?.get(item) == text ||
                    daysList[item] == text
                ) {
                    val tempDay = when (daysList[item]) {
                        DaysConstants.MONDAY -> getString(R.string.monday)
                        DaysConstants.TUESDAY -> getString(R.string.tuesday)
                        DaysConstants.WEDNESDAY -> getString(R.string.wednesday)
                        DaysConstants.THURSDAY -> getString(R.string.thursday)
                        DaysConstants.FRIDAY -> getString(R.string.friday)
                        DaysConstants.SATURDAY -> getString(R.string.saturday)
                        else -> getString(R.string.stub)
                    }

                    val showItem = ItemSearch(
                        titlesList[item], timesList[item], audiencesList?.get(item), tempDay
                    )
                    adapter.addItem(showItem)
                    itemsCounter++
                }
            }

            isFindNeeded = itemsCounter > 0
        }
    }

    private fun clearScreen() {
        if (itemsCounter != 0) {
            for (item in itemsCounter - 1 downTo 0) {
                adapter.removeItem(item)
            }
        }
        itemsCounter = 0
    }

    private fun guestReceiving(): String {
        return if (intent.getBooleanExtra(CoachIntentConstants.FROM_COACH, false)) {
            SearchConstants.COACH
        } else if (intent.getBooleanExtra(StudentIntentConstants.FROM_STUDENT, false)) {
            SearchConstants.STUDENT
        } else if (intent.getBooleanExtra(SchoolkidIntentConstants.FROM_SCHOOLKID, false)) {
            SearchConstants.SCHOOLKID
        } else {
            SearchConstants.AMOGUS
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.rcViewSearch.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.rcViewSearch.windowToken, 0)
            }

            true
        }
    }
}

object SearchConstants {
    const val COACH = "coach"
    const val STUDENT = "student"
    const val SCHOOLKID = "schoolkid"

    const val AMOGUS = "its_bad"
}