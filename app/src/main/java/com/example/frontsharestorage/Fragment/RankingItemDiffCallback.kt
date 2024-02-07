package com.example.frontsharestorage.Fragment

import androidx.recyclerview.widget.DiffUtil

class RankingItemDiffCallback : DiffUtil.ItemCallback<RankingItem>() {

    override fun areItemsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
        // 아이템이 같은지 확인하는 로직을 여기에 작성합니다.
        return oldItem.recordCount == newItem.recordCount
    }

    override fun areContentsTheSame(oldItem: RankingItem, newItem: RankingItem): Boolean {
        // 내용이 같은지 확인하는 로직을 여기에 작성합니다.
        return oldItem == newItem
    }
}