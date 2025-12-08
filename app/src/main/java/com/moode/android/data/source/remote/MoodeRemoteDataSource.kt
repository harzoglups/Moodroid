package com.moode.android.data.source.remote

import android.util.Log
import com.moode.android.domain.model.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException
import java.net.InetAddress
import java.net.URL
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Remote data source for Moode server API operations
 */
@Singleton
class MoodeRemoteDataSource @Inject constructor(
    private val okHttpClient: OkHttpClient,
    private val mdnsResolver: MdnsResolver
) {
    companion object {
        private const val TAG = "MoodeRemoteDataSource"
    }
    
    /**
     * Resolves a URL with mDNS hostname (.local) to an IP-based URL
     * Returns the original URL if it's already an IP or resolution fails
     */
    suspend fun resolveUrl(url: String): String = withContext(Dispatchers.IO) {
        return@withContext try {
            val urlObj = URL(url)
            val hostname = urlObj.host
            
            // Check if hostname ends with .local (mDNS)
            if (!hostname.endsWith(".local", ignoreCase = true)) {
                Log.d(TAG, "URL doesn't use mDNS, returning as-is: $url")
                return@withContext url
            }
            
            Log.i(TAG, "Resolving mDNS hostname: $hostname")
            val address = mdnsResolver.resolveHostname(hostname)
            
            if (address != null) {
                val ipAddress = address.hostAddress
                val resolvedUrl = url.replace(hostname, ipAddress ?: hostname)
                Log.i(TAG, "Resolved $hostname -> $ipAddress")
                Log.i(TAG, "Original URL: $url")
                Log.i(TAG, "Resolved URL: $resolvedUrl")
                resolvedUrl
            } else {
                Log.w(TAG, "Could not resolve $hostname using mDNS, using original URL")
                url
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error resolving mDNS hostname: ${e.message}", e)
            Log.i(TAG, "Fallback: using original URL: $url")
            url
        }
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
