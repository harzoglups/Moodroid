package com.moode.android.data.source.remote

import android.util.Log
import com.moode.android.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for Moode server API operations
 */
@Singleton
class MoodeRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient
) {
    companion object {
        private const val TAG = "MoodeRemoteDataSource"
    }
    
    suspend fun testConnection(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.i(TAG, "Testing connection to: $url")
            val request = Request.Builder()
                .url(url)
                .head()
                .build()
            
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.i(TAG, "Connection test successful: ${response.code}")
                    Result.Success(Unit)
                } else {
                    Log.e(TAG, "Connection test failed: ${response.code}")
                    Result.Error(IOException("Server returned error: ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection test exception: ${e.message}", e)
            Result.Error(e)
        }
    }
    
    suspend fun sendCommand(url: String): Result<Unit> = withContext(Dispatchers.IO) {
        return@withContext try {
            Log.i(TAG, "Sending command to: $url")
            val request = Request.Builder()
                .url(url)
                .build()
            
            okHttpClient.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Log.i(TAG, "Command sent successfully: ${response.code}")
                    Result.Success(Unit)
                } else {
                    Log.e(TAG, "Command failed: ${response.code}")
                    Result.Error(IOException("Server returned error: ${response.code}"))
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Command exception: ${e.message}", e)
            Result.Error(e)
        }
    }
}
