package com.amwebexpert.app.pokerplanning.ws

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.TimeUnit

class WebSocketService {
    companion object {
        val instance = WebSocketService()
        private val NORMAL_CLOSURE_STATUS = 1000
    }

    private var webSocket: WebSocket? = null
    private var httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(3, TimeUnit.SECONDS)
        .build()

    fun connect(hostname: String, wsListener: WebSocketListener) {
        webSocket?.cancel()

        val request = buildRequest(hostname)
        webSocket = httpClient.newWebSocket(request, wsListener)
    }

    private fun buildUrl(hostname: String): String = "wss://${hostname}/ws?roomUUID=e78caaee-a1a2-4298-860d-81d7752226ae"

    private fun buildRequest(hostname: String): Request = Request.Builder().url(buildUrl(hostname)).build()

    fun disconnect() {
        webSocket?.close(NORMAL_CLOSURE_STATUS, "disconnection")
    }

    fun sendVote(username: String, value: String) {
        if (webSocket === null) {
            return
        }

        val estimatedAtISO8601 = String.format("%tFT%<tT.%<tLZ", Calendar.getInstance(TimeZone.getTimeZone("Z")))
        val message = """
                        {
                            "type": "vote",
                            "payload": {
                                "username": "$username",
                                "estimate": "$value",
                                "estimatedAtISO8601": "$estimatedAtISO8601"
                            }
                        }
                    """.trimIndent()
        webSocket?.send(message)
    }

}