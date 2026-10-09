package com.example.efos

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ScanFragment : Fragment() {

    private lateinit var ivPreview: ImageView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvStatus: TextView
    private lateinit var btnGallery: Button
    private lateinit var btnCamera: Button

    private val mockResults = listOf(
        Pair("Healthy Eggplant Leaf", "Explanation: A healthy leaf is broad, vibrant green and entirely free of spots.\n\nRecommendation: Maintain consistent moisture and apply balanced fertilizer."),
        Pair("Verticillium Wilt", "Explanation: A soil-borne fungus blocking the vascular system. Leaves turn yellow and wilt.\n\nRecommendation: Destroy infected plants. Practice 3-4 year crop rotation."),
        Pair("Tobacco Caterpillar", "Explanation: Aggressive foliage feeders chewing large holes in leaves.\n\nRecommendation: Manually pick off or apply Bacillus thuringiensis (Bt) or neem oil."),
        Pair("Hadda Beetle", "Explanation: Feed by scraping green epidermal tissue, leaving a lace-like skeleton.\n\nRecommendation: Inspect leaves frequently and spray neem seed extract."),
        Pair("Phomopsis Blight", "Explanation: Fungal disease starting as pale, sunken spots with tiny black bodies.\n\nRecommendation: Prune affected leaves, avoid overhead watering, apply copper fungicide.")
    )

    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            showLoadingState()
            viewLifecycleOwner.lifecycleScope.launch(Dispatchers.IO) {
                val inputStream = requireContext().contentResolver.openInputStream(it)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                withContext(Dispatchers.Main) {
                    bitmap?.let { startFakeAIProcessing(it) }
                }
            }
        }
    }

    private val takePhotoWithCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let { startFakeAIProcessing(it) }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_scan, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ivPreview = view.findViewById(R.id.iv_scan_preview)
        progressBar = view.findViewById(R.id.progress_bar_inference)
        tvStatus = view.findViewById(R.id.tv_inference_status)
        btnGallery = view.findViewById(R.id.btn_gallery)
        btnCamera = view.findViewById(R.id.btn_camera)

        btnGallery.setOnClickListener { pickImageFromGallery.launch("image/*") }
        btnCamera.setOnClickListener { takePhotoWithCamera.launch(null) }
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        tvStatus.text = getString(R.string.status_running_ai)
        btnGallery.isEnabled = false
        btnCamera.isEnabled = false
    }

    private fun hideLoadingState() {
        progressBar.visibility = View.GONE
        tvStatus.text = ""
        btnGallery.isEnabled = true
        btnCamera.isEnabled = true
    }

    private fun startFakeAIProcessing(originalBitmap: Bitmap) {
        ivPreview.setImageBitmap(originalBitmap)
        showLoadingState()

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Default) {
            delay(2500)
            val isLowConfidence = (1..100).random() <= 25

            if (isLowConfidence) {
                withContext(Dispatchers.Main) {
                    hideLoadingState()
                    showLowConfidenceDialog()
                }
            } else {
                val result = mockResults.random()
                val randomDSP = (15..95).random()

                val finalImage = if (result.first == "Healthy Eggplant Leaf") {
                    originalBitmap
                } else {
                    drawBoundingBox(originalBitmap, result.first, randomDSP)
                }

                withContext(Dispatchers.Main) {
                    hideLoadingState()
                    ivPreview.setImageBitmap(finalImage)
                    showResultDialog(result.first, randomDSP, result.second, finalImage)
                }
            }
        }
    }

    // --- NEW: Helper function to save image to phone storage securely ---
    private fun saveImageToInternalStorage(bitmap: Bitmap, context: Context): String {
        // Scale it down to keep the app fast and save memory
        val aspectRatio = bitmap.width.toFloat() / bitmap.height.toFloat()
        val thumbWidth = 400
        val thumbHeight = (thumbWidth / aspectRatio).toInt()
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, thumbWidth, thumbHeight, true)

        val filename = "scan_${System.currentTimeMillis()}.jpg"
        context.openFileOutput(filename, Context.MODE_PRIVATE).use { fos ->
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos)
        }
        return context.getFileStreamPath(filename).absolutePath
    }

    private fun showLowConfidenceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Low Confidence Detected")
            .setMessage("The AI could not confidently identify the leaf. The image might be too blurry, too dark, or taken from too far away.\n\nPlease ensure the leaf is clearly visible and well-lit.")
            .setPositiveButton("Redo Scan") { dialog, _ -> dialog.dismiss() }
            .setCancelable(false)
            .show()
    }

    private fun drawBoundingBox(original: Bitmap, diseaseName: String, dsp: Int): Bitmap {
        val mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)
        val width = mutableBitmap.width
        val height = mutableBitmap.height

        val left = (width * 0.1f) + (Math.random() * width * 0.3f).toFloat()
        val top = (height * 0.1f) + (Math.random() * height * 0.3f).toFloat()
        val right = left + (width * 0.3f) + (Math.random() * width * 0.2f).toFloat()
        val bottom = top + (height * 0.3f) + (Math.random() * height * 0.2f).toFloat()

        val boxPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = (width / 80f).coerceAtLeast(5f)
        }
        val textBgPaint = Paint().apply { color = Color.RED; style = Paint.Style.FILL }
        val textPaint = Paint().apply { color = Color.WHITE; textSize = (width / 20f).coerceAtLeast(30f); isAntiAlias = true }

        canvas.drawRect(left, top, right, bottom, boxPaint)
        val label = "$diseaseName ($dsp%)"
        val textWidth = textPaint.measureText(label)
        canvas.drawRect(left, top - textPaint.textSize - 10f, left + textWidth + 16f, top, textBgPaint)
        canvas.drawText(label, left + 8f, top - 8f, textPaint)

        return mutableBitmap
    }

    private fun showResultDialog(disease: String, dsp: Int, details: String, resultImage: Bitmap) {
        AlertDialog.Builder(requireContext())
            .setTitle("Detection: $disease")
            .setMessage("Severity (DSP): $dsp%\n\n$details")
            .setPositiveButton("Save to History") { dialog, _ ->

                // Capture context on the main thread BEFORE switching to IO,
                // so we don't crash if the fragment detaches mid-save.
                val ctx = requireContext().applicationContext

                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            val imagePath = saveImageToInternalStorage(resultImage, ctx)
                            val dateFormat = SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault())
                            val currentDate = dateFormat.format(Date())
                            val severityStr = "$dsp% DSP"

                            val dbHelper = DatabaseHelper(ctx)
                            dbHelper.insertScan(disease, currentDate, severityStr, imagePath)
                        }
                        Toast.makeText(ctx, "Saved to History!", Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        Toast.makeText(ctx, "Failed to save: ${e.message}", Toast.LENGTH_LONG).show()
                    }
                    dialog.dismiss()
                }
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}