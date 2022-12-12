package com.example.roomwithaview

import android.content.Intent
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.roomwithaview.database.ToDoItem
import java.text.SimpleDateFormat
import java.util.*

class ToDoListAdapter(val itemClicked: (id: Int, userId: String, itemDue: Long, completed: Int) -> Unit) :
    ListAdapter<ToDoItem, ToDoListAdapter.ToDoItemViewHolder>(ToDoItemComparator()) {

    var clickOnItem: ((ToDoItem) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ToDoItemViewHolder {
        return ToDoItemViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: ToDoItemViewHolder, position: Int) {
        val current = getItem(position)
        //holder.bind(current.id, current.title, current.content, current.dueDate, current.completed)
        current.id.let {
            holder.bind(it, current.userId, current.title, current.content, current.dueDate, current.completed)
        }
        holder.itemView.tag = current.id

        holder.todoItemDelete.setOnClickListener {
            current.dueDate?.let { it1 -> itemClicked(holder.itemView.tag as Int, current.userId, it1, current.completed) }

        }

        //redirect to detail page
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, detail_activity::class.java)
            intent.putExtra("id", current.id)
            intent.putExtra("detailId", current.userId)
            intent.putExtra("detailTitle", current.title)
            intent.putExtra("detailContent", current.content)
            intent.putExtra("detailTime", current.dueDate)
            intent.putExtra("checkBoxComplete", current.completed)
            holder.itemView.context.startActivity(intent)
            clickOnItem?.invoke(current)
        }
    }

    class ToDoItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val toDoItemTitleView: TextView = itemView.findViewById(R.id.tvTitle)
        private val toDoItemContentView: TextView = itemView.findViewById(R.id.tvContent)
        private val todoItemDueView: TextView = itemView.findViewById(R.id.tvDue)
        val todoItemDelete: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(
            i: Int,
            userId: String?,
            title: String?,
            content: String?,
            testingTime: Long?,
            complete: Int?
        ) {
            toDoItemTitleView.text = title
            if (complete == 1) {
                toDoItemTitleView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                toDoItemContentView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                todoItemDueView.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                toDoItemTitleView.setBackgroundColor(0xFF05407E.toInt())
                toDoItemTitleView.setTextColor(0xFFFFFFFF.toInt())
            } else {
                toDoItemTitleView.paintFlags =
                    toDoItemTitleView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                toDoItemContentView.paintFlags =
                    toDoItemContentView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
                todoItemDueView.paintFlags =
                    todoItemDueView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
            toDoItemContentView.text = content
            val sdf = SimpleDateFormat("MM/dd/yyyy h:mm a")
            todoItemDueView.text = "Due: ${sdf.format(testingTime?.let { Date(it) })}"
        }

        companion object {
            fun create(parent: ViewGroup): ToDoItemViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recyclerview_item, parent, false)
                return ToDoItemViewHolder(view)
            }
        }
    }

    class ToDoItemComparator : DiffUtil.ItemCallback<ToDoItem>() {
        override fun areItemsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: ToDoItem, newItem: ToDoItem): Boolean {
            return oldItem.id == newItem.id
        }
    }
}
