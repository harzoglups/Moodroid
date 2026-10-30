package com.moode.android.data

import com.moode.android.data.source.remote.MoodeRemoteDataSource
import com.moode.android.domain.model.Result
import com.moode.android.domain.model.VolumeDirection
import com.moode.android.domain.repository.MoodeRepository
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation of MoodeRepository
 */
@Singleton
class MoodeRepositoryImpl @Inject constructor(
    private val moodeRemoteDataSource: MoodeRemoteDataSource
) : MoodeRepository {
    
    override suspend fun testConnection(url: String): Result<Unit> {
        return moodeRemoteDataSource.testConnection(url)
    }
    
    override suspend fun sendVolumeCommand(
        url: String,
        direction: VolumeDirection,
        step: Int
    ): Result<Unit> {
        val baseUrl = url.toHttpUrlOrNull()
            ?: return Result.Error(IllegalArgumentException("Invalid server URL: $url"))
        val token = when (direction) {
            VolumeDirection.UP -> "up"
            VolumeDirection.DOWN -> "dn"
        }
        // Resulting request: <base>/command/?cmd=set_volume -<up|dn> <step>
        val commandUrl = baseUrl.newBuilder()
            .addPathSegments("command/")
            .addQueryParameter("cmd", "set_volume -$token $step")
            .build()
        return moodeRemoteDataSource.sendCommand(commandUrl.toString())
    }
    
    override suspend fun resolveUrl(url: String): String {
        return moodeRemoteDataSource.resolveUrl(url)
    }
}
