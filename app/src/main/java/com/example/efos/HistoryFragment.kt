package com.example.efos

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.annotation.SuppressLint

class HistoryFragment : Fragment() {

    private var isSortedNewest = true
    private lateinit var adapter: HistoryRecyclerAdapter
    private lateinit var historyList: MutableList<HistoryItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view_history)
        val btnSort = view.findViewById<Button>(R.id.btn_sort_date)
        val btnExport = view.findViewById<Button>(R.id.btn_export_excel)

        // --- NEW: Load real database items ---
        val dbHelper = DatabaseHelper(requireContext())
        historyList = dbHelper.getAllScans().toMutableList()

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryRecyclerAdapter(historyList)
        recyclerView.adapter = adapter

        btnSort.setOnClickListener {
            isSortedNewest = !isSortedNewest
            if (isSortedNewest) {
                btnSort.text = getString(R.string.sort_newest)
                historyList.sortByDescending { it.date }
            } else {
                btnSort.text = getString(R.string.sort_oldest)
                historyList.sortBy { it.date }
            }

            @SuppressLint("NotifyDataSetChanged")
            adapter.notifyDataSetChanged()
        }

        btnExport.setOnClickListener { simulateExportToCSV() }
    }

    private fun simulateExportToCSV() {
        val csvBuilder = StringBuilder()
        csvBuilder.append("Disease,Date,Severity\n")

        for ((disease, date, severity) in historyList) {
            csvBuilder.append("$disease,$date,$severity\n")
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Export Successful")
            .setMessage("Scan history has been exported to:\n\nDownloads/EFOS_History.csv")
            .setPositiveButton("OK") { dialog, _ ->
                Toast.makeText(requireContext(), "Saved as EFOS_History.csv", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .show()
    }
}