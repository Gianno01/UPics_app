package com.example.upics

import android.content.res.Configuration
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController

@Composable
fun AboutScreen(navController: NavController) {
    // Handle physical back button
    BackHandler { navController.popBackStack() }

    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // State to manage which card is currently expanded
    var expandedMemberName by remember { mutableStateOf<String?>(null) }

    // Contenuto della lista del team (definito qui per riusarlo nei due layout)
    val teamListContent: @Composable ColumnScope.() -> Unit = {
        Text("Our Team", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.Black)
        Text("(Tap on the cards for details)", fontSize = 12.sp, color = Color.Gray)
        Spacer(modifier = Modifier.height(16.dp))

        ExpandableTeamMemberCard(
            icon = Icons.Default.ManageAccounts,
            imageRes = R.drawable.foto_dario,
            name = "Dario",
            role = "Project Manager",
            bio = "Dario coordinates the team and ensures the project vision is respected. He is our main point of reference for organization and deadlines.",
            isExpanded = expandedMemberName == "Dario",
            onClick = { expandedMemberName = if (expandedMemberName == "Dario") null else "Dario" }
        )

        ExpandableTeamMemberCard(
            icon = Icons.Default.DesignServices,
            imageRes = R.drawable.foto_marco,
            name = "Marco",
            role = "UI Designer",
            bio = "Marco crafted the user interface, defining the visual style, colors, and overall aesthetics of the application.",
            isExpanded = expandedMemberName == "Marco",
            onClick = { expandedMemberName = if (expandedMemberName == "Marco") null else "Marco" }
        )

        ExpandableTeamMemberCard(
            icon = Icons.Default.Brush,
            imageRes = R.drawable.foto_salvatore,
            name = "Salvatore",
            role = "UX Designer",
            bio = "Salvatore focused on the user experience and navigation flows to make the app intuitive, seamless, and easy to use.",
            isExpanded = expandedMemberName == "Salvatore",
            onClick = { expandedMemberName = if (expandedMemberName == "Salvatore") null else "Salvatore" }
        )

        ExpandableTeamMemberCard(
            icon = Icons.Default.Code,
            imageRes = R.drawable.foto_andrea,
            name = "Andrea",
            role = "Developer",
            bio = "Andrea translated the design into working code, taking care of the app's logic, image processing, and technical functioning.",
            isExpanded = expandedMemberName == "Andrea",
            onClick = { expandedMemberName = if (expandedMemberName == "Andrea") null else "Andrea" }
        )

        ExpandableTeamMemberCard(
            icon = Icons.Default.Engineering,
            imageRes = R.drawable.foto_giulia,
            name = "Giulia",
            role = "Maker",
            bio = "Giulia designed and built the physical prototype of the vending machine that seamlessly interacts with our application.",
            isExpanded = expandedMemberName == "Giulia",
            onClick = { expandedMemberName = if (expandedMemberName == "Giulia") null else "Giulia" }
        )

        Spacer(modifier = Modifier.height(40.dp))
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
    ) {
        if (isLandscape) {
            // ===========================
            // LAYOUT LANDSCAPE (Split View)
            // ===========================
            Row(modifier = Modifier.fillMaxSize()) {
                // COLONNA SINISTRA (Intro fissa e bottone indietro)
                Column(
                    modifier = Modifier
                        .weight(0.8f)
                        .fillMaxHeight()
                        .padding(24.dp)
                        .statusBarsPadding()
                        .navigationBarsPadding() // Protegge il tasto sul fondo
                ) {

                    // Testi introduttivi (Spostati in alto)
                    Text(
                        text = "Group 8",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "We are five Cinema Engineering students with different specializations. We combined our skills to design the phygital experience of Upics.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )

                    // Molla che spinge il tasto verso il basso
                    Spacer(modifier = Modifier.weight(1f))

                    // Tasto indietro in basso a sinistra (Dimensione 56dp per coerenza)
                    OutlinedIconButton(
                        onClick = { navController.popBackStack() },
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, Color.LightGray),
                        modifier = Modifier.size(56.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = Color.Black)
                    }
                }

                // COLONNA DESTRA (Lista scrollabile)
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .fillMaxHeight()
                        .background(Color(0xFFF0F0F0)) // Un leggero contrasto per la zona scrollabile
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(24.dp)
                            .statusBarsPadding()
                    ) {
                        teamListContent()
                    }
                }
            }

        } else {
            // ===========================
            // LAYOUT PORTRAIT (Originale)
            // ===========================
            Column(
                modifier = Modifier.fillMaxSize()
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

                // --- BODY (Scrollable) ---
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp)
                ) {

                    Text(
                        text = "Group 8",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "We are five Cinema Engineering students with different specializations. We combined our skills to design the phygital experience of Upics.",
                        fontSize = 15.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(32.dp))

                    // Inseriamo il contenuto della lista
                    teamListContent()
                }

                // --- CONSISTENT BOTTOM BAR ---
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
                    }
                }
            }
        }
    }
}

@Composable
fun ExpandableTeamMemberCard(
    icon: ImageVector,
    imageRes: Int,
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

            // Quando la carta è espansa, mostra Foto Polaroid + Bio
            if (isExpanded) {
                Spacer(modifier = Modifier.height(24.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top // Allineati in alto per compensare la foto
                ) {
                    // --- FOTO IN STILE POLAROID ---
                    Surface(
                        color = Color.White,
                        modifier = Modifier
                            .padding(start = 4.dp)
                            .rotate(-2f) // Leggera rotazione per dare l'effetto "foto appoggiata"
                            .shadow(8.dp, RoundedCornerShape(2.dp)), // Ombra quadrata
                        shape = RoundedCornerShape(2.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .padding(
                                    start = 6.dp,
                                    top = 6.dp,
                                    end = 6.dp,
                                    bottom = 16.dp // La banda inferiore più spessa tipica delle polaroid
                                )
                        ) {
                            // Immagine quadrata all'interno del frame
                            Image(
                                painter = painterResource(id = imageRes),
                                contentDescription = "Foto di $name",
                                contentScale = ContentScale.Crop, // Assicura che riempia il riquadro senza deformarsi
                                modifier = Modifier
                                    .size(90.dp) // Dimensione fissa dell'immagine quadrata
                                    .background(Color(0xFFEEEEEE)) // Colore di fondo se l'immagine carica lentamente
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(20.dp))

                    // LA BIO DI LATO
                    Text(
                        text = bio,
                        fontSize = 14.sp,
                        color = Color.DarkGray,
                        lineHeight = 22.sp,
                        fontStyle = FontStyle.Italic,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 8.dp) // Allineiamo un po' il testo rispetto alla foto
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}