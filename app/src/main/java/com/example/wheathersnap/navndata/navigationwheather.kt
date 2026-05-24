package com.example.wheathersnap.navndata

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.wheathersnap.WheatherviewModel
import com.example.wheathersnap.permissions.CameraScreen
import com.example.wheathersnap.screens.Homescreen
import com.example.wheathersnap.screens.SavedReportsScreen
import com.example.wheathersnap.screens.reportscreen

@Composable
fun appnavigation(modifier: Modifier = Modifier, viewmodel : WheatherviewModel){
    val navcontroller = rememberNavController()

    NavHost(navcontroller, startDestination = routes.home, modifier = Modifier) {
        composable(routes.home) {
            Homescreen(viewmodel , navcontroller)
        }
        composable(routes.report) {
            reportscreen(navcontroller , viewmodel)
        }
        composable(routes.camera) {
            CameraScreen(viewmodel,navcontroller = navcontroller)        }
        composable(routes.reporthistory) {
            SavedReportsScreen(navcontroller , viewmodel)

        }

    }
}