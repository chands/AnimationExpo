package com.example.animationexpo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.animationexpo.ui.theme.AnimationExpoTheme

class AnimationExpoActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnimationExpoTheme(dynamicColor = false, darkTheme = false) {
                AnimationExpoApp()
            }
        }
    }
}
