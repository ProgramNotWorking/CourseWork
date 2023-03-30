package com.example.coursework.student.db

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import com.example.coursework.coach.db.DatabaseNames
import com.example.coursework.student.CoupleData

class StudentDatabaseManager(context: Context) {
    private val dbHelper = StudentDatabaseHelper(context)
    private var db: SQLiteDatabase? = null

    fun open() {
        db = dbHelper.writableDatabase
    }

    private fun addCouple(couple: CoupleData) {
        val values = ContentValues().apply {
            put(StudentDatabaseNames.COLUMN_COUPLE_TITLE, couple.coupleTitle)
            put(StudentDatabaseNames.COLUMN_COUPLE_TIME, couple.coupleTime)
            put(StudentDatabaseNames.COLUMN_AUDIENCE_NUMBER, couple.audienceNumber)
            put(StudentDatabaseNames.COLUMN_DAY, couple.day)
        }
        db?.insert(StudentDatabaseNames.TABLE_NAME, null, values)
    }

    fun repopulate(couplesList: MutableList<CoupleData>?) {
        db?.beginTransaction()
        try {
            db?.delete(StudentDatabaseNames.TABLE_NAME, null, null)
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

    @SuppressLint("Range")
    fun read(): MutableList<CoupleData> {
        val dataList = ArrayList<CoupleData>()

        val cursor = db?.query(
            StudentDatabaseNames.TABLE_NAME,
            null,
            null,
            null,
            null,
            null,
            null
        )

        with(cursor) {
            while (this?.moveToNext()!!) {
                val dataName = cursor?.getString(cursor.getColumnIndex(StudentDatabaseNames.COLUMN_COUPLE_TITLE))
                val dataTime = cursor?.getString(cursor.getColumnIndex(StudentDatabaseNames.COLUMN_COUPLE_TIME))
                val audienceData = cursor?.getString(cursor.getColumnIndex(StudentDatabaseNames.COLUMN_AUDIENCE_NUMBER))
                val dayData = cursor?.getString(cursor.getColumnIndex(StudentDatabaseNames.COLUMN_DAY))

                val dataCouple = CoupleData(dataName, dataTime, audienceData, dayData)
                dataList.add(dataCouple)
            }
        }

        cursor?.close()
        return dataList
    }

    fun close() {
        dbHelper.close()
    }

    fun clear() {
        db?.delete(StudentDatabaseNames.TABLE_NAME, null, null)
    }
}