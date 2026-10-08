package com.example.efos

import android.graphics.Bitmap
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object HistoryManager {
    // This list acts as our live database for the prototype
    val scanHistory = mutableListOf(
        HistoryItem("Phomopsis Blight", "Oct 5, 2026 - 2:15 PM", "45% DSP", imageRes = R.drawable.img_phomopsis),
        HistoryItem("Tobacco Caterpillar", "Oct 4, 2026 - 9:30 AM", "60% DSP", imageRes = R.drawable.img_tobacco_caterpillar),
        HistoryItem("Verticillium Wilt", "Oct 2, 2026 - 4:10 PM", "85% DSP", imageRes = R.drawable.img_verticillium),
        HistoryItem("Healthy Eggplant Leaf", "Oct 1, 2026 - 11:00 AM", "0% DSP", imageRes = R.drawable.img_healthy)
    )

    fun addScan(disease: String, dsp: Int, imageBitmap: Bitmap) {
        val dateFormat = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault())
        val currentDate = dateFormat.format(Date())
        val severityStr = "$dsp% DSP"

        // Scale down the image to 300px wide to prevent memory crashes during the demo
        val aspectRatio = imageBitmap.width.toFloat() / imageBitmap.height.toFloat()
        val thumbWidth = 300
        val thumbHeight = (thumbWidth / aspectRatio).toInt()
        val thumbnail = Bitmap.createScaledBitmap(imageBitmap, thumbWidth, thumbHeight, true)

        // Add the new scan to the very top of the list (index 0)
        scanHistory.add(0, HistoryItem(disease, currentDate, severityStr, imageBitmap = thumbnail))
    }
}