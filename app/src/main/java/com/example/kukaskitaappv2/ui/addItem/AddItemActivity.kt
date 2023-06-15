package com.example.kukaskitaappv2.ui.addItem

import android.Manifest
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityAddItemBinding
import com.example.kukaskitaappv2.helper.ResultState
import com.example.kukaskitaappv2.ml.ConvertedModel
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.ui.login.LoginActivity
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.io.IOException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.min

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
class AddItemActivity : AppCompatActivity() {
    private var fabVisible = true
    private val imageSize: Int = 128
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

        fabVisible = false

        binding.fabAddFromCamera.setOnClickListener {
            if (!fabVisible) {
                binding.fabOpenCamera.show()
                binding.fabOpenGallery.show()
                binding.fabOpenCamera.visibility = View.VISIBLE
                binding.fabOpenGallery.visibility = View.VISIBLE
                binding.fabAddFromCamera.setImageDrawable(resources.getDrawable(R.drawable.ic_close_24))
                fabVisible = true
            }else{
                binding.fabOpenCamera.hide()
                binding.fabOpenGallery.hide()
                binding.fabOpenCamera.visibility = View.GONE
                binding.fabOpenGallery.visibility = View.GONE
                binding.fabAddFromCamera.setImageDrawable(resources.getDrawable(R.drawable.ic_camera))
                fabVisible = false
            }
        }

        binding.fabOpenCamera.setOnClickListener{
            if(checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED){
                val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                startActivityForResult(cameraIntent, 3)
            }else{
                val cameraPermission = Manifest.permission.CAMERA
                val requestCode = 100

                requestPermissions(arrayOf(cameraPermission), requestCode)
            }
        }

        binding.fabOpenGallery.setOnClickListener{
            val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(galleryIntent, 1)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == RESULT_OK){
            if(requestCode == 3){
                var image: Bitmap = data?.extras?.get("data") as Bitmap
                val dimension: Int = min(image.width, image.height)

                image = Bitmap.createScaledBitmap(image,imageSize,imageSize,false)
                classifyImage(image)
            }else{
                val dataUri: Uri? = data?.data
                var image: Bitmap? = null
                try {
                    dataUri?.let { uri ->
                        image = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
                image = image?.let { Bitmap.createScaledBitmap(it, imageSize, imageSize, false) }
                image?.let { classifyImage(it) }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun classifyImage(image: Bitmap) {
        val model = ConvertedModel.newInstance(applicationContext)

        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 128, 128, 3), DataType.FLOAT32)
        val byteBuffer = ByteBuffer.allocateDirect(4 * imageSize * imageSize * 3)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(imageSize * imageSize)
        image.getPixels(intValues, 0, image.width, 0, 0, image.width, image.height)
        var pixel = 0
        for (i in 0 until imageSize) {
            for (j in 0 until imageSize) {
                val value = intValues[pixel++]
                byteBuffer.putFloat(((value shr 16) and 0xFF) * (1f / 255))
                byteBuffer.putFloat(((value shr 8) and 0xFF) * (1f / 255))
                byteBuffer.putFloat((value and 0xFF) * (1f / 255))
            }
        }
        inputFeature0.loadBuffer(byteBuffer)

        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        val confidences = outputFeature0.floatArray
        var maxPos = 0
        var maxConfidence = 0f
        for (i in confidences.indices) {
            if (confidences[i] > maxConfidence) {
                maxConfidence = confidences[i]
                maxPos = i
            }
        }
        val classes = arrayOf(
            "Alpukat", "Anggur", "Apel", "Brokoli", "Buah naga", "Ceker ayam", "Dada ayam", "Durian", "Jagung",
            "Jambu Air", "Jeruk", "Kacang Mete", "Kacang polong", "Kacang tanah", "Kangkung", "Kelapa",
            "Kembang Kol", "Kentang", "Klengkeng", "Kulit ayam", "Labu", "Labu Siam", "Leci", "Lemon",
            "Lobak Merah", "Mangga", "Melon", "Mentimun", "Nanas", "Nangka", "Nasi putih", "Paprika",
            "Pare", "Pepaya", "Pir", "Pisang", "Pokcoy", "Rambutan", "Salmon", "Semangka", "Singkong",
            "Stroberi", "Tahu", "Tauge", "Telur", "Tempe", "Terong", "Tomat", "Ubi", "Wortel"
        )
        binding.etItemName.text = Editable.Factory.getInstance().newEditable(classes[maxPos])

        model.close()
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
        binding.tvExpired.text = sdf.format(myCalendar.time)
    }
}