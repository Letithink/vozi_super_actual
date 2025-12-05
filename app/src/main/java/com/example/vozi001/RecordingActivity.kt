package com.example.vozi001

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat
import com.example.vozi001.ui.composables.recording.RecordingScreen
import com.example.vozi001.ui.theme.Vozi001Theme
import com.example.vozi001.viewmodel.SessionViewModel

class RecordingActivity : ComponentActivity() {

    companion object {
        private const val TAG = "VoziApp/RecordingActivity"
    }

    private val sessionViewModel: SessionViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        Log.d(TAG, "Permiso de micrófono concedido: $isGranted")
        if (isGranted) {
            // El Composable manejará el inicio
        } else {
            Log.e(TAG, "❌ Permiso de micrófono DENEGADO")
            showPermissionDeniedDialog()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        Log.d(TAG, "=== onCreate() ===")
        Log.d(TAG, "Dispositivo: ${Build.MANUFACTURER} ${Build.MODEL}")

        // Verificar si es Xiaomi/MIUI para permisos especiales
        if (Build.MANUFACTURER.equals("xiaomi", ignoreCase = true)) {
            Log.d(TAG, "⚠️ Dispositivo Xiaomi detectado, verificando permisos especiales")
            checkMiuiPermissions()
        }

        // Verificar permiso inicial
        checkAudioPermission()

        setContent {
            Vozi001Theme {
                // Efecto para manejar cambios en el estado de disponibilidad
                LaunchedEffect(Unit) {
                    Log.d(TAG, "Composable LaunchedEffect ejecutado")
                }

                RecordingScreen(
                    sessionViewModel = sessionViewModel,
                    onBack = { finish() },
                    onOpenSettings = { openAppSettings() },
                    onInstallVoiceTyping = { openGoogleVoiceTyping() }
                )
            }
        }
    }

    private fun checkAudioPermission() {
        val hasPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == android.content.pm.PackageManager.PERMISSION_GRANTED

        Log.d(TAG, "Permiso de audio actual: $hasPermission")

        if (!hasPermission) {
            Log.d(TAG, "Solicitando permiso de audio...")
            requestPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    private fun checkMiuiPermissions() {
        try {
            // Intent para abrir configuración de permisos de MIUI
            val intent = Intent("miui.intent.action.APP_PERM_EDITOR")
            intent.setClassName(
                "com.miui.securitycenter",
                "com.miui.permcenter.permissions.PermissionsEditorActivity"
            )
            intent.putExtra("extra_pkgname", packageName)

            if (intent.resolveActivity(packageManager) != null) {
                Log.d(TAG, "MIUI permissions editor disponible")
                // Puedes iniciar esto si lo necesitas
                // startActivity(intent)
            }
        } catch (e: Exception) {
            Log.w(TAG, "No se pudo abrir editor de permisos MIUI: ${e.message}")
        }
    }

    private fun showPermissionDeniedDialog() {
        // Podrías mostrar un AlertDialog aquí explicando por qué necesitas el permiso
        Log.e(TAG, "Mostrar diálogo de permiso denegado")
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        val uri = Uri.fromParts("package", packageName, null)
        intent.data = uri
        startActivity(intent)
    }

    private fun openGoogleVoiceTyping() {
        try {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("market://details?id=com.google.android.tts")
                setPackage("com.android.vending")
            }
            startActivity(intent)
        } catch (e: Exception) {
            // Fallback a Play Store web
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.tts")
            }
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume() - Verificando permisos nuevamente")
        checkAudioPermission()
    }

    override fun onPause() {
        super.onPause()
        Log.d(TAG, "⏸️ onPause() - Deteniendo reconocimiento de voz...")
        sessionViewModel.stopPractice()
    }

    override fun onStop() {
        super.onStop()
        Log.d(TAG, "⏹️ onStop() - Activity detenida")
    }
}