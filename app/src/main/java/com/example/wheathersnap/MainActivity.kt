package com.example.wheathersnap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.ui.Modifier
import com.example.wheathersnap.navndata.appnavigation
import com.example.wheathersnap.ui.theme.WheatherSnapTheme

class MainActivity : ComponentActivity() {

    private val viewModel: WheatherviewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WheatherSnapTheme {
                appnavigation(modifier = Modifier, viewModel)
            }
        }
    }
}