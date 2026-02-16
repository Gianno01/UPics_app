package com.example.upics

import android.content.res.Configuration
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.firebase.Firebase
import com.google.firebase.database.database

@Composable
fun AuthScreen(navController: NavController) {
    // --- STATO GLOBALE DELLA SCHERMATA ---
    var isLoginMode by remember { mutableStateOf(true) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }

    val focusManager = LocalFocusManager.current
    val context = LocalContext.current
    val configuration = LocalConfiguration.current
    val isLandscape = configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

    // Callback per pulire il form dopo una registrazione successa
    val onSignupSuccess = {
        email = ""
        password = ""
        name = ""
        isLoginMode = true
    }

    // --- LAYOUT ADATTIVO ---
    if (isLandscape) {
        // === LAYOUT ORIZZONTALE ===
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            // METÀ SINISTRA: Header e Switch (Fisso e centrato)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                AuthHeaderSection(isLoginMode) { isLoginMode = it }
            }

            // METÀ DESTRA: Form e Bottoni (Scrollabile per la tastiera)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
                    .padding(24.dp),
                verticalArrangement = Arrangement.Center
            ) {
                AuthFormSection(
                    isLoginMode = isLoginMode,
                    name = name, onNameChange = { name = it },
                    email = email, onEmailChange = { email = it },
                    password = password, onPasswordChange = { password = it },
                    isPasswordVisible = isPasswordVisible, onPasswordVisibilityChange = { isPasswordVisible = it },
                    focusManager = focusManager
                )
                Spacer(modifier = Modifier.height(32.dp))
                AuthActionButtons(
                    isLoginMode = isLoginMode,
                    email = email, password = password, name = name,
                    navController = navController,
                    onSignupSuccess = onSignupSuccess
                )
            }
        }
    } else {
        // === LAYOUT VERTICALE (CLASSICO) ===
        // Aggiunto verticalScroll per evitare che la tastiera nasconda i tasti sui telefoni piccoli
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .verticalScroll(rememberScrollState())
                .padding(top = 16.dp, bottom = 24.dp)
        ) {
            CommonHeader()
            Spacer(modifier = Modifier.height(24.dp))

            Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                AuthHeaderSection(isLoginMode) { isLoginMode = it }
                Spacer(modifier = Modifier.height(32.dp))

                AuthFormSection(
                    isLoginMode = isLoginMode,
                    name = name, onNameChange = { name = it },
                    email = email, onEmailChange = { email = it },
                    password = password, onPasswordChange = { password = it },
                    isPasswordVisible = isPasswordVisible, onPasswordVisibilityChange = { isPasswordVisible = it },
                    focusManager = focusManager
                )

                Spacer(modifier = Modifier.height(48.dp))

                AuthActionButtons(
                    isLoginMode = isLoginMode,
                    email = email, password = password, name = name,
                    navController = navController,
                    onSignupSuccess = onSignupSuccess
                )
            }
        }
    }
}

// ============================================================================
// SOTTO-COMPONENTI RIUTILIZZABILI (Evitano di duplicare il codice)
// ============================================================================

@Composable
fun AuthHeaderSection(isLoginMode: Boolean, onModeChange: (Boolean) -> Unit) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Crossfade(targetState = isLoginMode, label = "TitleAnim") { login ->
            Column {
                Text(
                    text = if (login) "Welcome Back," else "Create Account,",
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Text(
                    text = if (login) "Please sign in to continue." else "Sign up to get started.",
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        SlidingTabSelector(isLoginMode = isLoginMode, onModeChange = onModeChange)
    }
}

@Composable
fun AuthFormSection(
    isLoginMode: Boolean,
    name: String, onNameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    isPasswordVisible: Boolean, onPasswordVisibilityChange: (Boolean) -> Unit,
    focusManager: FocusManager
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Campo Nome (Solo Registrazione)
        AnimatedVisibility(
            visible = !isLoginMode,
            enter = expandVertically() + fadeIn(),
            exit = shrinkVertically() + fadeOut()
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text("Full Name") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray,
                    focusedLabelColor = Color.Black, cursorColor = Color.Black
                )
            )
        }

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
            keyboardActions = KeyboardActions(onNext = { focusManager.moveFocus(FocusDirection.Down) }),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color.Black, cursorColor = Color.Black
            )
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!isPasswordVisible) }) {
                    Icon(
                        if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = "Toggle Password"
                    )
                }
            },
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Black, unfocusedBorderColor = Color.LightGray,
                focusedLabelColor = Color.Black, cursorColor = Color.Black
            )
        )

        // Password Dimenticata
        AnimatedVisibility(visible = isLoginMode) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Forgot Password?",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterEnd).clickable { },
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
fun AuthActionButtons(
    isLoginMode: Boolean,
    email: String,
    password: String,
    name: String,
    navController: NavController,
    onSignupSuccess: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = {
                // Validazione preliminare degli input
                if (email.isBlank() || password.isBlank() || !email.contains("@")) {
                    Toast.makeText(context, "Email o password non validi.", Toast.LENGTH_SHORT).show()
                    return@Button
                }

                val username = email.substringBefore("@")
                val domain = "@" + email.substringAfter("@")

                if(isLoginMode) {
                    val userRef = Firebase.database.getReference("Users/$username")
                    userRef.get().addOnCompleteListener { task ->
                        if (!task.isSuccessful || !task.result.exists()) {
                            val message = if (!task.result.exists()) "Utente non registrato" else "Errore nel contattare il database"
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                        val dbDomain = task.result.child("Domain").getValue(String::class.java)
                        val dbPassword = task.result.child("Password").getValue(String::class.java)
                        val dbName = task.result.child("Name").getValue(String::class.java)

                        if (dbDomain != domain) {
                            Toast.makeText(context, "Dominio Errato", Toast.LENGTH_SHORT).show()
                        } else if (dbPassword != password) {
                            Toast.makeText(context, "Password Errata", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Login Effettuato, benvenuto $dbName", Toast.LENGTH_SHORT).show()
                            navController.navigate("home")
                        }
                    }
                } else {
                    val usersRef = Firebase.database.getReference("Users")
                    val userRef = usersRef.child(username)
                    userRef.get().addOnCompleteListener { task ->
                        if (!task.isSuccessful || !task.result.exists()) {
                            if(domain == "" || name == "" || password == "") {
                                Toast.makeText(context, "Compila tutti i campi", Toast.LENGTH_SHORT).show()
                                return@addOnCompleteListener
                            }
                            usersRef.child(username).child("Name").setValue(name)
                            usersRef.child(username).child("Domain").setValue(domain)
                            usersRef.child(username).child("Password").setValue(password)

                            Toast.makeText(context, "Utente registrato con successo", Toast.LENGTH_SHORT).show()
                            onSignupSuccess() // Pulisce il form e torna al login
                        } else {
                            Toast.makeText(context, "Utente già registrato", Toast.LENGTH_SHORT).show()
                            return@addOnCompleteListener
                        }
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8BC34A), contentColor = Color.White),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Crossfade(targetState = isLoginMode, label = "ButtonAnim") { login ->
                Text(text = if (login) "Login" else "Create Account", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
            Text(" OR ", color = Color.Gray, fontSize = 12.sp, modifier = Modifier.padding(horizontal = 8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f), color = Color.LightGray)
        }

        OutlinedButton(
            onClick = { navController.navigate("home") },
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Black),
            border = BorderStroke(1.dp, Color.Black),
            modifier = Modifier.fillMaxWidth().height(56.dp)
        ) {
            Text("Continue as Guest", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun SlidingTabSelector(isLoginMode: Boolean, onModeChange: (Boolean) -> Unit) {
    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color(0xFFF5F5F5), RoundedCornerShape(25.dp))
            .padding(4.dp)
    ) {
        val maxWidth = maxWidth
        val tabWidth = maxWidth / 2

        val indicatorOffset by animateDpAsState(
            targetValue = if (isLoginMode) 0.dp else tabWidth,
            animationSpec = tween(durationMillis = 300),
            label = "indicator"
        )

        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .fillMaxHeight()
                .shadow(2.dp, RoundedCornerShape(24.dp))
                .background(Color.White, RoundedCornerShape(24.dp))
        )

        Row(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onModeChange(true) },
                contentAlignment = Alignment.Center
            ) {
                Text("Login", fontWeight = if (isLoginMode) FontWeight.Bold else FontWeight.Normal, color = Color.Black)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) { onModeChange(false) },
                contentAlignment = Alignment.Center
            ) {
                Text("Sign Up", fontWeight = if (!isLoginMode) FontWeight.Bold else FontWeight.Normal, color = Color.Black)
            }
        }
    }
}