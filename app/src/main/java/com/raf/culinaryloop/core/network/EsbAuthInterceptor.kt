package com.raf.culinaryloop.core.network

import okhttp3.Interceptor
import okhttp3.Response
import java.security.MessageDigest

/**
 * OkHttp Interceptor responsible for signing ESB Loop and ESB Order API requests.
 */
class EsbAuthInterceptor(
    private val apiKey: String = EsbApiConfig.DEFAULT_SANDBOX_KEY,
    private val apiSecret: String = EsbApiConfig.DEFAULT_SANDBOX_SECRET
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val timestamp = System.currentTimeMillis().toString()
        val signaturePayload = "$apiKey:$timestamp:$apiSecret"
        val signature = computeSha256(signaturePayload)

        val newRequest = originalRequest.newBuilder()
            .header(EsbApiConfig.HEADER_API_KEY, apiKey)
            .header(EsbApiConfig.HEADER_TIMESTAMP, timestamp)
            .header(EsbApiConfig.HEADER_SIGNATURE, signature)
            .header(EsbApiConfig.HEADER_AUTHORIZATION, "Bearer sandbox-token-$timestamp")
            .header("Accept", "application/json")
            .header("Content-Type", "application/json")
            .build()

        return chain.proceed(newRequest)
    }

    private fun computeSha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
