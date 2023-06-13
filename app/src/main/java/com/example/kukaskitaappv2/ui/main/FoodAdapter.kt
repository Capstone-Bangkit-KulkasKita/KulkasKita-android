package com.example.kukaskitaappv2.ui.main

import android.app.Activity
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.util.Pair
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.kukaskitaappv2.databinding.ItemInventoryBinding
import com.example.kukaskitaappv2.source.remote.response.FoodItem

class FoodAdapter: PagingDataAdapter<FoodItem, FoodAdapter.FoodViewHolder>(DIFF_ITEM_CALLBACK) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodViewHolder(binding)
    }


    override fun onBindViewHolder(holder: FoodViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(story)
        }
    }

    class FoodViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(food: FoodItem) {
            binding.apply {
                tvInventoryName.text = food.name
                tvExpired.text = food.expDate

//                itemView.setOnClickListener {
//                    val intent = Intent(itemView.context, DetailActivity::class.java)
//                    intent.putExtra(EXTRA_DATA, story)
//
//                    val optionsCompat: ActivityOptionsCompat =
//                        ActivityOptionsCompat.makeSceneTransitionAnimation(
//                            itemView.context as Activity,
//                            Pair(ivStoryPhoto, "story"),
//                            Pair(tvStoryName, "name"),
//                            Pair(tvStoryDesc, "desc")
//                        )
//                    itemView.context.startActivity(intent, optionsCompat.toBundle())
//                }
            }
        }
    }



    companion object {
        val DIFF_ITEM_CALLBACK = object : DiffUtil.ItemCallback<FoodItem>() {
            override fun areItemsTheSame(
                oldStory: FoodItem,
                newStory: FoodItem
            ): Boolean {
                return oldStory == newStory
            }

            override fun areContentsTheSame(
                oldStory: FoodItem,
                newStory: FoodItem
            ): Boolean {
                return oldStory.name == newStory.name &&
                        oldStory.expDate == newStory.expDate
            }
        }
    }

}