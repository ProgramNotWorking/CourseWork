package com.example.coursework.student

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.databinding.CoupleStudentItemBinding


class CoupleAdapter(
    private val onLayoutClickListener: OnLayoutClickListener,
    private val onDeleteClickListener: OnTrashCanClickListener
): RecyclerView.Adapter<CoupleAdapter.CoupleHolder>() {
    private val couplesList = ArrayList<Couple>()

    inner class CoupleHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = CoupleStudentItemBinding.bind(view)

        fun bind(couple: Couple) = with(binding) {
            coupleTitleTextViewItem.text = couple.coupleTitle
            coupleTimeTextViewItem.text = couple.coupleTime
            audienceTextView.text = couple.audienceNumber

            coupleHolder.setOnClickListener {
                onLayoutClickListener.onLayoutClick(couple)
            }

            deleteCoupleItemButton.setOnClickListener {
                onDeleteClickListener.onTrashCanClick(couple)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CoupleHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.couple_student_item, parent, false
        )
        return CoupleHolder(view)
    }

    override fun onBindViewHolder(holder: CoupleHolder, position: Int) {
        holder.bind(couplesList[position])
    }

    override fun getItemCount(): Int = couplesList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addCouple(couple: Couple) {
        couplesList.add(couple)
        notifyDataSetChanged()
    }

    fun removeCouple(position: Int) {
        couplesList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeCoupleByData(title: String?, time: String?, audience: String?) {
        for (item in couplesList.indices) {
            if (couplesList[item].coupleTitle.equals(title) &&
                couplesList[item].coupleTime.equals(time) &&
                couplesList[item].audienceNumber.equals(audience)) {

                couplesList.removeAt(item)
                notifyItemRemoved(item)

                break
            }
        }
    }

    interface OnLayoutClickListener {
        fun onLayoutClick(couple: Couple)
    }

    interface OnTrashCanClickListener {
        fun onTrashCanClick(couple: Couple)
    }
}