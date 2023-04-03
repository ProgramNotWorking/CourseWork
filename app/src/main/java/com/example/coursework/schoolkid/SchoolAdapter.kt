package com.example.coursework.schoolkid

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.coach.LessonAdapter
import com.example.coursework.databinding.CoupleStudentItemBinding
import com.example.coursework.student.CoupleAdapter

class SchoolAdapter(
    private val onLayoutClickListener: OnLayoutClickListener,
    private val onDeleteClickListener: OnTrashCanClickListener
): RecyclerView.Adapter<SchoolAdapter.LessonHolder>() {
    private val lessonsList = ArrayList<SchoolLesson>()

    inner class LessonHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = CoupleStudentItemBinding.bind(view)

        fun bind(lesson: SchoolLesson) = with(binding) {
            coupleTitleTextViewItem.text = lesson.lessonTitle
            coupleTimeTextViewItem.text = lesson.lessonTime
            audienceTextView.text = lesson.audienceNumber

            coupleHolder.setOnClickListener {
                onLayoutClickListener.onLayoutClick(lesson)
            }

            deleteCoupleItemButton.setOnClickListener {
                onDeleteClickListener.onTrashCanClick(lesson)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.couple_student_item, parent, false
        )
        return LessonHolder(view)
    }

    override fun onBindViewHolder(holder: LessonHolder, position: Int) {
        holder.bind(lessonsList[position])
    }

    override fun getItemCount(): Int = lessonsList.size

    fun removeLesson(position: Int) {
        lessonsList.removeAt(position)
        notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addLesson(lesson: SchoolLesson) {
        lessonsList.add(lesson)
        notifyDataSetChanged()
    }

    fun removeLessonByData(title: String?, time: String?, audience: String?) {
        for (item in lessonsList.indices) {
            if (lessonsList[item].lessonTitle.equals(title) &&
                lessonsList[item].lessonTime.equals(time) &&
                lessonsList[item].audienceNumber.equals(audience)) {

                lessonsList.removeAt(item)
                notifyItemRemoved(item)

                break
            }
        }
    }

    interface OnLayoutClickListener {
        fun onLayoutClick(lesson: SchoolLesson)
    }

    interface OnTrashCanClickListener {
        fun onTrashCanClick(lesson: SchoolLesson)
    }
}