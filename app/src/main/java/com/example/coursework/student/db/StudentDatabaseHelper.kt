package com.example.coursework.student.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.coursework.coach.db.DatabaseNames

class StudentDatabaseHelper(context: Context): SQLiteOpenHelper(
    context, StudentDatabaseNames.TABLE_NAME, null, DatabaseNames.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(StudentDatabaseNames.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(StudentDatabaseNames.SQL_DELETE_ENTRiES)
        onCreate(db)
    }
}