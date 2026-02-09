
package com.example.bleaudiotool.ui

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@SuppressLint("MissingPermission")
@Composable
fun MainScreen(viewModel: MainViewModel) {
    val status by viewModel.statusText.collectAsState()
    val isProcessing by viewModel.isProcessing.collectAsState()
    val isRecording by viewModel.isRecording.collectAsState()
    val readyToSend by viewModel.readyToSend.collectAsState()
    val isFast by viewModel.isMotorFast.collectAsState()
    val devices by viewModel.scannedDevices.collectAsState()
    val isScanning by viewModel.isScanning.collectAsState()

    val recordingColor by animateColorAsState(if (isRecording) Color.Red else Color.White)

    val audioPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { viewModel.handleFileUpload(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("BLE Audio & Motor Tool") },
                elevation = 0.dp,
                backgroundColor = MaterialTheme.colors.surface
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Status Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = if (isRecording) Color(0xFFFFEBEE) else Color(0xFFE3F2FD)
            ) {
                Row(
                    Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                when {
                                    isRecording -> Color.Red
                                    status.contains("Connected") -> Color(0xFF4CAF50)
                                    else -> Color(0xFFF44336)
                                }
                            )
                    )
                    Spacer(Modifier.width(12.dp))
                    Column {
                        Text("System Status", style = MaterialTheme.typography.caption, color = Color.Gray)
                        Text(status, fontWeight = FontWeight.Bold, color = Color.Black)
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // BLE Section Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Nearby Devices", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp)
                IconButton(onClick = { viewModel.startScan() }, enabled = !isScanning) {
                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                }
            }
            
            // Device List
            Box(Modifier.weight(1f).fillMaxWidth()) {
                if (devices.isEmpty() && !isScanning) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No devices found", color = Color.Gray)
                    }
                }

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(devices) { device ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { viewModel.connectToDevice(device) },
                            elevation = 1.dp,
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.Bluetooth, contentDescription = null, tint = Color(0xFF2196F3))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(device.name ?: "Unknown Device", fontWeight = FontWeight.Bold)
                                    Text(device.address, style = MaterialTheme.typography.caption, color = Color.Gray)
                                }
                            }
                        }
                    }

                    if (isScanning) {
                        item {
                            Box(Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(16.dp))

            // Audio Section
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = { audioPickerLauncher.launch("audio/*") },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !isRecording
                ) { 
                    Text("FILE") 
                }
                
                Button(
                    onClick = { viewModel.toggleRecording() },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = if (isRecording) Color.Red else MaterialTheme.colors.primary,
                        contentColor = Color.White
                    )
                ) { 
                    Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(if (isRecording) "STOP" else "RECORD") 
                }
            }

            Spacer(Modifier.height(16.dp))

            // Motor Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = 0.dp,
                shape = RoundedCornerShape(12.dp),
                backgroundColor = Color(0xFFF5F5F5)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("Motor Config", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.caption)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = isFast, onClick = { viewModel.isMotorFast.value = true })
                        Text("Fast")
                        Spacer(Modifier.width(24.dp))
                        RadioButton(selected = !isFast, onClick = { viewModel.isMotorFast.value = false })
                        Text("Slow")
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // Action Section
            Button(
                onClick = { viewModel.sendToDevice() },
                enabled = readyToSend && !isProcessing && !isRecording && status.contains("Connected"),
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF2196F3), contentColor = Color.White)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("STREAM DATA", fontWeight = FontWeight.ExtraBold)
                }
            }
        }
    }
}
