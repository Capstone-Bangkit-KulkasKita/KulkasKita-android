package com.example.kukaskitaappv2.ui.addItem

import android.app.DatePickerDialog
import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityAddItemBinding
import com.example.kukaskitaappv2.databinding.ActivityLoginBinding
import com.example.kukaskitaappv2.source.datastore.UserPreferences
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
    }


    private fun updateLable(myCalendar: Calendar) {
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat)
        binding.tvExpired.setText(sdf.format(myCalendar.time))
    }
}