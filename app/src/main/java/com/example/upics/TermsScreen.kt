package com.example.upics

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun TermsScreen(navController: NavController) {
    // Gestione tasto indietro fisico del telefono
    BackHandler { navController.popBackStack() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        // --- HEADER ---
        Surface(
            color = Color.White,
            shadowElevation = 2.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(vertical = 20.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Terms & Conditions",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // --- CORPO DEL TESTO (Scrollabile) ---
        Column(
            modifier = Modifier
                .weight(1f) // Occupa tutto lo spazio centrale spingendo la barra in basso
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {
            Text(
                text = "Last Updated: February 2026",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(24.dp))

            TermsSection(
                title = "1. Introduction",
                body = "Welcome to Upics. By using our application and physical vending machines, you agree to these terms. Upics provides a phygital experience allowing you to edit and print photos directly from your smartphone to our connected printers."
            )

            TermsSection(
                title = "2. Print Credits & Payments",
                body = "Print credits can be purchased within the app. Each credit is valid for a single print at any Upics machine. Credits are non-refundable and expire 12 months after the purchase date. Ensure you are connected to the machine via audio signal before confirming the print."
            )

            TermsSection(
                title = "3. User Content & Privacy",
                body = "You retain all rights to the photos you upload and edit. Upics processes your images temporarily solely for the purpose of applying magic edits and transmitting them to the printer. We do not store your photos on our servers after the print is successful."
            )

            TermsSection(
                title = "4. Audio Connection",
                body = "The app requires microphone access to listen for the secure audio token emitted by the vending machine. This audio data is processed locally on your device and is not recorded or transmitted."
            )

            TermsSection(
                title = "5. Acceptable Use",
                body = "You agree not to print explicit, illegal, or copyrighted material without permission. Upics reserves the right to suspend accounts that violate these guidelines."
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                text = "Thank you for creating magic with Upics ✨",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))
        }

        // --- BARRA INFERIORE (Coerente con History e Resume) ---
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = Color.White,
            shadowElevation = 16.dp
        ) {
            Row(
                modifier = Modifier
                    .padding(24.dp)
                    .navigationBarsPadding(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedIconButton(
                    onClick = { navController.popBackStack() },
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, Color.LightGray),
                    modifier = Modifier.size(56.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.Black
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))


            }
        }
    }
}

@Composable
fun TermsSection(title: String, body: String) {
    Column(modifier = Modifier.padding(bottom = 24.dp)) {
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Text(
            text = body,
            fontSize = 15.sp,
            color = Color.DarkGray,
            lineHeight = 22.sp
        )
    }
}