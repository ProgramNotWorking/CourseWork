package com.example.coursework.db

import com.example.coursework.constants.DaysConstants
import com.example.coursework.student.CoupleData

class OurGroupTimetableData {

    private val data = arrayListOf(
        CoupleData("Культура речи", "09:45-11:20",
            "306", DaysConstants.MONDAY, "Подшивалова"),
        CoupleData("Матанализ", "11:30-13:05",
            "478", DaysConstants.MONDAY, "Плетнева"),
        CoupleData("Матанализ", "13:25-15:00",
            "320", DaysConstants.MONDAY, "Залыгаева"),

        CoupleData("Комплексный анализ", "11:30-13:05",
            "504 П", DaysConstants.TUESDAY, "Мохова"),
        CoupleData("Функциональный анализ", "13:25-15:00",
            "333", DaysConstants.TUESDAY, "Леженина"),
        CoupleData("Английский", "15:10-16:45",
            "404 П", DaysConstants.TUESDAY, "Утицких"),
        CoupleData("Физ-ра", "16:55-18:30",
            "NONE", DaysConstants.TUESDAY, "Тамбовцев"),
    )

    fun getOurGroupData(): ArrayList<CoupleData> = data

}
