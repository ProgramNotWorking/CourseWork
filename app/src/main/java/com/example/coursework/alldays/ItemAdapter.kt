package com.example.coursework.alldays

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.databinding.AllDaysItemBinding

class ItemAdapter: RecyclerView.Adapter<ItemAdapter.ItemHolder>() {
    private val itemsList = ArrayList<ItemData>()

    inner class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = AllDaysItemBinding.bind(view)

        fun bind(item: ItemData) = with(binding) {
            titleTextView.text = item.title
            timeTextView.text = item.time
            if (item.audience != null) {
                audienceTextViewNullMaybe.text = item.audience
            } else {
                audienceTextViewNullMaybe.visibility = View.GONE
            }
            if (item.teacherName != null) {
                teacherNameTextView.text = item.teacherName
            } else {
                teacherNameTextView.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.all_days_item, parent, false
        )
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int = itemsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: ItemData) {
        itemsList.add(item)
        notifyDataSetChanged()
    }

}