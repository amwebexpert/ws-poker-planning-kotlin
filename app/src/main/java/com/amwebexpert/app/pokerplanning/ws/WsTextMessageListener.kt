package com.amwebexpert.app.pokerplanning.ws

interface WsTextMessageListener {
    fun onConnectSuccess()
    fun onConnectFailed()
    fun onClose()
    fun onMessage(text: String)
}
