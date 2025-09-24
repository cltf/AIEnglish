package com.vocabulary.scanner.ui.scan

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.vocabulary.scanner.databinding.ActivityScanBinding
import com.vocabulary.scanner.ui.result.ResultActivity
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityScanBinding
    private var imageCapture: ImageCapture? = null
    private lateinit var cameraExecutor: ExecutorService
    private var isFlashOn = false
    private val textRecognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)
    
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startCamera()
        } else {
            Toast.makeText(this, "需要相机权限才能扫描文本", Toast.LENGTH_LONG).show()
            finish()
        }
    }
    
    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            processImageFromUri(it)
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // 请求相机权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
            == PackageManager.PERMISSION_GRANTED) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
        
        cameraExecutor = Executors.newSingleThreadExecutor()
        setupClickListeners()
    }
    
    private fun setupClickListeners() {
        // 返回按钮
        binding.btnBack.setOnClickListener {
            finish()
        }
        
        // 闪光灯按钮
        binding.btnFlashlight.setOnClickListener {
            toggleFlashlight()
        }
        
        // 相册按钮
        binding.btnGallery.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
        
        // 取消按钮
        binding.btnCancel.setOnClickListener {
            finish()
        }
        
        // 扫描按钮
        binding.btnScan.setOnClickListener {
            takePicture()
        }
        
        // 中考词汇tab
        binding.navVocabulary.setOnClickListener {
            // 跳转到词汇页面
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 0) // 0表示中考词汇tab
            startActivity(intent)
            finish()
        }
        
        // 扫描tab
        binding.navScan.setOnClickListener {
            // 当前就在扫描页面，不需要跳转
        }
        
        // 我的tab
        binding.navProfile.setOnClickListener {
            // 跳转到我的页面
            val intent = Intent(this, com.vocabulary.scanner.ui.main.MainTabsActivity::class.java)
            intent.putExtra("initial_tab", 2) // 2表示我的tab
            startActivity(intent)
            finish()
        }
    }
    
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.cameraPreview.surfaceProvider)
                }
            
            imageCapture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(this, "相机启动失败", Toast.LENGTH_SHORT).show()
            }
            
        }, ContextCompat.getMainExecutor(this))
    }
    
    private fun toggleFlashlight() {
        isFlashOn = !isFlashOn
        binding.btnFlashlight.setColorFilter(
            if (isFlashOn) ContextCompat.getColor(this, android.R.color.holo_orange_light)
            else ContextCompat.getColor(this, android.R.color.white)
        )
        // TODO: 实现闪光灯控制逻辑
    }
    
    private fun takePicture() {
        val imageCapture = imageCapture ?: return
        
        binding.loadingLayout.visibility = android.view.View.VISIBLE
        
        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            createImageFile()
        ).build()
        
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exception: ImageCaptureException) {
                    binding.loadingLayout.visibility = android.view.View.GONE
                    Toast.makeText(this@ScanActivity, "拍照失败", Toast.LENGTH_SHORT).show()
                }
                
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    output.savedUri?.let { uri ->
                        processImageFromUri(uri)
                    }
                }
            }
        )
    }
    
    private fun processImageFromUri(uri: Uri) {
        try {
            val bitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(uri))
            bitmap?.let {
                val image = InputImage.fromBitmap(it, 0)
                recognizeText(image)
            }
        } catch (e: Exception) {
            binding.loadingLayout.visibility = android.view.View.GONE
            Toast.makeText(this, "图片处理失败", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun recognizeText(image: InputImage) {
        textRecognizer.process(image)
            .addOnSuccessListener { visionText ->
                binding.loadingLayout.visibility = android.view.View.GONE
                
                val recognizedText = visionText.text
                if (recognizedText.isNotEmpty()) {
                    val intent = Intent(this, ResultActivity::class.java)
                    intent.putExtra("recognized_text", recognizedText)
                    startActivity(intent)
                } else {
                    Toast.makeText(this, "未识别到文本", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                binding.loadingLayout.visibility = android.view.View.GONE
                Toast.makeText(this, "文本识别失败: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
    
    private fun createImageFile(): java.io.File {
        val timeStamp = java.text.SimpleDateFormat("yyyyMMdd_HHmmss", java.util.Locale.getDefault())
            .format(java.util.Date())
        val storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        return java.io.File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        )
    }
    
    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
        textRecognizer.close()
    }
}




