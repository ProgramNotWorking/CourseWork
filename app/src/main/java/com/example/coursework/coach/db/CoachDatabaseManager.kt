package com.example.coursework.coach.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.coursework.coach.StudentData

class CoachDatabaseManager(context: Context) {
    private val dbHelper = CoachDatabaseHelper(context)
    private var db: SQLiteDatabase? = null

    fun open() {
        db = dbHelper.writableDatabase
    }

    private fun addStudent(student: StudentData) {
        val values = ContentValues().apply {
            put(DatabaseNames.COLUMN_NAME, student.name)
            put(DatabaseNames.COLUMN_TIME, student.time)
            put(DatabaseNames.COLUMN_DAY, student.day)
        }
        db?.insert(DatabaseNames.TABLE_NAME, null, values)
    }

    fun repopulate(studentsList: MutableList<StudentData>?) {
        db?.beginTransaction()
        try {
            db?.delete(DatabaseNames.TABLE_NAME, null, null)
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

    @SuppressLint("Range")
    fun read(): MutableList<StudentData> {
        val dataList = ArrayList<StudentData>()

        val cursor = db?.query(
            DatabaseNames.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataName = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COLUMN_NAME))
                val dataTime = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COLUMN_TIME))
                val dataDay = cursor?.getString(cursor.getColumnIndex(DatabaseNames.COLUMN_DAY))

                val dataStudent = StudentData(dataName, dataTime, dataDay)
                dataList.add(dataStudent)
            }
        }

        cursor?.close()
        return dataList
    }

    fun close() {
        dbHelper.close()
    }

    fun clear() {
        db?.delete(DatabaseNames.TABLE_NAME, null, null)
    }
}