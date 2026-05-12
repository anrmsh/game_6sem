package com.example.play_6sem

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.play_6sem.ui.GameApp
import com.example.play_6sem.ui.theme.Play_6semTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Play_6semTheme {
                GameApp()
            }
        }
    }
}