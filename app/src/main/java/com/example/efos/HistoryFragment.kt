package com.example.efos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment

class HistoryFragment : Fragment() {

    private var isSortedNewest = true
    private lateinit var adapter: HistoryAdapter
    private lateinit var historyList: MutableList<HistoryItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val listView = view.findViewById<ListView>(R.id.list_view_history)
        val btnSort = view.findViewById<Button>(R.id.btn_sort_date)
        val btnExport = view.findViewById<Button>(R.id.btn_export_excel)

        // Load the live data from our HistoryManager
        historyList = HistoryManager.scanHistory

        adapter = HistoryAdapter(requireContext(), historyList)
        listView.adapter = adapter

        // Sort Button Logic
        btnSort.setOnClickListener {
            isSortedNewest = !isSortedNewest
            if (isSortedNewest) {
                btnSort.text = "Sort: Newest"
                // Sort string dates (works for demo purposes)
                historyList.sortByDescending { it.date }
            } else {
                btnSort.text = "Sort: Oldest"
                historyList.sortBy { it.date }
            }
            adapter.notifyDataSetChanged()
        }

        // Export Button Logic
        btnExport.setOnClickListener {
            simulateExportToCSV()
        }
    }

    private fun simulateExportToCSV() {
        // Build the actual CSV format string behind the scenes
        val csvBuilder = StringBuilder()
        csvBuilder.append("Disease,Date,Severity\n")
        for (item in historyList) {
            csvBuilder.append("${item.disease},${item.date},${item.severity}\n")
        }

        // Simulate saving it to the phone's storage
        AlertDialog.Builder(requireContext())
            .setTitle("Export Successful")
            .setMessage("Scan history has been exported to:\n\nDownloads/EFOS_History.csv\n\n(This file can be opened directly in Microsoft Excel).")
            .setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(requireContext(), "Saved as EFOS_History.csv", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }
}