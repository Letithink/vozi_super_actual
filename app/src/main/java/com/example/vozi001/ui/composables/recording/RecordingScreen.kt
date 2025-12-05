package com.example.vozi001.ui.composables.recording

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.vozi001.viewmodel.SessionViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordingScreen(
    sessionViewModel: SessionViewModel = viewModel(),
    onBack: () -> Unit,
    onOpenSettings: () -> Unit = {},
    onInstallVoiceTyping: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by sessionViewModel.uiState.collectAsState()

    // Estados locales
    val isListening = uiState.isListening
    val targetWord = uiState.targetWord
    val heardText = uiState.heardText
    val scoreStars = uiState.scoreStars
    val errorMessage = uiState.errorMessage
    val speechAvailable = uiState.speechAvailable
    val isLoading = uiState.isLoading

    // Permisos
    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasPermission = isGranted
        if (!isGranted) {
            sessionViewModel.onSpeechError("Permiso de micrófono denegado")
        }
    }

    // Animación de pulso para el micrófono
    val infiniteTransition = rememberInfiniteTransition(label = "micPulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isListening) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Mostrar diálogo de error si speech no está disponible
    if (!speechAvailable) {
        AlertDialog(
            onDismissRequest = { /* No permitir cerrar */ },
            title = { Text("Reconocimiento de Voz No Disponible") },
            text = {
                Column {
                    Text("Esta función requiere Google Speech Recognition.")
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Por favor:")
                    Text("1. Instala 'Google Voice Typing' desde Play Store")
                    Text("2. O usa un dispositivo con Google Services")
                }
            },
            confirmButton = {
                Button(onClick = onInstallVoiceTyping) {
                    Text("Instalar Google Voice Typing")
                }
            },
            dismissButton = {
                Button(
                    onClick = onBack,
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
                ) {
                    Text("Volver")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Práctica de Pronunciación") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {

            // --- SECCIÓN SUPERIOR: Información ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Di la palabra:",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Palabra objetivo
                Text(
                    text = targetWord.uppercase(),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Lo que escuchó el sistema
                if (heardText.isNotEmpty()) {
                    Text(
                        text = "Escuché:",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Visualización de letras coloreadas
                    Row(
                        modifier = Modifier.wrapContentWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        targetWord.uppercase().forEachIndexed { index, targetChar ->
                            val heardChar = heardText.uppercase().getOrNull(index)
                            val isCorrect = heardChar == targetChar

                            val backgroundColor = if (isCorrect) Color(0xFF4CAF50)
                            else Color(0xFFF44336)
                            val displayChar = heardChar ?: targetChar

                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(
                                        color = backgroundColor,
                                        shape = RoundedCornerShape(8.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = displayChar.toString(),
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                )
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN CENTRAL: Feedback ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                when {
                    isLoading -> {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Guardando resultado...")
                    }

                    heardText.isNotEmpty() && !isListening -> {
                        // Mostrar estrellas
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            repeat(3) { index ->
                                Icon(
                                    imageVector = if (index < scoreStars)
                                        Icons.Default.Star else Icons.Default.StarBorder,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Color(0xFFFFD700)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = when (scoreStars) {
                                3 -> "¡Excelente! 🎉"
                                2 -> "¡Muy bien! 👍"
                                else -> "¡Buen intento! 👏"
                            },
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (uiState.lastPracticeSaved) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Resultado guardado ✓",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.Green
                            )
                        }
                    }

                    isListening -> {
                        Text(
                            text = "🎤 Escuchando... Habla ahora",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "(Habla claramente y cerca del micrófono)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    else -> {
                        Text(
                            text = "Toca el micrófono para empezar",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                // Mostrar mensaje de error
                errorMessage?.let { message ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFFEBEE)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Error,
                                contentDescription = null,
                                tint = Color.Red,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = message,
                                color = Color.Red,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = { sessionViewModel.clearError() }
                            ) {
                                Icon(Icons.Default.Close, contentDescription = "Cerrar")
                            }
                        }
                    }
                }
            }

            // --- SECCIÓN INFERIOR: Botón de Micrófono ---
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(150.dp)
                        .scale(scale),
                    contentAlignment = Alignment.Center
                ) {
                    Button(
                        onClick = {
                            if (!hasPermission) {
                                permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                return@Button
                            }

                            if (!speechAvailable) {
                                onInstallVoiceTyping()
                                return@Button
                            }

                            if (isListening) {
                                sessionViewModel.stopPractice()
                            } else {
                                sessionViewModel.startPractice()
                            }
                        },
                        modifier = Modifier.size(100.dp),
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isListening) Color.Red
                            else MaterialTheme.colorScheme.primary,
                            disabledContainerColor = Color.Gray
                        ),
                        enabled = hasPermission && speechAvailable && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = if (isListening) "Detener" else "Grabar",
                            modifier = Modifier.size(48.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Indicadores de estado
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Indicador de permiso
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (hasPermission) Icons.Default.CheckCircle
                            else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (hasPermission) Color.Green else Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Micrófono",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }

                    // Indicador de servicio de voz
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (speechAvailable) Icons.Default.CheckCircle
                            else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (speechAvailable) Color.Green else Color.Red,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Voz",
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de configuración si hay problemas
                if (!hasPermission || !speechAvailable) {
                    Button(
                        onClick = onOpenSettings,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Gray
                        )
                    ) {
                        Icon(Icons.Default.Settings, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Abrir Configuración")
                    }
                }
            }
        }
    }
}