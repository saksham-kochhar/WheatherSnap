package com.example.wheathersnap.permissions

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext

@Composable
fun PermissionCheck(onPermissionsGranted: () -> Unit) {
    val context = LocalContext.current

    val permissionsToRequest = buildList {
        add(Manifest.permission.CAMERA) }.toTypedArray()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val cameraGranted  = results[Manifest.permission.CAMERA] == true

        when {
            cameraGranted-> {
            onPermissionsGranted() }
            !cameraGranted -> Toast.makeText(
                context, "Camera permission denied", Toast.LENGTH_SHORT
            ).show()
        }
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissionsToRequest)
    }
}