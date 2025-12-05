package com.example.vozi001.api

import android.util.Log
import com.example.vozi001.api.models.Session
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

/**
 * Servicio para interactuar con la API de sesiones en Firebase
 */
class SessionApiService(
    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    
    companion object {
        private const val TAG = "SessionApiService"
        private const val SESSIONS_COLLECTION = "sessions"
    }
    
    /**
     * Obtiene todas las sesiones
     */
    suspend fun getAllSessions(): Result<List<Session>> {
        return try {
            val snapshot = firestore.collection(SESSIONS_COLLECTION)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Session.fromMap(doc.id, it) }
            }
            
            Log.d(TAG, "Obtenidas ${sessions.size} sesiones")
            Result.success(sessions)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener sesiones: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene sesiones de un terapeuta específico
     */
    suspend fun getSessionsByTherapist(therapistId: String): Result<List<Session>> {
        return try {
            val snapshot = firestore.collection(SESSIONS_COLLECTION)
                .whereEqualTo("therapistId", therapistId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Session.fromMap(doc.id, it) }
            }
            
            Result.success(sessions)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener sesiones del terapeuta: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene sesiones asignadas a un niño
     */
    suspend fun getSessionsByChild(childId: String): Result<List<Session>> {
        return try {
            val snapshot = firestore.collection(SESSIONS_COLLECTION)
                .whereEqualTo("childId", childId)
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()
            
            val sessions = snapshot.documents.mapNotNull { doc ->
                doc.data?.let { Session.fromMap(doc.id, it) }
            }
            
            Result.success(sessions)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener sesiones del niño: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Obtiene una sesión por ID
     */
    suspend fun getSessionById(sessionId: String): Result<Session> {
        return try {
            val doc = firestore.collection(SESSIONS_COLLECTION)
                .document(sessionId)
                .get()
                .await()
            
            val session = doc.data?.let { Session.fromMap(doc.id, it) }
                ?: return Result.failure(Exception("Sesión no encontrada"))
            
            Result.success(session)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Crea una nueva sesión
     */
    suspend fun createSession(session: Session): Result<String> {
        return try {
            val docRef = firestore.collection(SESSIONS_COLLECTION).document()
            val sessionWithId = session.copy(id = docRef.id)
            
            docRef.set(sessionWithId.toMap()).await()
            
            Log.d(TAG, "Sesión creada con ID: ${docRef.id}")
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e(TAG, "Error al crear sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Actualiza una sesión existente
     */
    suspend fun updateSession(session: Session): Result<Unit> {
        return try {
            require(session.id.isNotEmpty()) { "El ID de sesión no puede estar vacío" }
            
            val updatedSession = session.copy(updatedAt = Timestamp.now())
            
            firestore.collection(SESSIONS_COLLECTION)
                .document(session.id)
                .set(updatedSession.toMap())
                .await()
            
            Log.d(TAG, "Sesión actualizada: ${session.id}")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Elimina una sesión
     */
    suspend fun deleteSession(sessionId: String): Result<Unit> {
        return try {
            firestore.collection(SESSIONS_COLLECTION)
                .document(sessionId)
                .delete()
                .await()
            
            Log.d(TAG, "Sesión eliminada: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al eliminar sesión: ${e.message}")
            Result.failure(e)
        }
    }
    
    /**
     * Marca una sesión como completada
     */
    suspend fun markSessionAsCompleted(sessionId: String): Result<Unit> {
        return try {
            firestore.collection(SESSIONS_COLLECTION)
                .document(sessionId)
                .update(
                    mapOf(
                        "isCompleted" to true,
                        "completedAt" to Timestamp.now(),
                        "updatedAt" to Timestamp.now()
                    )
                )
                .await()
            
            Log.d(TAG, "Sesión marcada como completada: $sessionId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error al marcar sesión como completada: ${e.message}")
            Result.failure(e)
        }
    }
}
