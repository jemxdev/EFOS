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

        // Navigate to Scan Tab by triggering the new Floating Action Button
        btnScan.setOnClickListener {
            val fabScan = requireActivity().findViewById<FloatingActionButton>(R.id.fab_scan)
            fabScan.performClick()
        }
    }

    // onResume is called every time this screen becomes visible.
    // This makes sure our statistics update instantly if we just saved a new scan!
    override fun onResume() {
        super.onResume()
        updateDashboardStats()
    }

    private fun updateDashboardStats() {
        // Read directly from our live HistoryManager memory bank
        val totalScans = HistoryManager.scanHistory.size

        // Get the very first item in the list (the newest one), or say "None yet" if empty
        val latestDisease = HistoryManager.scanHistory.firstOrNull()?.disease ?: "None yet"

        // Update the text on the screen
        tvTotalScans.text = "• Total Scans Stored: $totalScans"
        tvLatestScan.text = "• Latest Detection: $latestDisease"
    }
}