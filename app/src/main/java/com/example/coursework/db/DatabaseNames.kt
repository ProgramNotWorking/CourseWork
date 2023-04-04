package com.example.coursework.db

import android.provider.BaseColumns

object DatabaseNames: BaseColumns {
    const val COACH_TABLE_NAME = "coach_table"
    const val COACH_COLUMN_NAME = "coach_name"
    const val COACH_COLUMN_TIME = "coach_time"
    const val COACH_COLUMN_DAY = "coach_day"

    const val STUDENT_TABLE_NAME = "student_table"
    const val STUDENT_COLUMN_TITLE = "student_title"
    const val STUDENT_COLUMN_TIME = "student_time"
    const val STUDENT_COLUMN_AUDIENCE = "student_audience"
    const val STUDENT_COLUMN_DAY = "student_day"

    const val SCHOOLKID_TABLE_NAME = "schoolkid_table"
    const val SCHOOLKID_COLUMN_TITLE = "schoolkid_title"
    const val SCHOOLKID_COLUMN_TIME = "schoolkid_time"
    const val SCHOOLKID_COLUMN_AUDIENCE = "schoolkid_audience"
    const val SCHOOLKID_COLUMN_DAY = "schoolkid_day"

    const val DATABASE_VERSION = 1
    const val DATABASE = "Info.db"

    const val CREATE_COACH_TABLE = "CREATE TABLE $COACH_TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "$COACH_COLUMN_NAME TEXT, $COACH_COLUMN_TIME TEXT, $COACH_COLUMN_DAY TEXT)"
    const val SQL_DELETE_COACH_TABLE = "DROP TABLE IF EXISTS $COACH_TABLE_NAME"

    const val CREATE_STUDENT_TABLE = "CREATE TABLE $STUDENT_TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "$STUDENT_COLUMN_TITLE TEXT, $STUDENT_COLUMN_TIME TEXT, " +
            "$STUDENT_COLUMN_AUDIENCE TEXT, $STUDENT_COLUMN_DAY)"
    const val SQL_DELETE_STUDENT_TABLE = "DROP TABLE IS EXISTS $STUDENT_TABLE_NAME"

    const val CREATE_SCHOOLKID_TABLE = "CREATE TABLE $SCHOOLKID_TABLE_NAME (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY, " +
            "$SCHOOLKID_COLUMN_TITLE TEXT, $SCHOOLKID_COLUMN_TIME TEXT, " +
            "$SCHOOLKID_COLUMN_AUDIENCE TEXT, $SCHOOLKID_COLUMN_DAY)"
    const val SQL_DELETE_SCHOOLKID_TABLE = "DROP TABLE IS EXISTS $SCHOOLKID_TABLE_NAME"
}