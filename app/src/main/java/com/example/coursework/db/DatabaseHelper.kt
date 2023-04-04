package com.example.coursework.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context): SQLiteOpenHelper(
    context, DatabaseNames.DATABASE, null, DatabaseNames.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseNames.CREATE_COACH_TABLE)
        db?.execSQL(DatabaseNames.CREATE_STUDENT_TABLE)
        db?.execSQL(DatabaseNames.CREATE_SCHOOLKID_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(DatabaseNames.SQL_DELETE_COACH_TABLE)
        db?.execSQL(DatabaseNames.SQL_DELETE_STUDENT_TABLE)
        db?.execSQL(DatabaseNames.SQL_DELETE_SCHOOLKID_TABLE)
        onCreate(db)
    }
}