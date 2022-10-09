package com.amwebexpert.app.pokerplanning.ws

import android.util.Log
import okhttp3.*
import okio.ByteString
import java.util.*
import java.util.concurrent.TimeUnit

class WebSocketService {
    companion object {
        val instance = WebSocketService()

        private const val NORMAL_CLOSURE_STATUS = 1001
        private val TAG = WebSocketService::class.java.simpleName
    }

    private val postponedMessages: Queue<String> = LinkedList()
    private var isConnected = false
    private lateinit var messageListener: WsTextMessageListener
    private lateinit var webSocket: WebSocket
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .readTimeout(5, TimeUnit.SECONDS)
        .writeTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(10, TimeUnit.SECONDS)
        .build()

    fun connect(isSecure: Boolean, hostname: String, roomUUID: String, listener: WsTextMessageListener) {
        if (isConnected) {
            return
        }

        messageListener = listener
        val url = buildUrl(isSecure, hostname, roomUUID)
        val request: Request = Request.Builder().url(url).build()
        httpClient.newWebSocket(request, createWebSocketListener())
    }

    private fun createWebSocketListener(): WebSocketListener {
        return object: WebSocketListener() {
            override fun onOpen(newWebSocket: WebSocket, response: Response) {
                super.onOpen(webSocket = newWebSocket, response)

                webSocket = newWebSocket
                isConnected = true
                Log.i(TAG, "connect success")
                messageListener.onConnectSuccess()

                flushPostponedMessages()
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                messageListener.onMessage(text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                messageListener.onMessage(bytes.base64())
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.i(TAG, "closing socket: $code / $reason")
            }

            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                isConnected = false
                messageListener.onClose()
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.e(TAG, "connect failed" + t.message, t)
                isConnected = false
                messageListener.onConnectFailed()
            }
        }
    }

    private fun buildUrl(isSecure: Boolean, hostname: String, roomUUID: String): String {
        val protocol = if (isSecure) "wss" else "ws"
        return "$protocol://${hostname}/ws?roomUUID=$roomUUID"
    }


    fun disconnect() {
        if (isConnected) {
            webSocket.cancel()
            webSocket.close(NORMAL_CLOSURE_STATUS, "disconnection")
            isConnected = false
        }
    }

    fun flushPostponedMessages() {
        synchronized(postponedMessages) {
            while(postponedMessages.isNotEmpty()) {
                sendMessage(postponedMessages.poll()!!)
            }
        }
    }

    fun sendMessage(text: String): Boolean {
        return if (isConnected) {
            webSocket.send(text)
            true
        } else {
            postponedMessages.add(text)
            false
        }
    }

    fun sendMessage(byteString: ByteString): Boolean {
        return if (isConnected) {
            webSocket.send(byteString)
            true
        } else {
            postponedMessages.add(byteString.base64())
            false
        }
    }

    // TODO Move this into poker planning messages factory
    fun sendVote(username: String, value: String) {
        if (!isConnected) {
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
        sendMessage(message)
    }

}