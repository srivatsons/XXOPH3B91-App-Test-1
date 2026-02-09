
package com.example.bleaudiotool

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.bleaudiotool.audio.AudioConverter
import com.example.bleaudiotool.ble.BleManager
import com.example.bleaudiotool.data.BleRepository
import com.example.bleaudiotool.ui.MainScreen
import com.example.bleaudiotool.ui.MainViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Manual DI for core components
        val bleManager = BleManager(applicationContext)
        val bleRepository = BleRepository(bleManager)
        val audioConverter = AudioConverter(applicationContext)
        val viewModel = MainViewModel(bleRepository, audioConverter)

        setContent {
            MainScreen(viewModel)
        }
    }
}
