package com.example.frontsharestorage.Fragment


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.frontsharestorage.R

class BadgeAdapter(private val badgeList: List<String>) : RecyclerView.Adapter<BadgeAdapter.BadgeViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BadgeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_badge, parent, false)
        return BadgeViewHolder(view)
    }

    override fun onBindViewHolder(holder: BadgeViewHolder, position: Int) {
        val badge = badgeList[position]
        holder.bind(badge, position)
    }

    override fun getItemCount(): Int {
        return badgeList.size
    }

    class BadgeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(badge: String, position: Int) {
            // 뱃지에 대한 표시 로직
            val badgeTextView: TextView = itemView.findViewById(R.id.badge_name)
            badgeTextView.text = badge
            val badgeImageView: ImageView = itemView.findViewById(R.id.badge_image)

            // 각 뱃지별 이미지 리소스 이름 결정
            val imageResource = when (position) {
                1 -> R.drawable.silverbadge
                2 -> R.drawable.goldbadge
                3 -> R.drawable.emeraldbadge
                4 -> R.drawable.diabadge
                5 -> R.drawable.masterbadge
                6 -> R.drawable.challengerbadge
                else -> R.drawable.bronzebadge
            }

            val textResource = when (position) {
                1 -> R.string.silver_text
                2 -> R.string.gold_text
                3 -> R.string.emerald_text
                4 -> R.string.dia_text
                5 -> R.string.master_text
                6 -> R.string.challenger_text
                else -> R.string.bronze_text
            }

            // 이미지 설정
            badgeImageView.setImageResource(imageResource)
            badgeTextView.setText(textResource)
        }
    }
}