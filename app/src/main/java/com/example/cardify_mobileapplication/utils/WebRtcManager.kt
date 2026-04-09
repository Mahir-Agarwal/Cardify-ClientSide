package com.example.cardify_mobileapplication.utils

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.webrtc.*
import java.nio.ByteBuffer
import java.nio.charset.Charset

class WebRtcManager(private val context: Context) {

    private var peerConnectionFactory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null
    private var dataChannel: DataChannel? = null

    var onSignalReady: ((String) -> Unit)? = null

    private val _secureMessages = MutableStateFlow<String?>(null)
    val secureMessages = _secureMessages.asStateFlow()

    init {
        val options = PeerConnectionFactory.InitializationOptions.builder(context)
            .setEnableInternalTracer(true)
            .createInitializationOptions()
        PeerConnectionFactory.initialize(options)

        val factoryOptions = PeerConnectionFactory.Options().apply {
            disableNetworkMonitor = true
        }
        peerConnectionFactory = PeerConnectionFactory.builder()
            .setOptions(factoryOptions)
            .createPeerConnectionFactory()
    }

    fun startPeerConnection(isInitiator: Boolean) {
        val iceServers = listOf(PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer())
        val rtcConfig = PeerConnection.RTCConfiguration(iceServers)

        peerConnection = peerConnectionFactory?.createPeerConnection(rtcConfig, object : PeerConnection.Observer {
            override fun onSignalingChange(state: PeerConnection.SignalingState?) {}
            override fun onIceConnectionChange(state: PeerConnection.IceConnectionState?) {}
            override fun onIceConnectionReceivingChange(receiving: Boolean) {}
            override fun onIceGatheringChange(state: PeerConnection.IceGatheringState?) {}

            override fun onIceCandidate(candidate: IceCandidate?) {
                candidate?.let {
                    // Normally you'd format this to JSON and send to onSignalReady
                    val signalPayload = "{\"type\":\"candidate\",\"sdpMid\":\"${it.sdpMid}\",\"sdpMLineIndex\":${it.sdpMLineIndex},\"candidate\":\"${it.sdp}\"}"
                    onSignalReady?.invoke(signalPayload)
                }
            }

            override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
            override fun onAddStream(stream: MediaStream?) {}
            override fun onRemoveStream(stream: MediaStream?) {}
            override fun onDataChannel(dc: DataChannel?) {
                dataChannel = dc
                setupDataChannelObserver(dataChannel)
            }
            override fun onRenegotiationNeeded() {}
            override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {}
        })

        if (isInitiator) {
            val dcInit = DataChannel.Init()
            dataChannel = peerConnection?.createDataChannel("secure_card_channel", dcInit)
            setupDataChannelObserver(dataChannel)

            peerConnection?.createOffer(SdpObserverImpl(), MediaConstraints())
        }
    }

    fun sendSecureData(data: String) {
        if (dataChannel?.state() == DataChannel.State.OPEN) {
            val buffer = DataChannel.Buffer(ByteBuffer.wrap(data.toByteArray(Charset.defaultCharset())), false)
            dataChannel?.send(buffer)
        } else {
            Log.e("WebRtcManager", "DataChannel not open")
        }
    }

    private fun setupDataChannelObserver(dc: DataChannel?) {
        dc?.registerObserver(object : DataChannel.Observer {
            override fun onBufferedAmountChange(previousAmount: Long) {}
            override fun onStateChange() {
                Log.d("WebRtcManager", "DataChannel state: ${dc.state()}")
            }
            override fun onMessage(buffer: DataChannel.Buffer?) {
                buffer?.data?.let {
                    val bytes = ByteArray(it.remaining())
                    it.get(bytes)
                    val msg = String(bytes)
                    _secureMessages.value = msg
                }
            }
        })
    }

    // Handles incoming JSON SDP packets from the WebSocket signaling track
    fun handleIncomingSignal(payload: String) {
        // Here you would parse the JSON and route to PC.addIceCandidate or PC.setRemoteDescription
        Log.d("WebRtcManager", "Handling incoming peer signal: $payload")
    }

    fun dispose() {
        dataChannel?.dispose()
        peerConnection?.dispose()
        peerConnectionFactory?.dispose()
    }

    private inner class SdpObserverImpl : SdpObserver {
        override fun onCreateSuccess(desc: SessionDescription?) {
            peerConnection?.setLocalDescription(this, desc)
            desc?.let {
                val signalPayload = "{\"type\":\"${it.type.canonicalForm()}\",\"sdp\":\"${it.description}\"}"
                onSignalReady?.invoke(signalPayload)
            }
        }
        override fun onSetSuccess() {}
        override fun onCreateFailure(error: String?) {}
        override fun onSetFailure(error: String?) {}
    }
}
