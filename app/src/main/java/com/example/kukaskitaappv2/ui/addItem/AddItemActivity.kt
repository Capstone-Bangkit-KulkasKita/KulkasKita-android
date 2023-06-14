package com.example.kukaskitaappv2.ui.addItem

import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityAddItemBinding
import com.example.kukaskitaappv2.databinding.ActivityLoginBinding
import com.example.kukaskitaappv2.helper.ResultState
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.ui.login.LoginActivity
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
class AddItemActivity : AppCompatActivity() {
    private val binding: ActivityAddItemBinding by lazy {
        ActivityAddItemBinding.inflate(layoutInflater)
    }

    private val addItemViewModel: AddItemViewModel by viewModels {
        AddItemViewModel.AddItemViewModelFactory.getInstance(
            this,
            UserPreferences.getInstance(dataStore)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.title = "Add Item"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val myCalendar = Calendar.getInstance()
        val datePicker = DatePickerDialog.OnDateSetListener { view, year, month, day ->
            myCalendar.set(Calendar.YEAR,year)
            myCalendar.set(Calendar.MONTH,month)
            myCalendar.set(Calendar.DAY_OF_MONTH,day)
            updateLable(myCalendar)
        }

        binding.btnInputDate.setOnClickListener {
            DatePickerDialog(this,datePicker,myCalendar.get(Calendar.YEAR),myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        binding.btnAddItem.setOnClickListener {
            addItemViewModel.checkToken().observe(this){
                if (it == "null") {
                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                } else {
                    uploadItem("Bearer $it")
                }
            }
        }
    }

    private fun uploadItem(myToken: String) {
        if(binding.tvExpired.text.isNullOrEmpty() || binding.etItemName.text.isNullOrEmpty()){
            Toast.makeText(this, getString(R.string.field_cannot_empty), Toast.LENGTH_SHORT).show()
        }else{
            binding.progressBar.visibility = View.VISIBLE
            val name = binding.etItemName.text.toString()
            val expDate = binding.tvExpired.text.toString()
            val result = addItemViewModel.addItem(myToken, name, expDate)
            result.observe(this) {
                when (it) {
                    is ResultState.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE
                    }

                    is ResultState.Error -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        val error = it.error
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }

                    is ResultState.Success -> {
                        binding.progressBar.visibility = View.INVISIBLE
                        Toast.makeText(this, getString(R.string.succes_addItem), Toast.LENGTH_SHORT)
                            .show()
                        val intent = Intent(this, AddItemActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkSession()
    }

    private fun checkSession() {
        addItemViewModel.checkToken().observe(this) {
            if (it == "null") {
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }


    private fun updateLable(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat)
        binding.tvExpired.setText(sdf.format(myCalendar.time))
    }
}