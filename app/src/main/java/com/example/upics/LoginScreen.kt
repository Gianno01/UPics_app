package com.example.upics

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current

    // Launcher per i permessi (Simula l'attivazione della camera e il successo della scansione)
    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Simuliamo il successo della lettura QR
            Toast.makeText(context, "QR Code Rilevato!", Toast.LENGTH_SHORT).show()
            // Naviga verso Auth (Login/Registrazione)
            navController.navigate("auth") { popUpTo("login") { inclusive = true } }
        } else {
            Toast.makeText(context, "Serve la fotocamera per scansionare il QR", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. Header Comune
        CommonHeader()

        Spacer(modifier = Modifier.height(40.dp))

        // 2. Titolo e Istruzioni
        Text(
            text = "Connect to Machine",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Frame the QR Code displayed on the\nvending machine screen.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )

        Spacer(modifier = Modifier.height(50.dp))

        // 3. Area Scanner (Visiva) con mirino
        Box(
            modifier = Modifier
                .size(280.dp)
                .background(Color(0xFFF5F5F5), RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            // Icona di sfondo pallida
            Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = "Scanner Placeholder",
                modifier = Modifier.size(100.dp),
                tint = Color.LightGray.copy(alpha = 0.5f)
            )

            // Disegna gli angoli verdi sopra
            ScannerOverlay()
        }

        Spacer(modifier = Modifier.weight(1f))

        // 4. Bottone Azione
        Button(
            onClick = { cameraPermissionLauncher.launch(Manifest.permission.CAMERA) },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Black,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .height(56.dp)
        ) {
            Icon(Icons.Default.CameraAlt, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Scan QR Code", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

// Componente grafico per disegnare gli angoli del mirino
@Composable
fun ScannerOverlay() {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 5.dp.toPx()
        val lineLength = 40.dp.toPx()
        val color = Color(0xFF8BC34A) // Verde Upics
        val padding = 20f // Leggero padding interno

        // Angolo in alto a sinistra
        drawLine(color, Offset(padding, padding), Offset(padding + lineLength, padding), strokeWidth)
        drawLine(color, Offset(padding, padding), Offset(padding, padding + lineLength), strokeWidth)

        // Angolo in alto a destra
        drawLine(color, Offset(size.width - padding, padding), Offset(size.width - padding - lineLength, padding), strokeWidth)
        drawLine(color, Offset(size.width - padding, padding), Offset(size.width - padding, padding + lineLength), strokeWidth)

        // Angolo in basso a sinistra
        drawLine(color, Offset(padding, size.height - padding), Offset(padding + lineLength, size.height - padding), strokeWidth)
        drawLine(color, Offset(padding, size.height - padding), Offset(padding, size.height - padding - lineLength), strokeWidth)

        // Angolo in basso a destra
        drawLine(color, Offset(size.width - padding, size.height - padding), Offset(size.width - padding - lineLength, size.height - padding), strokeWidth)
        drawLine(color, Offset(size.width - padding, size.height - padding), Offset(size.width - padding, size.height - padding - lineLength), strokeWidth)
    }
}