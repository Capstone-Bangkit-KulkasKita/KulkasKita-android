package com.example.kukaskitaappv2.ui.main

import android.app.Activity
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.activity.viewModels
import androidx.core.util.Pair
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
import androidx.recyclerview.widget.RecyclerView
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ItemInventoryBinding
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.source.remote.response.DeleteResponse
import com.example.kukaskitaappv2.source.remote.response.FoodResponseItem
import com.example.kukaskitaappv2.source.remote.retrofit.ApiConfig
import kotlinx.coroutines.flow.Flow
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.Instant

class FoodAdapter(private var listFood: List<FoodResponseItem>) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listFood[position]
        holder.bind(item)
//        holder.itemView.setOnClickListener{
//            val intentDetail = Intent(holder.itemView.context, DetailActivity::class.java)
//            intentDetail.putExtra("DATA", item.login)
//            intentDetail.putExtra("DATA2",item.avatarUrl)
//            holder.itemView.context.startActivity(intentDetail)
//        }
    }
    override fun getItemCount()= listFood.size

    class ViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FoodResponseItem) {
            val specificDate = Instant.parse(item.expDate)
            val currentDate = Instant.now() // Get the current date and time

            val duration = Duration.between(currentDate, specificDate).toDays()

            binding.tvInventoryName.text = item.name
            binding.tvExpired.text = "Expired in $duration days!"
            if (duration <= 2) {
                binding.tvExpired.setTextColor(ContextCompat.getColor(binding.root.context, R.color.red_200))
                val notificationManager = ContextCompat.getSystemService(binding.root.context, NotificationManager::class.java)
                notificationManager?.let {
                    val channelId = "channel01"
                    val notificationBuilder = NotificationCompat.Builder(binding.root.context, channelId)
                        .setContentTitle("Food Expiration Notification")
                        .setContentText("The food item '${item.name}' is expiring soon!")
                        .setSmallIcon(R.drawable.ic_notifications_active_24)
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    // Set the notification channel if the device is running Android 8.0 (API level 26) or higher
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        val channel = NotificationChannel(channelId, "Your Channel Name", NotificationManager.IMPORTANCE_DEFAULT)
                        notificationManager.createNotificationChannel(channel)
                        notificationBuilder.setChannelId(channelId)
                    }

                    // Build and show the notification
                    val notification = notificationBuilder.build()
                    it.notify(1, notification)
                }
            }


//            binding.btnRemove.setOnClickListener {
//                val client = ApiConfig.getApiService().deleteFood()
//                client.enqueue(object : Callback<List<DeleteResponse>> {
//                    override fun onResponse(
//                        call: Call<List<DeleteResponse>>,
//                        response: Response<List<DeleteResponse>>
//                    ) {
//                        if (response.isSuccessful) {
//                           _follower.value = response.body()
//                        } else {
//                            Log.e(TAG, "onSuccess: ${response.message()}")
//                        }
//                    }
//                    override fun onFailure(call: Call<List<DeleteResponse>>, t: Throwable) {
//                        Log.e(TAG, "onFailure: ${t.message.toString()}")
//                    }
//
//                })
//            }
        }
    }

    companion object {
        private const val NOTIFICATION_ID = 1
        private const val CHANNEL_ID = "channel_01"
        private const val CHANNEL_NAME = "dicoding channel"
    }
}