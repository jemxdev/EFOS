package com.example.efos

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0) // Removed bottom padding so the bar sits flush
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan)

        // Disable clicks on the empty placeholder gap
        bottomNav.menu.findItem(R.id.nav_placeholder).isEnabled = false

        // Load Home Fragment automatically
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
            bottomNav.selectedItemId = R.id.nav_home
        }

        // FAB Click Listener (Center Button)
        fabScan.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ScanFragment())
                .commit()

            // Clear selection from the bottom nav when FAB is clicked
            bottomNav.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNav.menu.size()) {
                bottomNav.menu.getItem(i).isChecked = false
            }
            bottomNav.menu.setGroupCheckable(0, true, true)
        }

        // Standard Navigation Listener (The 4 surrounding tabs)
        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HomeFragment()).commit()
                    true
                }
                R.id.nav_history -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, HistoryFragment()).commit()
                    true
                }
                R.id.nav_defects -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, DefectsFragment()).commit()
                    true
                }
                R.id.nav_settings -> {
                    supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SettingsFragment()).commit()
                    true
                }
                else -> false
            }
        }
    }
}