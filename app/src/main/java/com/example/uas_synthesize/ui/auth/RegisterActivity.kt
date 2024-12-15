package com.example.uas_synthesize.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.uas_synthesize.ui.MainActivity
import com.example.uas_synthesize.utils.view_model.RegisterViewModel
import com.example.uas_synthesize.databinding.ActivityRegisterBinding
import com.example.uas_synthesize.utils.PrefManager
import kotlinx.coroutines.launch

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var prefManager: PrefManager
    private val registerViewModel: RegisterViewModel by viewModels {
        ViewModelProvider.AndroidViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefManager = PrefManager.getInstance(this)

        with(binding) {
            btnRegister.setOnClickListener {
                val name = edtName.text.toString()
                val username = edtUsername.text.toString()
                val password = edtPassword.text.toString()
                val confirmPassword = edtPasswordConfirm.text.toString()
                if (username.isEmpty() || password.isEmpty() ||
                    confirmPassword.isEmpty()
                ) {
                    Toast.makeText(
                        this@RegisterActivity,
                        "Mohon isi semua data",
                        Toast.LENGTH_SHORT
                    ).show()
                } else if (password != confirmPassword) {
                    Toast.makeText(
                        this@RegisterActivity, "Password tidak sama",
                        Toast.LENGTH_SHORT
                    )
                        .show()
                } else {
                    registerUser(name, username, password)
                }
            }
            txtRegister.setOnClickListener {
                finish()
            }
        }
    }

    private fun registerUser(name: String, username: String, password: String) {
        lifecycleScope.launch {
            try {
                val isSuccess = registerViewModel.registerUser(name, username, password)
                if (isSuccess) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil", Toast.LENGTH_SHORT).show()
                    navigateToMainActivity()
                } else {
                    Toast.makeText(this@RegisterActivity, "Registrasi gagal", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@RegisterActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this@RegisterActivity, MainActivity::class.java))
        finish()
    }
}