package com.example.wheathersnap.permissions

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.wheathersnap.WheatherviewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@Composable
fun CameraScreen(
    viewModel: WheatherviewModel,
    navcontroller: NavHostController
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val previewView = remember { PreviewView(context) }
    var imageCapture by remember { mutableStateOf<ImageCapture?>(null) }
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var isCapturing by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val capture = ImageCapture.Builder()
                .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                .build()
            imageCapture = capture
            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    capture
                )
            } catch (e: Exception) {
                Toast.makeText(context, "Camera error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        AndroidView(
            factory = { previewView },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 48.dp),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (isCapturing) {
                CircularProgressIndicator(color = Color.White)
            } else {
                Button(
                    onClick = {
                        isCapturing = true
                        captureAndSave(
                            context = context,
                            imageCapture = imageCapture,
                            cameraExecutor = cameraExecutor,
                            onDone = { uri, originalKb, compressedKb ->
                                android.os.Handler(android.os.Looper.getMainLooper()).post {
                                    isCapturing = false
                                    if (uri != null) {
                                        viewModel.setCapturedUri(uri)
                                        viewModel.setImageSizes(originalKb, compressedKb)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            "Capture failed",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    navcontroller.popBackStack()
                                }
                            }
                        )
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFC1CC7D)
                    ),
                    modifier = Modifier.size(72.dp),
                    contentPadding = PaddingValues(0.dp)
                ) { }
            }
        }
    }
}


private fun captureAndSave(
    context: Context,
    imageCapture: ImageCapture?,
    cameraExecutor: java.util.concurrent.Executor,
    onDone: (Uri?, Long?, Long?) -> Unit
) {
    val capture = imageCapture ?: run { onDone(null, null, null); return }

    val tempFile = File(context.cacheDir, "raw_capture.jpg")
    val outputOptions = ImageCapture.OutputFileOptions.Builder(tempFile).build()

    capture.takePicture(
        outputOptions,
        cameraExecutor,
        object : ImageCapture.OnImageSavedCallback {

            override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                val originalKb = tempFile.length() / 1024   // before compression

                val compressed = compressBitmap(tempFile, quality = 60)
                    ?: run { onDone(null, null, null); return }

                val compressedKb = compressed.size.toLong() / 1024  // after compression

                val savedUri = saveToMediaStore(context, compressed)
                tempFile.delete()
                onDone(savedUri, originalKb, compressedKb)
            }

            override fun onError(exception: ImageCaptureException) {
                onDone(null, null, null)
            }
        }
    )
}

private fun compressBitmap(inputFile: File, quality: Int): ByteArray? {
    return try {
        val options = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeFile(inputFile.absolutePath, options)
        options.inSampleSize = calculateSampleSize(options, 1080, 1440)
        options.inJustDecodeBounds = false
        val bitmap = BitmapFactory.decodeFile(inputFile.absolutePath, options) ?: return null
        val baos = java.io.ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos)
        bitmap.recycle()
        baos.toByteArray()
    } catch (e: Exception) {
        null
    }
}

private fun calculateSampleSize(
    options: BitmapFactory.Options,
    reqWidth: Int,
    reqHeight: Int
): Int {
    val (height, width) = options.outHeight to options.outWidth
    var inSampleSize = 1
    if (height > reqHeight || width > reqWidth) {
        val halfH = height / 2
        val halfW = width / 2
        while (halfH / inSampleSize >= reqHeight && halfW / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }
    return inSampleSize
}

private fun saveToMediaStore(context: Context, data: ByteArray): Uri? {
    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
    val filename = "WSNAP_$timestamp.jpg"
    return try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Images.Media.DISPLAY_NAME, filename)
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.RELATIVE_PATH, "${Environment.DIRECTORY_PICTURES}/WeatherSnap")
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
            val uri = context.contentResolver
                .insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values) ?: return null
            context.contentResolver.openOutputStream(uri)?.use { it.write(data) }
            values.clear()
            values.put(MediaStore.Images.Media.IS_PENDING, 0)
            context.contentResolver.update(uri, values, null, null)
            uri
        } else {
            val dir = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                "WeatherSnap"
            ).also { it.mkdirs() }
            val file = File(dir, filename)
            FileOutputStream(file).use { it.write(data) }
            Uri.fromFile(file)
        }
    } catch (e: Exception) {
        null
    }
}