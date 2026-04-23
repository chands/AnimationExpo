package com.example.animationexpo

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.animationexpo.gallery.AnimationDestination
import com.example.animationexpo.gallery.AnimationDemoDetailScreen
import com.example.animationexpo.gallery.AnimationGalleryScreen
import com.example.animationexpo.gallery.animationDemoSpecs

@Composable
fun AnimationExpoApp() {
    val demos = remember { animationDemoSpecs() }
    var selectedDemoId by rememberSaveable { mutableStateOf<String?>(null) }
    val selectedDemo = demos.firstOrNull { it.id == selectedDemoId }

    BackHandler(enabled = selectedDemo != null) {
        selectedDemoId = null
    }

    if (selectedDemo == null) {
        AnimationGalleryScreen(
            demos = demos,
            onDemoSelected = { demo ->
                val destination = demo.destination
                if (destination is AnimationDestination.Demo) {
                    selectedDemoId = destination.demoId
                }
            }
        )
    } else {
        AnimationDemoDetailScreen(
            demo = selectedDemo,
            onBack = { selectedDemoId = null }
        )
    }
}
