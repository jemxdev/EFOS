package com.example.efos

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

// Updated to hold either an Int (stock image) or a Bitmap (real camera photo)
data class HistoryItem(
    val disease: String,
    val date: String,
    val severity: String,
    val imageRes: Int? = null,
    val imageBitmap: Bitmap? = null
)

class HistoryAdapter(private val context: Context, private val dataSource: List<HistoryItem>) : BaseAdapter() {

    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int = dataSource.size

    override fun getItem(position: Int): Any = dataSource[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val rowView = convertView ?: inflater.inflate(R.layout.item_history, parent, false)

        val titleTextView = rowView.findViewById<TextView>(R.id.tv_history_disease)
        val dateTextView = rowView.findViewById<TextView>(R.id.tv_history_date)
        val severityTextView = rowView.findViewById<TextView>(R.id.tv_history_severity)
        val imageView = rowView.findViewById<ImageView>(R.id.iv_history_thumb)

        val item = getItem(position) as HistoryItem

        titleTextView.text = item.disease
        dateTextView.text = item.date
        severityTextView.text = item.severity

        // Prioritize showing the real photo if it exists, otherwise fall back to stock images
        if (item.imageBitmap != null) {
            imageView.setImageBitmap(item.imageBitmap)
        } else if (item.imageRes != null) {
            imageView.setImageResource(item.imageRes)
        }

        return rowView
    }
}