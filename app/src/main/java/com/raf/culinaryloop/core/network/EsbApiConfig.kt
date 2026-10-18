package com.raf.culinaryloop.core.network

/**
 * ESB API Platform configuration endpoints and headers.
 * References: https://developers.esb.co.id/
 */
object EsbApiConfig {
    const val BASE_URL = "https://api-sandbox.esb.co.id/v1/"
    const val HEADER_API_KEY = "X-ESB-Api-Key"
    const val HEADER_TIMESTAMP = "X-ESB-Timestamp"
    const val HEADER_SIGNATURE = "X-ESB-Signature"
    const val HEADER_AUTHORIZATION = "Authorization"

    // Default Sandbox Credentials (when not injected via environment)
    const val DEFAULT_SANDBOX_KEY = "esb_sandbox_demo_key_2026"
    const val DEFAULT_SANDBOX_SECRET = "esb_sec_demo_signature_883"
}
