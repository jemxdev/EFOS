package com.example.efos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class HomeFragment : Fragment() {

    private lateinit var tvTotalScans: TextView
    private lateinit var tvLatestScan: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnScan = view.findViewById<Button>(R.id.btn_home_scan)
        tvTotalScans = view.findViewById(R.id.tv_home_total_scans)
        tvLatestScan = view.findViewById(R.id.tv_home_latest_scan)

        btnScan.setOnClickListener {
            val fabScan = requireActivity().findViewById<FloatingActionButton>(R.id.fab_scan)
            fabScan.performClick()
        }
    }

    override fun onResume() {
        super.onResume()
        updateDashboardStats()
    }

    private fun updateDashboardStats() {
        // --- NEW: Pull real stats from SQLite ---
        val dbHelper = DatabaseHelper(requireContext())
        val totalScans = dbHelper.getScanCount()

        // getAllScans returns them newest first, so index 0 is the most recent
        val allScans = dbHelper.getAllScans()
        val latestDisease = allScans.firstOrNull()?.disease ?: getString(R.string.none_yet)

        tvTotalScans.text = totalScans.toString()
        tvLatestScan.text = latestDisease
    }
}