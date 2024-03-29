package com.example.coursework.student

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.*
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.*
import com.example.coursework.R
import com.example.coursework.alldays.AllDaysActivity
import com.example.coursework.constants.*
import com.example.coursework.databinding.ActivityStudentBinding
import com.example.coursework.db.DatabaseManager
import com.example.coursework.db.OurGroupTimetableData
import com.example.coursework.firebase.StudentTimetableData
import com.example.coursework.helpers.AnimationsHelperClass
import com.example.coursework.helpers.DatabaseHelperClass
import com.example.coursework.helpers.StudentsHelper
import com.example.coursework.search_system.SearchActivity
import com.google.firebase.database.*
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

    private val helper = StudentsHelper(this@StudentActivity)

    private val firebase = FirebaseDatabase.getInstance().getReference("student")
    private lateinit var getDataCode: String

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        openAnimationHelperClass()
        onTouchCloseKeyboard()

        addButtonAnimationSet.addAnimation(alphaInAnimation)
        addButtonAnimationSet.addAnimation(addButtonRotateAnimation)

        addButtonOutAnimationSet.addAnimation(alphaOutAnimations)
        addButtonOutAnimationSet.addAnimation(addButtonRotationOutAnimation)

        val sharedPreferences = getSharedPreferences(
            SharedPreferencesConstants.LOAD_KEY, Context.MODE_PRIVATE
        )
        val editor = sharedPreferences.edit()

        getDataCode = sharedPreferences.getString(
            SharedPreferencesConstants.LOAD_CODE, "NONE"
        ).toString()

        couplesList = if (sharedPreferences.getBoolean(SharedPreferencesConstants.IS_NEED_LOAD, false)) {
            editor.putBoolean(
                SharedPreferencesConstants.IS_NEED_LOAD, false
            )
            editor.apply()

            getDataFromDB()
        } else {
            databaseHelper.getStudentData()
        }

        rcViewsList = getRcViewsList()

        binding.apply {
            connectAdaptersToRcViews()

            bottomNavigationView.setOnItemSelectedListener {
                when (it.itemId) {
                    R.id.options -> {
                        databaseHelper.saveData(
                            SharedPreferencesConstants.STUDENT, couplesList,
                            null, null
                        )

                        val intent = Intent(
                            this@StudentActivity, OptionsActivity::class.java
                        )
                        intent.putExtra(StudentIntentConstants.FROM_STUDENT, true)
                        intent.flags =
                            Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        startActivity(intent)
                        finish()
                    }
                    R.id.all_days -> {
                        val intent = Intent(
                            this@StudentActivity, AllDaysActivity::class.java
                        )

                        val titlesList = ArrayList<String>()
                        val timesList = ArrayList<String>()
                        val audiencesList = ArrayList<String>()
                        val daysList = ArrayList<String>()
                        val teachersList = ArrayList<String>()

                        for (couple in couplesList) {
                            couple.coupleTitle?.let { it1 -> titlesList.add(it1) }
                            couple.coupleTime?.let { it2 -> timesList.add(it2) }
                            couple.audienceNumber?.let { it3 -> audiencesList.add(it3) }
                            couple.day?.let { it4 -> daysList.add(it4) }
                            couple.teacherName?.let { it5 -> teachersList.add(it5) }
                        }

                        intent.putExtra(AllDaysConstants.TITLE, titlesList)
                        intent.putExtra(AllDaysConstants.TIME, timesList)
                        intent.putExtra(AllDaysConstants.AUDIENCE, audiencesList)
                        intent.putExtra(AllDaysConstants.DAY, daysList)
                        intent.putExtra(AllDaysConstants.TEACHER_NAME, teachersList)

                        startActivity(intent)
                    }
                    R.id.search_bottom_nav -> {
                        val intent = Intent(
                            this@StudentActivity, SearchActivity::class.java
                        )
                        intent.putExtra(StudentIntentConstants.FROM_STUDENT, true)

                        val titlesList = ArrayList<String>()
                        val timesList = ArrayList<String>()
                        val audiencesList = ArrayList<String>()
                        val daysList = ArrayList<String>()
                        val teachersList = ArrayList<String>()

                        for (couple in couplesList) {
                            couple.coupleTitle?.let { it1 -> titlesList.add(it1) }
                            couple.coupleTime?.let { it2 -> timesList.add(it2) }
                            couple.audienceNumber?.let { it3 -> audiencesList.add(it3) }
                            couple.day?.let { it4 -> daysList.add(it4) }
                            couple.teacherName?.let { it5 -> teachersList.add(it5) }
                        }

                        intent.putExtra(SearchIntentConstants.TITLES_LIST, titlesList)
                        intent.putExtra(SearchIntentConstants.TIMES_LIST, timesList)
                        intent.putExtra(SearchIntentConstants.AUDIENCES_LIST, audiencesList)
                        intent.putExtra(SearchIntentConstants.DAYS_LIST, daysList)
                        intent.putExtra(SearchIntentConstants.TEACHERS_LIST, teachersList)

                        startActivity(intent)
                    }
                }

                true
            }

            buttonsClickListenersInit()

            uploadStudentTimetableButton.setOnClickListener {
                if (uploadTimetableStudentEditTextView.text.toString().isEmpty()) {
                    Toast.makeText(
                        this@StudentActivity,
                        getString(R.string.code_field_is_empty),
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    val key = uploadTimetableStudentEditTextView.text.toString()

                    for ((index, item) in couplesList.withIndex()) {
                        val savedStudentData = item.coupleTitle?.let { it1 ->
                            item.coupleTime?.let { it2 ->
                                item.audienceNumber?.let { it3 ->
                                    item.day?.let { it4 ->
                                        StudentTimetableData(
                                            it1,
                                            it2,
                                            it3,
                                            it4,
                                            item.teacherName,
                                        )
                                    }
                                }
                            }
                        }

                        firebase.child(key).child(index.toString()).setValue(savedStudentData)
                    }

                    Toast.makeText(
                        this@StudentActivity,
                        getString(R.string.timetable_created),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            editCoupleInfoLauncher =
                registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->

                    intent = result.data

                    if (result.resultCode == RESULT_OK) {
                        val dayIntent = helper.getStudentResult(
                            intent,
                            adaptersList,
                            couplesList,
                            editCoupleTitle,
                            editCoupleTime,
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
            SharedPreferencesConstants.STUDENT, couplesList,
            null, null
        )
    }

    override fun onLayoutClick(couple: Couple) {
        editCoupleTitle = couple.coupleTitle.toString()
        editCoupleTime = couple.coupleTime.toString()
        editAudienceNumber = couple.audienceNumber.toString()

        helper.startEdit(
            true, editCoupleInfoLauncher, couple, null
        )
    }

    override fun onTrashCanClick(couple: Couple) {
        helper.deleteCouple(adaptersList, couple, couplesList)
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
            mondayHeader.setOnClickListener { openInit(DaysConstants.MONDAY) }
            addMondayButton.setOnClickListener { startEditActivity(DaysConstants.MONDAY) }
            tuesdayHeader.setOnClickListener { openInit(DaysConstants.TUESDAY) }
            addTuesdayButton.setOnClickListener { startEditActivity(DaysConstants.TUESDAY) }
            wednesdayHeader.setOnClickListener { openInit(DaysConstants.WEDNESDAY) }
            addWednesdayButton.setOnClickListener { startEditActivity(DaysConstants.WEDNESDAY) }
            thursdayHeader.setOnClickListener { openInit(DaysConstants.THURSDAY) }
            addThursdayButton.setOnClickListener { startEditActivity(DaysConstants.THURSDAY) }
            fridayHeader.setOnClickListener { openInit(DaysConstants.FRIDAY) }
            addFridayButton.setOnClickListener { startEditActivity(DaysConstants.FRIDAY) }
            saturdayHeader.setOnClickListener { openInit(DaysConstants.SATURDAY) }
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
            LocalTime.parse(it.coupleTime?.substring(0, 5))
        }

        for (couple in couplesList) {
            if (couple.day.equals(day)) {
                val showCouple = Couple(
                    couple.coupleTitle, couple.coupleTime, couple.audienceNumber,
                    couple.day, couple.teacherName
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

        animationsHelperClass.headersAnimationInit(
            headersList = arrayListOf(
                binding.mondayHeader, binding.tuesdayHeader, binding.wednesdayHeader,
                binding.thursdayHeader, binding.fridayHeader, binding.saturdayHeader
            )
        )
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

    @SuppressLint("ClickableViewAccessibility")
    private fun onTouchCloseKeyboard() {
        binding.mainHolderScrollView.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                keyboard.hideSoftInputFromWindow(binding.mainHolderScrollView.windowToken, 0)
            }

            true
        }
    }

    private fun getDataFromDB(): MutableList<CoupleData> {
        val dataList = mutableListOf<CoupleData>()

        firebase.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (code in snapshot.children) {
                    if (code.key == getDataCode) {
                        for (item in code.children) {
                            val tempStudent = item.getValue(StudentTimetableData::class.java)
                            val tempCoupleData = CoupleData(
                                tempStudent?.coupleTitle,
                                tempStudent?.coupleTime,
                                tempStudent?.audienceNumber,
                                tempStudent?.day,
                                tempStudent?.teacherName
                            )

                            dataList.add(tempCoupleData)
                        }

                        break
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("Firebase get data:", "Error with getting data")
            }
        })

        return dataList
    }
}