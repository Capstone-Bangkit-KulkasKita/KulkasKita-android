package com.example.kukaskitaappv2.ui.register

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
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
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityRegisterBinding
import com.example.kukaskitaappv2.helper.ResultState
import com.example.kukaskitaappv2.ui.login.LoginActivity

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
class RegisterActivity : AppCompatActivity() {
    private val binding: ActivityRegisterBinding by lazy {
        ActivityRegisterBinding.inflate(layoutInflater)
    }

    private val registerViewModel: RegisterViewModel by viewModels{
        RegisterViewModel.RegisterViewModelFactory.getInstance(this, dataStore)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(binding.root)

        hideSystemUI()

        playAnimation()

        binding.btnRegister.setOnClickListener {
            val username = binding.etRegUsername.text.toString()
            val email = binding.etRegEmail.text.toString()
            val password = binding.etRegPassword.text.toString()
            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty() &&
                 binding.etRegEmail.error.isNullOrEmpty() && binding.etRegPassword.error.isNullOrEmpty() && binding.etRegUsername.error.isNullOrEmpty()){
                val result = registerViewModel.register(username, email, password)
                result.observe(this){
                    when(it){
                        is ResultState.Error -> {
                            val error = it.error
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                        }
                        is ResultState.Success -> {
                            binding.progressBar.visibility = View.INVISIBLE
                            Toast.makeText(this, getString(R.string.register_success), Toast.LENGTH_SHORT).show()
                            val intent = Intent(this, LoginActivity::class.java)
                            startActivity(intent)
                        }
                        is ResultState.Loading -> {
                            binding.progressBar.visibility = View.VISIBLE
                        }
                    }
                }
            } else {
                if (email.isEmpty()) binding.etRegEmail.error = getString(R.string.empty_email)
                if (email.isEmpty()) binding.etRegPassword.error = getString(R.string.password_minimum)
            }
        }

        binding.linkLogin.setOnClickListener {
            startActivity(
                Intent(
                    this, LoginActivity::class.java
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

    private fun playAnimation() {
        val title = ObjectAnimator.ofFloat(binding.tvRegTitle, View.ALPHA, 1f).setDuration(300)
        val textEmail = ObjectAnimator.ofFloat(binding.tvEmail, View.ALPHA, 1f).setDuration(300)
        val inputEmail = ObjectAnimator.ofFloat(binding.etRegEmail, View.ALPHA, 1f).setDuration(300)
        val textPassword = ObjectAnimator.ofFloat(binding.tvPassword, View.ALPHA, 1f).setDuration(300)
        val inputPassword = ObjectAnimator.ofFloat(binding.etRegPassword, View.ALPHA, 1f).setDuration(300)
        val btnRegister = ObjectAnimator.ofFloat(binding.btnRegister, View.ALPHA, 1f).setDuration(300)
        val login = ObjectAnimator.ofFloat(binding.linkLogin, View.ALPHA, 1f).setDuration(300)

        val input = AnimatorSet().apply {
            playTogether(textEmail,inputEmail,textPassword,inputPassword)
        }


        AnimatorSet().apply {
            playSequentially(title, input, btnRegister, login)
            start()
        }
    }
}