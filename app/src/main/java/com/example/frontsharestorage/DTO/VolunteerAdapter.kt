package com.example.frontsharestorage.DTO

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontsharestorage.R

class VolunteerAdapter(
    private val volunteerList: MutableList<VolunteerData>,
    private val onDeleteItemClickListener: OnDeleteItemClickListener
) : RecyclerView.Adapter<VolunteerAdapter.ViewHolder>() {

    interface OnDeleteItemClickListener {
        fun onDeleteItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.item_volunteerTitle)
        val locationTextView: TextView = itemView.findViewById(R.id.item_volunterLocation)
        val dateTextView: TextView = itemView.findViewById(R.id.item_volunteerDate)
        val startTimeTextView: TextView = itemView.findViewById(R.id.item_volunteerStartTime)
        val endTimeTextView: TextView = itemView.findViewById(R.id.item_volunteerEndTime)
        val deleteItemButton: ImageView = itemView.findViewById(R.id.delete_item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_record_volunteer, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = volunteerList[position]

        holder.titleTextView.text = data.title
        holder.locationTextView.text = data.location
        holder.dateTextView.text = data.date
        holder.startTimeTextView.text = data.startTime
        holder.endTimeTextView.text = data.endTime

        // 삭제 버튼 클릭 리스너 추가
        holder.deleteItemButton.setOnClickListener {
            onDeleteItemClickListener.onDeleteItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return volunteerList.size
    }

    fun removeItem(position: Int) {
        volunteerList.removeAt(position)
        notifyItemRemoved(position)
    }
}