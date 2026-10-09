package com.example.efos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.io.File

data class HistoryItem(
    val disease: String,
    val date: String,
    val severity: String,
    val imageRes: Int? = null,
    val imageBitmap: Bitmap? = null,
    val imagePath: String? = null
)

class HistoryRecyclerAdapter(private val dataSource: List<HistoryItem>) :
    RecyclerView.Adapter<HistoryRecyclerAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val titleTextView: TextView = view.findViewById(R.id.tv_history_disease)
        val dateTextView: TextView = view.findViewById(R.id.tv_history_date)
        val severityTextView: TextView = view.findViewById(R.id.tv_history_severity)
        val imageView: ImageView = view.findViewById(R.id.iv_history_thumb)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = dataSource[position]

        holder.titleTextView.text = item.disease
        holder.dateTextView.text = item.date
        holder.severityTextView.text = item.severity

        // --- NEW: Prioritize loading the saved image file from internal storage ---
        if (item.imagePath != null && File(item.imagePath).exists()) {
            val bitmap = BitmapFactory.decodeFile(item.imagePath)
            holder.imageView.setImageBitmap(bitmap)
        } else if (item.imageBitmap != null) {
            holder.imageView.setImageBitmap(item.imageBitmap)
        } else if (item.imageRes != null) {
            holder.imageView.setImageResource(item.imageRes)
        }
    }

    override fun getItemCount(): Int = dataSource.size
}