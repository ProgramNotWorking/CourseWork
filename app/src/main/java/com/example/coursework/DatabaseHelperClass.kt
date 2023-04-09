package com.example.coursework

import android.content.Context
import com.example.coursework.constants.SharedPreferencesConstants
import com.example.coursework.db.DatabaseManager
import com.example.coursework.schoolkid.LessonData
import com.example.coursework.student.CoupleData

class DatabaseHelperClass(private val db: DatabaseManager) {

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
        schoolkidData: MutableList<LessonData>?
    ) {
        if (whoSave == SharedPreferencesConstants.STUDENT) {
            db.open()
            db.repopulateStudent(studentData)
            db.close()
        } else {
            db.open()
            db.repopulateSchoolkid(schoolkidData)
            db.close()
        }
    }

}