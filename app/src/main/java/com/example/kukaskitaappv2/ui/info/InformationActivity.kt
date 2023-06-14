package com.example.kukaskitaappv2.ui.info

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kukaskitaappv2.databinding.ActivityInformationBinding

class InformationActivity : AppCompatActivity() {
    private val binding: ActivityInformationBinding by lazy {
        ActivityInformationBinding.inflate(layoutInflater)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Information"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
}