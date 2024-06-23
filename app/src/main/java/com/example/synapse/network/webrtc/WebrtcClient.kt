package com.example.synapse.network.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.MediaConstraints
import org.webrtc.PeerConnection
import org.webrtc.PeerConnection.Observer
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer
import javax.inject.Inject

class WebrtcClient @Inject constructor(
    private val context : Context
) {

    private val TAG = "WEBRTC_CLIENT"

    // Initialize manually
    private lateinit var  surfaceView : SurfaceViewRenderer
    private lateinit var observer : Observer

    private var streamerConnection : PeerConnection? = null
    private val viewersConnections : List<PeerConnection>? = null

    private val peerConnectionFactory by lazy { createPeerConnectionFactory() }
    private val eglBaseContext = EglBase.create().eglBaseContext
    private val mediaConstraints = MediaConstraints().apply {
        mandatory.add(MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"))
    }
    private val iceServer = listOf(
        PeerConnection.IceServer(
            "turn:openrelay.metered.ca:443?transport=tcp", "openrelayproject", "openrelayproject"
        )
    )

    init {
        initializePeerConnectionFactoryOptions()
    }

    private fun initializePeerConnectionFactoryOptions(){
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true).setFieldTrials("WebRTC-H264HighProfile/Enabled/")
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)
    }

    private fun createPeerConnectionFactory() : PeerConnectionFactory{
        return PeerConnectionFactory.builder()
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBaseContext))
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBaseContext, true, true))
            .setOptions(
                PeerConnectionFactory.Options().apply {
                    disableEncryption = false
                    disableNetworkMonitor = false
                }
            ).createPeerConnectionFactory()
    }

    private fun createPeerConnection() : PeerConnection? {
        return peerConnectionFactory.createPeerConnection(iceServer, observer)
    }

    private fun createOffer(){
        streamerConnection?.createOffer(object : MySdpObserver(){
            override fun onCreateSuccess(sdp: SessionDescription?) {
                super.onCreateSuccess(sdp)
                setLocalDescription(streamerConnection, sdp)
            }
        }, mediaConstraints)
    }

    private fun createAnswer(){
        streamerConnection?.createAnswer(object : MySdpObserver(){
            override fun onCreateSuccess(sdp: SessionDescription?) {
                super.onCreateSuccess(sdp)
                setLocalDescription(streamerConnection, sdp)
            }
        }, mediaConstraints)
    }

    private fun setLocalDescription(peerConnection : PeerConnection?, sdp : SessionDescription?){
        try {
            peerConnection?.setLocalDescription(object  : MySdpObserver(){
                override fun onSetSuccess() {
                    super.onSetSuccess()
                    Log.d(TAG, "setLocalDescription, onSetSuccess: ")
                }
            }, sdp)
        }
        catch (e : Exception){
            Log.d(TAG, "setLocalDescription: error : ${e.message}")
        }
    }

    fun onRemoteSessionReceived(sdp : SessionDescription){
        setRemoteDescription(streamerConnection, sdp)
    }

    private fun setRemoteDescription(peerConnection : PeerConnection?, sdp : SessionDescription?){
        try {
            peerConnection?.setRemoteDescription(object  : MySdpObserver(){
                override fun onSetSuccess() {
                    super.onSetSuccess()
                    Log.d(TAG, "setRemoteDescription, onSetSuccess: ")
                }
            }, sdp)
        }
        catch (e : Exception){
            Log.d(TAG, "setRemoteDescription: error : ${e.message}")
        }
    }
}