package com.example.coursework.coach

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.coursework.R
import com.example.coursework.databinding.LessonCoachItemBinding

class LessonAdapter(
    private val onDeleteListener: OnDeleteClickListener,
    private val onEditListener: OnEditClickListener
): RecyclerView.Adapter<LessonAdapter.LessonHolder>() {
    private val lessonsList = ArrayList<Lesson>()

    inner class LessonHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = LessonCoachItemBinding.bind(view)

        fun bind(lesson: Lesson) = with(binding) {
            nameTextViewItem.text = lesson.name
            timeTextViewItem.text = lesson.time

            deleteStudentButtonItem.setOnClickListener {
                onDeleteListener.onDeleteClick(lesson)
            }

            editStudentButtonItem.setOnClickListener {
                onEditListener.onEditClick(lesson)
            }
        }
    }

    interface OnDeleteClickListener {
        fun onDeleteClick(lesson: Lesson)
    }

    interface OnEditClickListener {
        fun onEditClick(lesson: Lesson)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LessonHolder {
        val view = LayoutInflater.from(parent.context).inflate(
            R.layout.lesson_coach_item, parent, false
        )
        return LessonHolder(view)
    }

    override fun onBindViewHolder(holder: LessonHolder, position: Int) {
        holder.bind(lessonsList[position])
    }

    override fun getItemCount(): Int = lessonsList.size

    @SuppressLint("NotifyDataSetChanged")
    fun addLesson(lesson: Lesson) {
        lessonsList.add(lesson)
        notifyDataSetChanged()
    }

    fun removeLesson(position: Int) {
        lessonsList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeLessonByData(studentName: String?, studentTime: String?) {
        for (item in lessonsList.indices) {
            if (lessonsList[item].name == studentName && lessonsList[item].time == studentTime) {
                lessonsList.removeAt(item)
                notifyItemRemoved(item)

                break
            }
        }
    }
}