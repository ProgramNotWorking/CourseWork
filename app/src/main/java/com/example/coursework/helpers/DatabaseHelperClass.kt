package com.example.coursework.helpers

import com.example.coursework.coach.StudentData
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.db.DatabaseManager
import com.example.coursework.schoolkid.LessonData
import com.example.coursework.student.CoupleData

class DatabaseHelperClass(private val db: DatabaseManager) {

    fun getCoachData(): MutableList<StudentData> {
        db.open()
        val dataList = db.readCoach()
        db.close()

        return dataList
    }

    fun getStudentData(): MutableList<CoupleData> {
        db.open()
        val dataList = db.readStudent()
        db.close()

        return dataList
    }

    fun getSchoolkidData(): MutableList<LessonData> {
        db.open()
        val dataList = db.readSchoolkid()
        db.close()

        return dataList
    }

    fun saveData(
        whoSave: String,
        studentData: MutableList<CoupleData>?,
        schoolkidData: MutableList<LessonData>?,
        coachData: MutableList<StudentData>?
    ) {
        when (whoSave) {
            SharedPreferencesConstants.STUDENT -> {
                db.open()
                db.repopulateStudent(studentData)
                db.close()
            }
            SharedPreferencesConstants.SCHOOLKID -> {
                db.open()
                db.repopulateSchoolkid(schoolkidData)
                db.close()
            }
            SharedPreferencesConstants.COACH -> {
                db.open()
                db.repopulateCoach(coachData)
                db.close()
            }
        }
    }

}