package com.example.coursework.coach.db

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class CoachDatabaseHelper(context: Context): SQLiteOpenHelper(
    context, DatabaseNames.TABLE_NAME, null, DatabaseNames.DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(DatabaseNames.CREATE_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(DatabaseNames.SQL_DELETE_ENTRiES)
        onCreate(db)
    }
}