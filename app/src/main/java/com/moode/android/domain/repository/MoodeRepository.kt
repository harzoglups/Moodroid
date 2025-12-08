package com.moode.android.domain.repository

import com.moode.android.domain.model.Result

/**
 * Repository interface for Moode server network operations
 */
interface MoodeRepository {
    suspend fun testConnection(url: String): Result<Unit>
    suspend fun sendVolumeCommand(url: String, command: String, step: Int): Result<Unit>
    suspend fun resolveUrl(url: String): String
}
