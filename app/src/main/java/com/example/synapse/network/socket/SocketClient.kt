package com.example.synapse.network.socket

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import okio.ByteString
import javax.inject.Singleton

@Singleton
class SocketClient {
    private val TAG = "WEBSOCKET"
    private var webSocket : WebSocket? = null
    val okHttpClient = OkHttpClient()

    fun createStreamConnection(token : String, listener : SocketListener){
        val streamUrl = "what ever url"
        val request = Request.Builder()
            .addHeader("Authentication-Token", token)
            .url(streamUrl)
            .build()
        webSocket?.close(1000, "already exist...switching")

        webSocket = okHttpClient.newWebSocket(request, getWebSocketListener(listener))
    }

    fun createWatchConnection(streamId : String, token : String, listener : SocketListener){
        var watchUrl = "what ever url$streamId"
        val request = Request.Builder()
            .addHeader("Authentication-Token", token)
            .url(watchUrl)
            .build()
        webSocket?.close(1000, "already exist...switching")

        webSocket = okHttpClient.newWebSocket(request, getWebSocketListener(listener))
    }

    fun closeConnection(){
        webSocket?.close(1001, "closing manually")
        webSocket = null
    }

    fun isConnected() : Boolean{
        return webSocket != null
    }

    private fun getWebSocketListener(listener: SocketListener) : WebSocketListener{
        return  object: WebSocketListener(){
            override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosed(webSocket, code, reason)
                Log.d(TAG, "onClosed: code: $code, reason: $reason")
            }

            override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
                super.onClosing(webSocket, code, reason)
                Log.d(TAG, "onClosing: code: $code, reason: $reason")
            }

            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                super.onFailure(webSocket, t, response)
                Log.d(TAG, "onFailure: ")
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                super.onMessage(webSocket, text)
                Log.d(TAG, "onMessage:  $text")
                listener.onSocketMessage(1, message = text)
            }

            override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
                super.onMessage(webSocket, bytes)
                Log.d(TAG, "onMessage (Bytes Type): $bytes")
            }

            override fun onOpen(webSocket: WebSocket, response: Response) {
                super.onOpen(webSocket, response)
                Log.d(TAG, "onOpen: ${response.body}")
            }
        }
    }

}