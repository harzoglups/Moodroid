package com.moode.android.data.source.remote

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withTimeoutOrNull
import java.net.InetAddress
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

/**
 * Resolves mDNS hostnames (.local) using Android NsdManager
 */
@Singleton
class MdnsResolver @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val TAG = "MdnsResolver"
        private const val SERVICE_TYPE = "_http._tcp."
        private const val RESOLUTION_TIMEOUT_MS = 5000L
    }
    
    /**
     * Resolves a .local hostname to an IP address using mDNS
     * Returns null if resolution fails or times out
     */
    suspend fun resolveHostname(hostname: String): InetAddress? {
        if (!hostname.endsWith(".local", ignoreCase = true)) {
            Log.d(TAG, "Hostname $hostname is not a .local domain")
            return null
        }
        
        val serviceName = hostname.removeSuffix(".local").removeSuffix(".")
        Log.i(TAG, "Attempting to resolve mDNS service: $serviceName")
        
        return withTimeoutOrNull(RESOLUTION_TIMEOUT_MS) {
            suspendCancellableCoroutine { continuation ->
                val nsdManager = context.getSystemService(Context.NSD_SERVICE) as NsdManager
                
                val resolveListener = object : NsdManager.ResolveListener {
                    override fun onResolveFailed(serviceInfo: NsdServiceInfo?, errorCode: Int) {
                        Log.w(TAG, "mDNS resolution failed for $serviceName, error: $errorCode")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                    
                    override fun onServiceResolved(serviceInfo: NsdServiceInfo?) {
                        val host = serviceInfo?.host
                        Log.i(TAG, "mDNS service resolved: ${serviceInfo?.serviceName} -> $host")
                        if (continuation.isActive) {
                            continuation.resume(host)
                        }
                    }
                }
                
                val discoveryListener = object : NsdManager.DiscoveryListener {
                    override fun onDiscoveryStarted(serviceType: String?) {
                        Log.d(TAG, "mDNS discovery started for $serviceType")
                    }
                    
                    override fun onServiceFound(serviceInfo: NsdServiceInfo?) {
                        Log.d(TAG, "mDNS service found: ${serviceInfo?.serviceName}")
                        // Match service names containing the hostname (case-insensitive)
                        // e.g., "moOde audio player: Moode-2" contains "moode"
                        val serviceNameLower = serviceInfo?.serviceName?.lowercase()
                        val targetNameLower = serviceName.lowercase()
                        if (serviceNameLower?.contains(targetNameLower) == true) {
                            Log.i(TAG, "Found matching service: ${serviceInfo.serviceName}, resolving...")
                            nsdManager.stopServiceDiscovery(this)
                            nsdManager.resolveService(serviceInfo, resolveListener)
                        }
                    }
                    
                    override fun onServiceLost(serviceInfo: NsdServiceInfo?) {
                        Log.d(TAG, "mDNS service lost: ${serviceInfo?.serviceName}")
                    }
                    
                    override fun onDiscoveryStopped(serviceType: String?) {
                        Log.d(TAG, "mDNS discovery stopped for $serviceType")
                    }
                    
                    override fun onStartDiscoveryFailed(serviceType: String?, errorCode: Int) {
                        Log.e(TAG, "mDNS discovery start failed for $serviceType, error: $errorCode")
                        if (continuation.isActive) {
                            continuation.resume(null)
                        }
                    }
                    
                    override fun onStopDiscoveryFailed(serviceType: String?, errorCode: Int) {
                        Log.e(TAG, "mDNS discovery stop failed for $serviceType, error: $errorCode")
                    }
                }
                
                continuation.invokeOnCancellation {
                    try {
                        nsdManager.stopServiceDiscovery(discoveryListener)
                    } catch (e: Exception) {
                        Log.w(TAG, "Error stopping discovery: ${e.message}")
                    }
                }
                
                try {
                    nsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, discoveryListener)
                } catch (e: Exception) {
                    Log.e(TAG, "Error starting mDNS discovery: ${e.message}", e)
                    if (continuation.isActive) {
                        continuation.resume(null)
                    }
                }
            }
        }
    }
}
