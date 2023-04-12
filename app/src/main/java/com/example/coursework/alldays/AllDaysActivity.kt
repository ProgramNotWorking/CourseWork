package com.example.coursework.alldays

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.databinding.ActivityAllDaysBinding
import com.example.coursework.constants.AllDaysConstants
import com.example.coursework.constants.DaysConstants

class AllDaysActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAllDaysBinding

    private val helper = HelperClass(this@AllDaysActivity)

    private lateinit var rcViewsList: ArrayList<RecyclerView>
    private lateinit var adaptersList: ArrayList<ItemAdapter>

    private lateinit var titlesList: ArrayList<String>
    private lateinit var timesList: ArrayList<String>
    private var audiencesList: ArrayList<String>? = null
    private lateinit var daysList: ArrayList<String>

    private lateinit var itemsList: ArrayList<ItemData>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllDaysBinding.inflate(layoutInflater)
        setContentView(binding.root)

        titlesList = intent?.getStringArrayListExtra(AllDaysConstants.TITLE) as ArrayList<String>
        timesList = intent?.getStringArrayListExtra(AllDaysConstants.TIME) as ArrayList<String>
        audiencesList = intent?.getStringArrayListExtra(AllDaysConstants.AUDIENCE)
        daysList = intent?.getStringArrayListExtra(AllDaysConstants.DAY) as ArrayList<String>

        rcViewsList = helper.getRcViewsList()
        adaptersList = helper.convertAdaptersIntoList()
        helper.connectAdaptersToRcViews()

        itemsList = getDataList()

        helper.display()
    }

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
    override fun onBackPressed() {
        finish()
    }

    private fun getDataList(): ArrayList<ItemData> {
        val dataList = ArrayList<ItemData>()

        for (item in titlesList.indices) {
            val tempData = ItemData(
                titlesList[item], timesList[item], audiencesList?.get(item), daysList[item]
            )
            dataList.add(tempData)
        }

        return dataList
    }

    inner class HelperClass(val context: Context) {

        fun getRcViewsList(): ArrayList<RecyclerView> = with(binding) {
            return arrayListOf(
                mondayDaysRcView, tuesdayDaysRcView, wednesdayDaysRcView,
                thursdayDaysRcView, fridayDaysRcView, saturdayDaysRcView
            )
        }

        fun convertAdaptersIntoList(): ArrayList<ItemAdapter> {
            val mondayAdapter = ItemAdapter()
            val tuesdayAdapter = ItemAdapter()
            val wednesdayAdapter = ItemAdapter()
            val thursdayAdapter = ItemAdapter()
            val fridayAdapter = ItemAdapter()
            val saturdayAdapter = ItemAdapter()

            return arrayListOf(
                mondayAdapter, tuesdayAdapter, wednesdayAdapter, thursdayAdapter,
                fridayAdapter, saturdayAdapter
            )
        }

        fun connectAdaptersToRcViews() = with(binding) {
            val countItemsList = arrayListOf( 0, 0, 0, 0, 0, 0 )

            for (item in daysList) {
                when (item) {
                    DaysConstants.MONDAY -> countItemsList[0] += 1
                    DaysConstants.TUESDAY -> countItemsList[1] += 1
                    DaysConstants.WEDNESDAY -> countItemsList[2] += 1
                    DaysConstants.THURSDAY -> countItemsList[3] += 1
                    DaysConstants.FRIDAY -> countItemsList[4] += 1
                    DaysConstants.SATURDAY -> countItemsList[5] += 1
                }
            }

            var isNull = true
            for (item in countItemsList) {
                if (item > 0) isNull = false
            }
            val spanCount = if (!isNull) {
                countItemsList.maxOrNull() ?: 1
            } else {
                1
            }

            mondayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            mondayDaysRcView.adapter = adaptersList[0]
            tuesdayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            tuesdayDaysRcView.adapter = adaptersList[1]
            wednesdayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            wednesdayDaysRcView.adapter = adaptersList[2]
            thursdayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            thursdayDaysRcView.adapter = adaptersList[3]
            fridayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            fridayDaysRcView.adapter = adaptersList[4]
            saturdayDaysRcView.layoutManager = GridLayoutManager(context, spanCount)
            saturdayDaysRcView.adapter = adaptersList[5]
        }

        fun display() {
            for (item in itemsList) {
                when (item.day) {
                    DaysConstants.MONDAY -> adaptersList[0].addItem(item)
                    DaysConstants.TUESDAY -> adaptersList[1].addItem(item)
                    DaysConstants.WEDNESDAY -> adaptersList[2].addItem(item)
                    DaysConstants.THURSDAY -> adaptersList[3].addItem(item)
                    DaysConstants.FRIDAY -> adaptersList[4].addItem(item)
                    DaysConstants.SATURDAY -> adaptersList[5].addItem(item)
                }
            }
        }

    }
}