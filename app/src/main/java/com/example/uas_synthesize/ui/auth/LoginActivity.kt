package com.example.uas_synthesize.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.uas_synthesize.ui.MainActivity
import com.example.uas_synthesize.utils.view_model.LoginViewModel
import com.example.uas_synthesize.databinding.ActivityLoginBinding
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var prefManager: PrefManager
    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        with(binding){
            btnLogin.setOnClickListener {
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                if (username.isEmpty() || password.isEmpty()) {
                    Toast.makeText(
                        this@LoginActivity,
                        "Mohon isi semua data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    loginUser(username, password)
                }
            }
            btnGuest.setOnClickListener {
                guestLogin()
            }
            txtRegister.setOnClickListener {
                startActivity(
                    Intent(this@LoginActivity,
                        RegisterActivity::class.java)
                )
            }
        }
    }

    private fun loginUser(username: String, password: String) {
        lifecycleScope.launch {
            try {
                val user = loginViewModel.loginUser(username, password)
                if (user != null) {
                    prefManager.setLoggedIn(true)
                    prefManager.setGuest(false)
                    prefManager.saveUsername(user.username)
                    prefManager.saveUserId(user._id)
                    prefManager.saveProfile(user.profile.name, user.profile.avatar, user.profile.bio)

                    Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    Toast.makeText(this@LoginActivity, "Username atau password salah", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@LoginActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guestLogin() {
        prefManager.setLoggedIn(true)
        prefManager.setGuest(true)
        Toast.makeText(this@LoginActivity, "Login berhasil", Toast.LENGTH_SHORT).show()
        navigateToMainActivity()
    }

    private fun navigateToMainActivity() {
        startActivity(
            Intent(this@LoginActivity, MainActivity::class.java)
        )
        finish()
    }
}