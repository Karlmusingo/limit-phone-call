package com.karlmusingo.limit_phone_call

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.Toast

// Listen for call states
class PhoneCallStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

//        telephonyManager.getCall
//        if(intent.action == Intent.ACTION_CALL) {
//            println("onReceive: ACTION_CALL received")
//        }
//        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK) {
//            showToastMsg(context, "Phone call is Started....")
//        } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_IDLE) {
//            showToastMsg(context, "Phone call is Ended....")
//        } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
//            showToastMsg(context, "Phone call is Ringing....")
//        }
    }

    private fun   showToastMsg(c:Context, msg:String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT)

        toast.show()
    }
}