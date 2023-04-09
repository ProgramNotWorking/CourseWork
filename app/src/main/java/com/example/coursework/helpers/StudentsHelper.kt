package com.example.coursework.helpers

import android.content.Context
import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import com.example.coursework.constants.DaysConstants
import com.example.coursework.constants.SchoolkidIntentConstants
import com.example.coursework.constants.StudentIntentConstants
import com.example.coursework.schoolkid.EditLessonInfoActivity
import com.example.coursework.schoolkid.LessonData
import com.example.coursework.schoolkid.SchoolAdapter
import com.example.coursework.schoolkid.SchoolLesson
import com.example.coursework.student.Couple
import com.example.coursework.student.CoupleAdapter
import com.example.coursework.student.CoupleData
import com.example.coursework.student.EditCoupleInfoActivity

class StudentsHelper(val context: Context) {

    fun startEdit(
        isStudent: Boolean,
        launcher: ActivityResultLauncher<Intent>,
        couple: Couple?,
        lesson: SchoolLesson?
    ) {
        val intent: Intent

        if (isStudent) {
            intent = Intent(
                context, EditCoupleInfoActivity::class.java
            )
            intent.putExtra(StudentIntentConstants.COUPLE_TITLE, couple!!.coupleTitle)
            intent.putExtra(StudentIntentConstants.COUPLE_TIME, couple.coupleTime)
            intent.putExtra(StudentIntentConstants.AUDIENCE_NUMBER, couple.audienceNumber)
            intent.putExtra(StudentIntentConstants.WHAT_DAY, couple.day)
            intent.putExtra(StudentIntentConstants.IS_EDIT, true)
        } else {
            intent = Intent(
                context, EditLessonInfoActivity::class.java
            )
            intent.putExtra(SchoolkidIntentConstants.LESSON_TITLE, lesson!!.lessonTitle)
            intent.putExtra(SchoolkidIntentConstants.LESSON_TIME, lesson.lessonTime)
            intent.putExtra(SchoolkidIntentConstants.AUDIENCE_NUMBER, lesson.audienceNumber)
            intent.putExtra(SchoolkidIntentConstants.WHAT_DAY, lesson.day)
            intent.putExtra(SchoolkidIntentConstants.IS_EDIT, true)
        }

        launcher.launch(intent)
    }

    fun deleteCouple(
        adaptersList: ArrayList<CoupleAdapter>,
        couple: Couple,
        couplesList: MutableList<CoupleData>
    ) {
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

    fun deleteLesson(
        adaptersList: ArrayList<SchoolAdapter>,
        lesson: SchoolLesson,
        lessonsList: ArrayList<LessonData>
    ) {
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

    fun getStudentResult(
        intent: Intent,
        adaptersList: ArrayList<CoupleAdapter>,
        couplesList: MutableList<CoupleData>,
        editCoupleTitle: String,
        editCoupleTime: String,
        editAudienceNumber: String
    ): String? {
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
        }

        return dayIntent
    }

}