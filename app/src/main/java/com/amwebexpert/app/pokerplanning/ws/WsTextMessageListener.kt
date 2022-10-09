package com.amwebexpert.app.pokerplanning.ws

import okio.ByteString

interface WsTextMessageListener {
    fun onConnectSuccess()
    fun onConnectFailed()
    fun onClose()
    fun onMessage(text: String)
}
