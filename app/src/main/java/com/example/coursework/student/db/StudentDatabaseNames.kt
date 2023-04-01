package com.example.coursework.student.db

import android.provider.BaseColumns

object StudentDatabaseNames: BaseColumns {
    const val TABLE_NAME = "student_table"
    const val COLUMN_COUPLE_TITLE = "couple_title"
    const val COLUMN_COUPLE_TIME = "couple_time"
    const val COLUMN_AUDIENCE_NUMBER = "audience_number"
    const val COLUMN_DAY = "day"

    const val DATABASE_VERSION = 1
    const val DATABASE = "Student.db"

    const val CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "$COLUMN_COUPLE_TITLE TEXT, $COLUMN_COUPLE_TIME TEXT " +
            "$COLUMN_AUDIENCE_NUMBER TEXT, $COLUMN_DAY TEXT)"
    const val SQL_DELETE_ENTRiES = "DROP TABLE IF EXISTS $TABLE_NAME"
}