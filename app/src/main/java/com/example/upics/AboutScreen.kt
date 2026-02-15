package com.example.upics

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.DesignServices
import androidx.compose.material.icons.filled.Engineering
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    // Gestione tasto indietro fisico
    BackHandler { navController.popBackStack() }

    // Stato per gestire quale card è espansa
    var expandedMemberName by remember { mutableStateOf<String?>(null) }

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
                    text = "About Us",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
        }

        // --- CORPO (Scrollabile) ---
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(24.dp)
        ) {

        //    Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Gruppo 8",
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Siamo cinque studenti di Ingegneria del Cinema con specializzazioni diverse. Abbiamo unito le nostre competenze per progettare l'esperienza phygital di Upics.",
                fontSize = 15.sp,
                color = Color.DarkGray,
                lineHeight = 22.sp
            )

            Spacer(modifier = Modifier.height(32.dp))
            Text("Il nostro Team", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
            Text("(Tappa sulle card per i dettagli)", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.height(16.dp))

            // LISTA MEMBRI
            ExpandableTeamMemberCard(
                icon = Icons.Default.ManageAccounts,
                name = "Dario",
                role = "Project Manager",
                bio = "Dario coordina il team e assicura che la visione del progetto sia rispettata. Il punto di riferimento per l'organizzazione.",
                isExpanded = expandedMemberName == "Dario",
                onClick = { expandedMemberName = if (expandedMemberName == "Dario") null else "Dario" }
            )

            ExpandableTeamMemberCard(
                icon = Icons.Default.DesignServices,
                name = "Marco",
                role = "UI Designer",
                bio = "Marco ha curato l'interfaccia utente, definendo lo stile visivo e l'estetica generale dell'applicazione.",
                isExpanded = expandedMemberName == "Marco",
                onClick = { expandedMemberName = if (expandedMemberName == "Marco") null else "Marco" }
            )

            ExpandableTeamMemberCard(
                icon = Icons.Default.Brush,
                name = "Salvatore",
                role = "UX Designer",
                bio = "Salvatore si è concentrato sull'esperienza utente e sui flussi di navigazione per rendere l'app intuitiva.",
                isExpanded = expandedMemberName == "Salvatore",
                onClick = { expandedMemberName = if (expandedMemberName == "Salvatore") null else "Salvatore" }
            )

            ExpandableTeamMemberCard(
                icon = Icons.Default.Code,
                name = "Andrea",
                role = "Developer",
                bio = "Andrea ha tradotto il design in codice, occupandosi della logica e del funzionamento tecnico dell'app.",
                isExpanded = expandedMemberName == "Andrea",
                onClick = { expandedMemberName = if (expandedMemberName == "Andrea") null else "Andrea" }
            )

            ExpandableTeamMemberCard(
                icon = Icons.Default.Engineering,
                name = "Giulia",
                role = "Maker",
                bio = "Giulia ha progettato e realizzato il prototipo fisico della vending machine che interagisce con l'app.",
                isExpanded = expandedMemberName == "Giulia",
                onClick = { expandedMemberName = if (expandedMemberName == "Giulia") null else "Giulia" }
            )

            Spacer(modifier = Modifier.height(40.dp))
        }

        // --- BARRA INFERIORE COERENTE ---
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
fun ExpandableTeamMemberCard(
    icon: ImageVector,
    name: String,
    role: String,
    bio: String,
    isExpanded: Boolean,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = Color.White,
        border = BorderStroke(1.dp, if (isExpanded) Color(0xFF8BC34A) else Color(0xFFEEEEEE)),
        shadowElevation = if (isExpanded) 6.dp else 2.dp
    ) {
        Column(
            modifier = Modifier
                .animateContentSize(animationSpec = spring(dampingRatio = Spring.DampingRatioLowBouncy))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = CircleShape,
                    color = if (isExpanded) Color(0xFF8BC34A) else Color(0xFFE8F5E9),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(icon, null, tint = if (isExpanded) Color.White else Color(0xFF4CAF50), modifier = Modifier.size(24.dp))
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(name, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text(role, fontSize = 14.sp, color = Color.Gray)
                }
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))

                // Placeholder per foto
                Surface(
                    modifier = Modifier.fillMaxWidth().height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFF5F5F5)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, null, tint = Color.LightGray, modifier = Modifier.size(40.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = bio,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }
    }
}