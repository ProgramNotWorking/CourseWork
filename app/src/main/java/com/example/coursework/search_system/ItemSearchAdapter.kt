package com.example.coursework.search_system

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.databinding.ItemSearchBinding

class ItemSearchAdapter: RecyclerView.Adapter<ItemSearchAdapter.ItemHolder>() {
    private val itemsList = ArrayList<ItemSearch>()

    inner class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ItemSearchBinding.bind(view)

        fun bind(item: ItemSearch) = with(binding) {
            titleSearchTextView.text = item.title
            timeSearchTextView.text = item.time
            if (item.audience != null) {
                audienceSearchTextView.text = item.audience
            } else {
                audienceSearchTextView.visibility = View.GONE
            }
            if (item.teacherName != null) {
                teacherNameSearchTextView.text = item.teacherName
            } else {
                teacherNameSearchTextView.visibility = View.GONE
            }
            daySearchTextView.text = item.day
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.item_search, parent, false
        )
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.bind(itemsList[position])
    }

    override fun getItemCount(): Int = itemsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addItem(item: ItemSearch) {
        itemsList.add(item)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        itemsList.removeAt(position)
        notifyItemRemoved(position)
    }
}