package com.example.efos

import android.content.Context
import android.content.res.Configuration
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
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val switchDarkMode = view.findViewById<SwitchMaterial>(R.id.switch_dark_mode)
        val switchDevMode = view.findViewById<SwitchMaterial>(R.id.switch_dev_mode)
        val btnClearHistory = view.findViewById<Button>(R.id.btn_clear_history)
        val btnAbout = view.findViewById<Button>(R.id.btn_about)

        val sharedPrefs = requireActivity().getSharedPreferences("EFOS_PREFS", Context.MODE_PRIVATE)

        // 1. Correctly detect if the screen is CURRENTLY in dark mode
        val currentNightMode = resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
        switchDarkMode.isChecked = (currentNightMode == Configuration.UI_MODE_NIGHT_YES)

        switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            val newMode = if (isChecked) AppCompatDelegate.MODE_NIGHT_YES else AppCompatDelegate.MODE_NIGHT_NO

            // Save the manual override
            sharedPrefs.edit().putInt("DARK_MODE", newMode).apply()

            // Apply theme instantly
            AppCompatDelegate.setDefaultNightMode(newMode)
        }

        switchDevMode.setOnCheckedChangeListener { _, isChecked ->
            val status = if (isChecked) "Enabled" else "Disabled"
            Toast.makeText(requireContext(), "Developer Mode $status", Toast.LENGTH_SHORT).show()
        }

        btnClearHistory.setOnClickListener {
            Toast.makeText(requireContext(), "SQLite Scan History Cleared!", Toast.LENGTH_SHORT).show()
        }

        btnAbout.setOnClickListener {
            Toast.makeText(requireContext(), "EFOS v1.0 - La Union Research Project", Toast.LENGTH_LONG).show()
        }
    }
}