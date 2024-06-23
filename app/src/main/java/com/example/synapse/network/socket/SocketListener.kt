package com.example.synapse.network.socket

interface SocketListener {
    fun onSocketMessage(type : Int, message : Any)
}