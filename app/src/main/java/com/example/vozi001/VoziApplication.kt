package com.example.vozi001

import android.app.Application
import android.util.Log
import com.google.firebase.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.google.firebase.perf.ktx.performance

class VoziApplication : Application() {

    companion object {
        private const val TAG = "VoziApp/Application"
        var isDebug: Boolean = false
    }

    override fun onCreate() {
        super.onCreate()

        isDebug = BuildConfig.DEBUG

        Log.d(TAG, "=== VoziApplication.onCreate() ===")
        Log.d(TAG, "Package: ${applicationContext.packageName}")
        Log.d(TAG, "Debug mode: $isDebug")

        try {
            // Inicializar Firebase
            Firebase.initialize(this)
            Log.d(TAG, "✅ Firebase inicializado correctamente")

            // Configurar Firestore para mejor rendimiento offline
            setupFirestore()

            // Configurar Performance Monitoring
            setupPerformanceMonitoring()

            // Configurar Crashlytics (opcional)
            setupCrashReporting()

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error inicializando Firebase: ${e.message}", e)
        }

        // Inicializar otros componentes
        initializeAppComponents()
    }

    private fun setupFirestore() {
        try {
            val settings = FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(true) // Habilitar cache offline
                .setCacheSizeBytes(FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED)
                .build()

            FirebaseFirestore.getInstance().firestoreSettings = settings
            Log.d(TAG, "✅ Firestore configurado con persistencia offline")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error configurando Firestore: ${e.message}")
        }
    }

    private fun setupPerformanceMonitoring() {
        try {
            val perf = Firebase.performance

            // Habilitar/deshabilitar según modo debug
            if (isDebug) {
                // En desarrollo, puedes deshabilitar si quieres
                // FirebasePerformance.getInstance().isPerformanceCollectionEnabled = false
                Log.d(TAG, "📊 Performance Monitoring: MODO DEBUG")
            } else {
                Log.d(TAG, "📊 Performance Monitoring: MODO PRODUCCIÓN")
            }

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error configurando Performance Monitoring: ${e.message}")
        }
    }

    private fun setupCrashReporting() {
        try {
            // Si usas Crashlytics, inicialízalo aquí
            // FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(true)
            Log.d(TAG, "⚠️ Crash Reporting no configurado (agregar dependencia si es necesario)")

        } catch (e: Exception) {
            Log.e(TAG, "❌ Error configurando Crash Reporting: ${e.message}")
        }
    }

    private fun initializeAppComponents() {
        Log.d(TAG, "🔄 Inicializando componentes de la aplicación...")

        // Aquí puedes inicializar otras librerías o servicios
        // Ejemplo: inicializar un cliente HTTP, base de datos local, etc.

        Log.d(TAG, "✅ Componentes de la aplicación inicializados")
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.w(TAG, "⚠️ Memoria baja detectada - limpiando cache si es necesario")
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when (level) {
            TRIM_MEMORY_COMPLETE -> Log.w(TAG, "⚠️ TRIM_MEMORY_COMPLETE - App puede ser eliminada")
            TRIM_MEMORY_MODERATE -> Log.d(TAG, "🔄 TRIM_MEMORY_MODERATE - Liberar recursos no críticos")
            TRIM_MEMORY_BACKGROUND -> Log.d(TAG, "🔄 TRIM_MEMORY_BACKGROUND - App en background")
        }
    }
}