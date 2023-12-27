package com.karlmusingo.limit_phone_call

import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.provider.CallLog
import android.telecom.TelecomManager
import android.telephony.TelephonyManager
import android.widget.Toast
import java.lang.Long
import java.util.*
import kotlin.Int
import kotlin.String
import kotlin.to

class PhoneCallStateReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val telephonyManager = context.getSystemService(Context.TELEPHONY_SERVICE) as TelephonyManager
        val telecomManager = context.getSystemService(Context.TELECOM_SERVICE) as TelecomManager

        if(intent.action == Intent.ACTION_CALL) {
            println("onReceive: ACTION_CALL received")
        }
        if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_OFFHOOK) {
            showToastMsg(context, "Phone call is Started....")
        } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_IDLE) {
            val logs = getCallLogs(context)
            println(logs)
            if(logs.size > 0) {
                val lastCall = getCallLogs(context).last()
                println(lastCall)
                val lastCallDuration = lastCall["callDuration"]
                showToastMsg(context, "Phone call is Ended: $lastCallDuration....")
            } else {
                showToastMsg(context, "Phone call is Ended no log item....")
            }
        } else if(intent.getStringExtra(TelephonyManager.EXTRA_STATE) == TelephonyManager.EXTRA_STATE_RINGING) {
            showToastMsg(context, "Phone call is Ringing....")
        }
    }

    fun getCallLogs(context: Context): ArrayList<Map<String, String>> {
        println("Get call logs")
        val flag = 1
        val calllogsBuffer = ArrayList<String>()

        val callLogsMap = ArrayList<Map<String, String>>()
        calllogsBuffer.clear()

        val managedCursor: Cursor? = context.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )
        val number: Int = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)

        managedCursor.moveToFirst()

        while (managedCursor.moveToNext()) {
            val phNumber: String = managedCursor.getString(number)
            val callType: String = managedCursor.getString(type)
            val callDate: String = managedCursor.getString(date)
            val callDayTime = Date(java.lang.Long.valueOf(callDate))
            val callDuration: String = managedCursor.getString(duration)
            var dir: String = "null"
            val dircode = callType.toInt()
            when (dircode) {
                CallLog.Calls.OUTGOING_TYPE -> dir = "OUTGOING"
                CallLog.Calls.INCOMING_TYPE -> dir = "INCOMING"
                CallLog.Calls.MISSED_TYPE -> dir = "MISSED"
            }

            val theMap = mapOf(
                "phoneNumber" to phNumber,
                "type" to dir,
                "callDuration" to callDuration,
                "callTime" to callDayTime.toString()
            )

            callLogsMap.add(theMap)
        }
        managedCursor.close()

        return callLogsMap
    }


    private fun   showToastMsg(c:Context, msg:String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_SHORT)

        toast.show()
    }
}