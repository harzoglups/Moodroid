package com.moode.android.data

import com.moode.android.data.source.remote.MoodeRemoteDataSource
import com.moode.android.domain.model.Result
import com.moode.android.domain.repository.MoodeRepository
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
    
    override suspend fun sendVolumeCommand(url: String, command: String, step: Int): Result<Unit> {
        val commandUrl = "$url/command/?cmd=set_volume%20-$command%20$step"
        return moodeRemoteDataSource.sendCommand(commandUrl)
    }
}
