package com.example.efos

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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

    // 1. Set up Gallery Picker
    private val pickImageFromGallery = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream = requireContext().contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            startFakeAIProcessing(bitmap)
        }
    }

    // 2. Set up Camera Capture
    private val takePhotoWithCamera = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
        bitmap?.let {
            startFakeAIProcessing(it)
        }
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

        // Launch Gallery
        btnGallery.setOnClickListener {
            pickImageFromGallery.launch("image/*")
        }

        // Launch Camera
        btnCamera.setOnClickListener {
            takePhotoWithCamera.launch(null)
        }
    }

    private fun startFakeAIProcessing(originalBitmap: Bitmap) {
        // Show the unedited image first
        ivPreview.setImageBitmap(originalBitmap)

        // Show loading state
        progressBar.visibility = View.VISIBLE
        tvStatus.text = getString(R.string.status_running_ai)
        btnGallery.isEnabled = false
        btnCamera.isEnabled = false

        // Simulate a 2.5-second AI inference delay
        Handler(Looper.getMainLooper()).postDelayed({
            progressBar.visibility = View.GONE
            tvStatus.text = ""
            btnGallery.isEnabled = true
            btnCamera.isEnabled = true

            // --- NEW LOGIC: 25% chance of Low Confidence ---
            val isLowConfidence = (1..100).random() <= 25

            if (isLowConfidence) {
                showLowConfidenceDialog()
            } else {
                // Generate random mock data for a successful scan
                val result = mockResults.random()
                val randomDSP = (15..95).random() // Random severity between 15% and 95%

                // If it's a healthy leaf, DSP is 0 and we don't draw a red box. Otherwise, draw it!
                if (result.first == "Healthy Eggplant Leaf") {
                    showResultDialog(result.first, 0, result.second, originalBitmap)
                } else {
                    val annotatedImage = drawBoundingBox(originalBitmap, result.first, randomDSP)
                    ivPreview.setImageBitmap(annotatedImage)
                    showResultDialog(result.first, randomDSP, result.second, annotatedImage)
                }
            }
        }, 2500)
    }

    // --- NEW FUNCTION: Displays the Redo Warning ---
    private fun showLowConfidenceDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("⚠️ Low Confidence Detected")
            .setMessage("The AI could not confidently identify the leaf. The image might be too blurry, too dark, or taken from too far away.\n\nPlease ensure the leaf is clearly visible and well-lit.")
            .setPositiveButton("Redo Scan") { dialog, _ ->
                dialog.dismiss()
                // You can optionally launch the camera immediately here,
                // but letting them pick Gallery or Camera again is safer.
            }
            .setCancelable(false) // Forces the user to click the button
            .show()
    }

    private fun drawBoundingBox(original: Bitmap, diseaseName: String, dsp: Int): Bitmap {
        // Convert image to a mutable canvas so we can draw on it
        val mutableBitmap = original.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val width = mutableBitmap.width
        val height = mutableBitmap.height

        // Generate random coordinates for the box (keeping it somewhat centered)
        val left = (width * 0.1f) + (Math.random() * width * 0.3f).toFloat()
        val top = (height * 0.1f) + (Math.random() * height * 0.3f).toFloat()
        val right = left + (width * 0.3f) + (Math.random() * width * 0.2f).toFloat()
        val bottom = top + (height * 0.3f) + (Math.random() * height * 0.2f).toFloat()

        // Set up the Red Box Pen
        val boxPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.STROKE
            strokeWidth = (width / 80f).coerceAtLeast(5f)
        }

        // Set up the Text Background Pen
        val textBgPaint = Paint().apply {
            color = Color.RED
            style = Paint.Style.FILL
        }

        // Set up the White Text Pen
        val textPaint = Paint().apply {
            color = Color.WHITE
            textSize = (width / 20f).coerceAtLeast(30f)
            isAntiAlias = true
        }

        // 1. Draw the Red Box
        canvas.drawRect(left, top, right, bottom, boxPaint)

        // 2. Draw the Label Background
        val label = "$diseaseName ($dsp%)"
        val textWidth = textPaint.measureText(label)
        canvas.drawRect(left, top - textPaint.textSize - 10f, left + textWidth + 16f, top, textBgPaint)

        // 3. Draw the Text inside the label
        canvas.drawText(label, left + 8f, top - 8f, textPaint)

        return mutableBitmap
    }

    private fun showResultDialog(disease: String, dsp: Int, details: String, resultImage: Bitmap) {
        AlertDialog.Builder(requireContext())
            .setTitle("Detection: $disease")
            .setMessage("Severity (DSP): $dsp%\n\n$details")
            .setPositiveButton("Save to History") { dialog, _ ->

                // Pass the real image to our memory bank
                HistoryManager.addScan(disease, dsp, resultImage)

                Toast.makeText(requireContext(), "Saved to History!", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}