package com.moode.android.domain.repository

import com.moode.android.domain.model.Result
import com.moode.android.domain.model.VolumeDirection

/**
 * Repository interface for Moode server network operations
 */
interface MoodeRepository {
    suspend fun testConnection(url: String): Result<Unit>
    suspend fun sendVolumeCommand(url: String, direction: VolumeDirection, step: Int): Result<Unit>
    suspend fun resolveUrl(url: String): String
}
