package com.example.kukaskitaappv2.ui.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.app.NotificationCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ItemInventoryBinding
import com.example.kukaskitaappv2.source.remote.response.DeleteResponse
import com.example.kukaskitaappv2.source.remote.response.FoodResponseItem
import com.example.kukaskitaappv2.source.remote.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.Duration
import java.time.Instant


class FoodAdapter(private var listFood: List<FoodResponseItem>, var myToken: String) : RecyclerView.Adapter<FoodAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    var token = myToken
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listFood[position]
        holder.bind(item, token.toString())
    }
    override fun getItemCount()= listFood.size

    fun removeProduct(model: FoodResponseItem) {
        val position = listFood.indexOf(model)
        listFood.filter { it.id != model.id }
        notifyItemRemoved(position)
    }



    class ViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: FoodResponseItem, token: String) {
            val specificDate = Instant.parse(item.expDate)
            val currentDate = Instant.now() // Get the current date and time


            val duration = Duration.between(currentDate, specificDate).toDays()

            binding.tvInventoryName.text = item.name
            binding.tvExpired.text = "Expired in $duration days!"
            if(duration < 0){
                deleteFood(token,item.id)
            }

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


            binding.btnRemove.setOnClickListener {
                deleteFood(token,item.id)

            }
        }

        private fun deleteFood(token:String, id:Int) {
            val client = ApiConfig.getApiService().deleteFood(token = token, id = id)
            client.enqueue(object : Callback<DeleteResponse>{
                override fun onResponse(
                    call: Call<DeleteResponse>,
                    response: Response<DeleteResponse>
                ) {
                    if (response.isSuccessful) {
                        val intent = Intent(binding.btnRemove.context, MainActivity::class.java)
                        binding.btnRemove.context.startActivity(intent)
                        Log.d("Success", response.body().toString())
                    } else {
                        Log.e(TAG, "onSuccess: ${response.message()}")
                    }
                }
                override fun onFailure(call: Call<DeleteResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message.toString()}")
                }

            })
        }
    }

}