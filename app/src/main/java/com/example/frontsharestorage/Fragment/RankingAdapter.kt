package com.example.frontsharestorage.Fragment

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.example.frontsharestorage.R

class RankingAdapter : ListAdapter<RankingItem, RankingAdapter.ViewHolder>(RankingItemDiffCallback()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ranking, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.nameTextView.text = item.nickName
        holder.countTextView.text = item.recordCount.toString() + "회"

        //val currentItem = rankingItems[position]

        // 뱃지 이미지 설정
        when (item.badge) {
            "실버" -> holder.badgeImage.setImageResource(R.drawable.silverbadge)
            "골드" -> holder.badgeImage.setImageResource(R.drawable.goldbadge)
            "에메랄드" -> {
                holder.badgeImage.setImageResource(R.drawable.emeraldbadge)
                val margin = 15 // 마진 값
                val layoutParams = holder.badgeImage.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, margin, 0)
                holder.badgeImage.layoutParams = layoutParams
            }
            "다이아" -> {
                holder.badgeImage.setImageResource(R.drawable.diabadge)
                val margin = 5 // 마진 값
                val layoutParams = holder.badgeImage.layoutParams as ViewGroup.MarginLayoutParams
                layoutParams.setMargins(0, 0, margin, 0)
                holder.badgeImage.layoutParams = layoutParams
            }

            "마스터" -> holder.badgeImage.setImageResource(R.drawable.masterbadge)
            "챌린저" -> holder.badgeImage.setImageResource(R.drawable.challengerbadge)
            else -> holder.badgeImage.setImageResource(R.drawable.bronzebadge)
        }

        // 이미지 로드 및 설정
        Glide.with(holder.itemView)
            .load(item.imageURL)
            .placeholder(R.drawable.defaultimage)
            .apply(RequestOptions().override(100, 100).centerCrop().transform(CircleCrop()))
            .into(holder.profileImage)

        Log.d("ㅁㄴㅇㄴㅇㅁㅈㅇ", item.imageURL.toString())
        Log.d("ㅁㄴㅇㄴㅇㅁㅈㅇ", item.badge.toString())
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.textView)
        val countTextView: TextView = itemView.findViewById(R.id.countTextView)
        val profileImage: ImageView = itemView.findViewById(R.id.imageURL)
        val badgeImage: ImageView = itemView.findViewById(R.id.badge)
    }


}