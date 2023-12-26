package com.karlmusingo.limit_phone_call

import android.content.ComponentName
import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.telecom.*
import android.widget.Toast

class MyConnectionService: ConnectionService() {
    private val telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager

    override fun onCreateOutgoingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        Toast.makeText(this, "onCreateOutgoingConnection: We are here !!!!!!!", Toast.LENGTH_SHORT)
//        return super.onCreateOutgoingConnection(connectionManagerPhoneAccount, request)

        // Create a custom Connection object for the outgoing call
        val connection = YourConnection()
        // Perform any necessary configuration of the connection

        println(request?.toString())

        request?.accountHandle

        // Obtain the Call object associated with the Connection
//        val call: Parcelable? = request!!.extras.getParcelable(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS)

        // Add a callback to monitor changes in the call state
//        call.registerCallback(object : Call.Callback() {
//            override fun onStateChanged(call: Call?, state: Int) {
//                // Handle changes in call state
//                // For example, check if the call is disconnected and perform cleanup
//                if (state == Call.STATE_DISCONNECTED) {
//                    // Perform cleanup, update UI, etc.
//                }
//            }
//        })

        return connection
    }

    override fun onCreateOutgoingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateOutgoingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    override fun onCreateIncomingConnection(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ): Connection {
        return super.onCreateIncomingConnection(connectionManagerPhoneAccount, request)
    }

    override fun onCreateIncomingConnectionFailed(
        connectionManagerPhoneAccount: PhoneAccountHandle?,
        request: ConnectionRequest?
    ) {
        super.onCreateIncomingConnectionFailed(connectionManagerPhoneAccount, request)
    }

    override fun onCreate() {
        super.onCreate()
        Toast.makeText(this, "onCreate: We are here !!!!!!!", Toast.LENGTH_SHORT)
        // Create a unique component name for your ConnectionService
        val componentName = ComponentName(this, MyConnectionService::class.java)

        // Create a PhoneAccount using the ComponentName
        val phoneAccount = PhoneAccount.Builder(
            PhoneAccountHandle(componentName, "Your Connection Service"),
            "Your Connection Service"
        )
            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
            .build()

        // Register the PhoneAccount with TelecomManager
        telecomManager.registerPhoneAccount(phoneAccount)
    }
}

class YourConnection : Connection() {

    init {
        // Perform any initialization for your custom connection
    }

    override fun onShowIncomingCallUi() {
        // Handle UI updates when an incoming call is shown
    }

     fun onCallStateChanged(state: Int) {
        // Handle changes in call state
    }

    override fun onDisconnect() {
        // Handle disconnection
    }

    override fun onAbort() {
        // Handle call abort
    }

    override fun onAnswer() {
        // Handle call answer
    }

    override fun onReject() {
        // Handle call rejection
    }

    override fun onHold() {
        // Handle call hold
    }

    override fun onUnhold() {
        // Handle call unhold
    }

    override fun onStateChanged(state: Int) {
        // Handle changes in the call state
    }

    override fun onPlayDtmfTone(c: Char) {
        // Handle playing DTMF tone
    }

    override fun onStopDtmfTone() {
        // Handle stopping DTMF tone
    }

     fun onDisconnectCauseChanged(cause: Int) {
        // Handle changes in the disconnect cause
    }

     fun onConnectionCapabilitiesChanged(capabilities: Int) {
        // Handle changes in connection capabilities
    }

     fun onConnectionPropertiesChanged(properties: Int) {
        // Handle changes in connection properties
    }

     fun onConferenceableConnectionsChanged(conferenceableConnections: List<Connection>) {
        // Handle changes in conferenceable connections
    }

    override fun onExtrasChanged(extras: Bundle?) {
        // Handle changes in extras
    }

     fun onPostDialWait() {
        // Handle post-dial wait
    }

     fun onPostDialChar(char: Char) {
        // Handle post-dial character
    }

     fun onConnectionEvent(event: String?, extras: Bundle?) {
        // Handle connection events
    }

     fun onRttInitiated() {
        // Handle RTT initiation
    }

     fun onRttInitiationFailed(reason: Int) {
        // Handle RTT initiation failure
    }

     fun onRttSessionRemotelyTerminated() {
        // Handle RTT session termination by the remote party
    }

     fun onRttStopped() {
        // Handle RTT stopped
    }

    // Implement other necessary methods as needed for your use case
}

