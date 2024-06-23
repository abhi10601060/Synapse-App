package com.example.synapse.network.webrtc

import android.util.Log
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

open class MySdpObserver : SdpObserver {
    override fun onCreateSuccess(p0: SessionDescription?) {
        Log.d("MySdpObserver", "onCreateSuccess: ")
    }

    override fun onSetSuccess() {
        Log.d("MySdpObserver", "onSetSuccess: ")
    }

    override fun onCreateFailure(p0: String?) {
        Log.d("MySdpObserver", "onCreateFailure: ")
    }

    override fun onSetFailure(p0: String?) {
        Log.d("MySdpObserver", "onSetFailure: ")
    }
}