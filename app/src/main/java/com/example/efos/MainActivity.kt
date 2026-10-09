package com.example.efos

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.get
import androidx.core.view.size
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    // Change these from 'val' to 'lateinit var' so we don't accidentally create duplicates
    private lateinit var homeFragment: Fragment
    private lateinit var historyFragment: Fragment
    private lateinit var defectsFragment: Fragment
    private lateinit var settingsFragment: Fragment
    private lateinit var scanFragment: Fragment
    private lateinit var activeFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        val sharedPrefs = getSharedPreferences("EFOS_PREFS", Context.MODE_PRIVATE)
        val savedMode = sharedPrefs.getInt("DARK_MODE", AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        AppCompatDelegate.setDefaultNightMode(savedMode)

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        val fabScan = findViewById<FloatingActionButton>(R.id.fab_scan)
        bottomNav.menu.findItem(R.id.nav_placeholder).isEnabled = false

        if (savedInstanceState == null) {
            // First time opening the app: Create new fragments
            homeFragment = HomeFragment()
            historyFragment = HistoryFragment()
            defectsFragment = DefectsFragment()
            settingsFragment = SettingsFragment()
            scanFragment = ScanFragment()
            activeFragment = homeFragment

            supportFragmentManager.beginTransaction().apply {
                add(R.id.fragment_container, settingsFragment, "settings").hide(settingsFragment)
                add(R.id.fragment_container, defectsFragment, "defects").hide(defectsFragment)
                add(R.id.fragment_container, historyFragment, "history").hide(historyFragment)
                add(R.id.fragment_container, scanFragment, "scan").hide(scanFragment)
                add(R.id.fragment_container, homeFragment, "home")
            }.commit()

            bottomNav.selectedItemId = R.id.nav_home
        } else {
            // Screen rotated or Dark Mode toggled: Find the surviving fragments by their tags!
            homeFragment = supportFragmentManager.findFragmentByTag("home")!!
            historyFragment = supportFragmentManager.findFragmentByTag("history")!!
            defectsFragment = supportFragmentManager.findFragmentByTag("defects")!!
            settingsFragment = supportFragmentManager.findFragmentByTag("settings")!!
            scanFragment = supportFragmentManager.findFragmentByTag("scan")!!

            // Physically check which fragment the system kept visible.
            activeFragment = when {
                !settingsFragment.isHidden -> settingsFragment
                !defectsFragment.isHidden -> defectsFragment
                !historyFragment.isHidden -> historyFragment
                !scanFragment.isHidden -> scanFragment
                else -> homeFragment
            }
        }

        fabScan.setOnClickListener {
            fabScan.animate()
                .scaleX(1.2f).scaleY(1.2f)
                .setDuration(120)
                .withEndAction {
                    fabScan.animate().scaleX(1f).scaleY(1f).setDuration(120).start()
                }
                .start()

            supportFragmentManager.beginTransaction()
                .hide(activeFragment)
                .show(scanFragment)
                .commit()
            activeFragment = scanFragment

            bottomNav.menu.setGroupCheckable(0, true, false)
            for (i in 0 until bottomNav.menu.size) {
                bottomNav.menu[i].isChecked = false
            }
            bottomNav.menu.setGroupCheckable(0, true, true)
        }

        bottomNav.setOnItemSelectedListener { item ->
            val itemView = bottomNav.findViewById<View>(item.itemId)
            itemView?.let {
                it.animate().scaleX(0.85f).scaleY(0.85f).setDuration(100)
                    .withEndAction {
                        it.animate().scaleX(1.0f).scaleY(1.0f).setDuration(100).start()
                    }.start()
            }

            val selectedFragment = when (item.itemId) {
                R.id.nav_home -> homeFragment
                R.id.nav_history -> historyFragment
                R.id.nav_defects -> defectsFragment
                R.id.nav_settings -> settingsFragment
                else -> return@setOnItemSelectedListener false
            }

            if (activeFragment != selectedFragment) {
                supportFragmentManager.beginTransaction()
                    .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                    .hide(activeFragment)
                    .show(selectedFragment)
                    .commit()
                activeFragment = selectedFragment
            }
            true
        }
    }
}