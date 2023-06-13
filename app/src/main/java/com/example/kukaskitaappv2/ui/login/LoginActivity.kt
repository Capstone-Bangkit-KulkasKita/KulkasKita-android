package com.example.kukaskitaappv2.ui.login

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.kukaskitaappv2.ui.main.MainActivity
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityLoginBinding
import com.example.kukaskitaappv2.helper.ResultState
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.ui.register.RegisterActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
class LoginActivity : AppCompatActivity() {
    private val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private val loginViewModel:LoginViewModel by viewModels {
        LoginViewModel.LoginViewModelFactory.getInstance(
            this,
            UserPreferences.getInstance(dataStore)
        )
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        hideSystemUI()

        binding.btnLogin.setOnClickListener {
            val email = binding.etLoginEmail.text.toString()
            val password = binding.etLoginPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() &&
                binding.etLoginPassword.error.isNullOrEmpty() && binding.etLoginEmail.error.isNullOrEmpty()) {
                val result = loginViewModel.login(email, password)
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
                            val data = it.data
                            loginViewModel.saveToken(data.loginResult?.token.toString())
                            val intent = Intent(this, MainActivity::class.java)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            } else {
                binding.etLoginEmail.error = resources.getString(R.string.empty_email)
                binding.etLoginPassword.error = resources.getString(R.string.empty_password)

                if (binding.etLoginPassword.text.isNullOrEmpty()) {
                    binding.etLoginPassword.error = resources.getString(R.string.password_minimum)
                }
            }
        }

        binding.linkRegister.setOnClickListener {
            startActivity(
                Intent(
                    this, RegisterActivity::class.java
                )
            )
        }
    }

    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(android.R.id.content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }
}