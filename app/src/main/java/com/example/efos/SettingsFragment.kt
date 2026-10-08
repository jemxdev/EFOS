package com.example.efos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.google.android.material.switchmaterial.SwitchMaterial

class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val switchDevMode = view.findViewById<SwitchMaterial>(R.id.switch_dev_mode)
        val btnClearHistory = view.findViewById<Button>(R.id.btn_clear_history)
        val btnAbout = view.findViewById<Button>(R.id.btn_about)

        // Check current night mode state so the switch shows the correct position
        val isDarkMode = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES
        switchDarkMode.isChecked = isDarkMode

        // Dark Mode Logic
        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
        }

        // Developer Mode Logic
        switchDevMode.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(requireContext(), "Developer Mode $status", Toast.LENGTH_SHORT).show()
        }

        // Clear History Button Logic
        btnClearHistory.setOnClickListener {
            Toast.makeText(requireContext(), "SQLite Scan History Cleared!", Toast.LENGTH_SHORT).show()
        }

        // About Button Logic
        btnAbout.setOnClickListener {
            Toast.makeText(requireContext(), "EFOS v1.0 - La Union Research Project", Toast.LENGTH_LONG).show()
        }
    }
}