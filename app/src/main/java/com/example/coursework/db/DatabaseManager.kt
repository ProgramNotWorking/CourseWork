package com.example.coursework.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.coursework.coach.Lesson
import com.example.coursework.coach.StudentData
import com.example.coursework.schoolkid.LessonData
import com.example.coursework.student.Couple
import com.example.coursework.student.CoupleData

class DatabaseManager(context: Context) {
    private val dbHelper = DatabaseHelper(context)
    private var db: SQLiteDatabase? = null

    fun open() {
        db = dbHelper.writableDatabase
    }

    fun repopulateCoach(studentsList: MutableList<StudentData>?) {
        db?.beginTransaction()
        try {
            db?.delete(DatabaseNames.COACH_TABLE_NAME, null, null)
            if (studentsList != null) {
                for (student in studentsList) {
                    addStudent(student)
                }
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    fun repopulateStudent(couplesList: MutableList<CoupleData>?) {
        db?.beginTransaction()
        try {
            db?.delete(DatabaseNames.STUDENT_TABLE_NAME, null, null)
            if (couplesList != null) {
                for (couple in couplesList) {
                    addCouple(couple)
                }
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    fun repopulateSchoolkid(lessonsList: MutableList<LessonData>?) {
        db?.beginTransaction()
        try {
            db?.delete(DatabaseNames.SCHOOLKID_TABLE_NAME, null, null)
            if (lessonsList != null) {
                for (lesson in lessonsList) {
                    addLesson(lesson)
                }
            }
            db?.setTransactionSuccessful()
        } finally {
            db?.endTransaction()
        }
    }

    @SuppressLint("Range")
    fun readCoach(): MutableList<StudentData> {
        val dataList = ArrayList<StudentData>()

        val cursor = db?.query(
            DatabaseNames.COACH_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataName = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COACH_COLUMN_NAME))
                val dataTime = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COACH_COLUMN_TIME))
                val dataDay = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COACH_COLUMN_DAY))

                val dataStudent = StudentData(dataName, dataTime, dataDay)
                dataList.add(dataStudent)
            }
        }

        cursor?.close()
        return dataList
    }

    @SuppressLint("Range")
    fun readStudent(): MutableList<CoupleData> {
        val dataList = ArrayList<CoupleData>()

        val cursor = db?.query(
            DatabaseNames.STUDENT_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataName = cursor?.getString(cursor.getColumnIndex(DatabaseNames.STUDENT_COLUMN_TITLE))
                val dataTime = cursor?.getString(cursor.getColumnIndex(DatabaseNames.STUDENT_COLUMN_TIME))
                val dataAudience = cursor?.getString(cursor.getColumnIndex(DatabaseNames.STUDENT_COLUMN_AUDIENCE))
                val dataDay = cursor?.getString(cursor.getColumnIndex(DatabaseNames.STUDENT_COLUMN_DAY))

                val dataCouple = CoupleData(dataName, dataTime, dataAudience, dataDay)
                dataList.add(dataCouple)
            }
        }

        cursor?.close()
        return dataList
    }

    @SuppressLint("Range")
    fun readSchoolkid(): MutableList<LessonData> {
        val dataList = ArrayList<LessonData>()

        val cursor = db?.query(
            DatabaseNames.SCHOOLKID_TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataName = cursor?.getString(cursor.getColumnIndex(DatabaseNames.SCHOOLKID_COLUMN_TITLE))
                val dataTime = cursor?.getString(cursor.getColumnIndex(DatabaseNames.SCHOOLKID_COLUMN_TIME))
                val dataAudience = cursor?.getString(cursor.getColumnIndex(DatabaseNames.SCHOOLKID_COLUMN_AUDIENCE))
                val dataDay = cursor?.getString(cursor.getColumnIndex(DatabaseNames.SCHOOLKID_COLUMN_DAY))

                val dataLesson = LessonData(dataName, dataTime, dataAudience, dataDay)
                dataList.add(dataLesson)
            }
        }

        cursor?.close()
        return dataList
    }

    fun close() {
        dbHelper.close()
    }

    fun clearCoachTable() {
        db?.delete(DatabaseNames.COACH_TABLE_NAME, null, null)
    }

    fun clearStudentTable() {
        db?.delete(DatabaseNames.STUDENT_TABLE_NAME, null, null)
    }

    fun clearSchoolkidTable() {
        db?.delete(DatabaseNames.SCHOOLKID_TABLE_NAME, null, null)
    }

    private fun addStudent(student: StudentData) {
        val values = ContentValues().apply {
            put(DatabaseNames.COACH_COLUMN_NAME, student.name)
            put(DatabaseNames.COACH_COLUMN_TIME, student.time)
            put(DatabaseNames.COACH_COLUMN_DAY, student.day)
        }
        db?.insert(DatabaseNames.COACH_TABLE_NAME, null, values)
    }

    private fun addCouple(couple: CoupleData) {
        val values = ContentValues().apply {
            put(DatabaseNames.STUDENT_COLUMN_TITLE, couple.coupleTitle)
            put(DatabaseNames.STUDENT_COLUMN_TIME, couple.coupleTime)
            put(DatabaseNames.STUDENT_COLUMN_AUDIENCE, couple.audienceNumber)
            put(DatabaseNames.STUDENT_COLUMN_DAY, couple.day)
        }
        db?.insert(DatabaseNames.STUDENT_TABLE_NAME, null, values)
    }

    private fun addLesson(lesson: LessonData) {
        val values = ContentValues().apply {
            put(DatabaseNames.SCHOOLKID_COLUMN_TITLE, lesson.lessonTitle)
            put(DatabaseNames.SCHOOLKID_COLUMN_TIME, lesson.lessonTime)
            put(DatabaseNames.SCHOOLKID_COLUMN_AUDIENCE, lesson.audienceNumber)
            put(DatabaseNames.SCHOOLKID_COLUMN_DAY, lesson.day)
        }
        db?.insert(DatabaseNames.SCHOOLKID_TABLE_NAME, null, values)
    }
}