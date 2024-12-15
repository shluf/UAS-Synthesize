package com.example.uas_synthesize.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import com.example.uas_synthesize.R
import com.example.uas_synthesize.databinding.ActivityMainBinding
import com.example.uas_synthesize.ui.auth.LoginActivity
import com.example.uas_synthesize.utils.PrefManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefManager = PrefManager.getInstance(this)
        checkLoginStatus()

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        if (prefManager.isGuest()) {
            setupGuestView(navController)
        } else {
            setupDefaultView(navController)
        }

    }

    private fun checkLoginStatus() {
        val isLoggedIn = prefManager.isLoggedIn()
        if (!isLoggedIn) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun setupGuestView(navController: NavController) {
        val navGraph = navController.navInflater.inflate(R.navigation.nav_graph_guest)
        navController.graph = navGraph

        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)

        binding.bottomNavigationView.menu.clear()
        binding.bottomNavigationView.inflateMenu(R.menu.bottom_nav_menu_guest)
    }

    private fun setupDefaultView(navController: NavController) {
        NavigationUI.setupWithNavController(binding.bottomNavigationView, navController)
    }
}
