package com.karlmusingo.limit_phone_call

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Paint.Align
import android.net.Uri
import android.os.Bundle
import android.provider.CallLog
import android.telecom.PhoneAccount
import android.telecom.PhoneAccountHandle
import android.telecom.TelecomManager
import android.text.Html
import android.view.Gravity
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Clear
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.karlmusingo.limit_phone_call.ui.theme.LimitphonecallTheme
import java.util.Date


class MainActivity : ComponentActivity() {

    private var telecomManager: TelecomManager? = null
    private lateinit var callLogs: ArrayList<Map<String, String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        if(ContextCompat.checkSelfPermission(
//                this, android.Manifest.permission.READ_PHONE_STATE
//            ) == PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_PHONE_STATE), 369)
//        }
//
//        telecomManager = getSystemService(Context.TELECOM_SERVICE) as TelecomManager?
//
//        val componentName = ComponentName(this, MyConnectionService::class.java)
//
//        // Create a PhoneAccount using the ComponentName
//        val phoneAccount = PhoneAccount.Builder(
//            PhoneAccountHandle(componentName, "Your Connection Service"),
//            "Your Connection Service"
//        )
//            .setCapabilities(PhoneAccount.CAPABILITY_CALL_PROVIDER)
//            .build()
//
//        // Register the PhoneAccount with TelecomManager
//        telecomManager?.registerPhoneAccount(phoneAccount)

        if(ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED) {
            println("We are here")
//            ActivityCompat.requ(this, android.Manifest.permission.READ_CALL_LOG)

            requestPermissions(arrayOf(android.Manifest.permission.READ_CALL_LOG), 369)
        }

        callLogs = getCallLogs()

        println(callLogs)

        setContent {
            CallLogHistory()
        }

    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        if(hasFocus) {
            println(hasFocus)
            callLogs = getCallLogs()
        }
    }

    fun getCallLogs(): ArrayList<Map<String, String>> {
        println("Get call logs")
        val calllogsBuffer = ArrayList<String>()

        val callLogsMap = ArrayList<Map<String, String>>()
        calllogsBuffer.clear()

        val managedCursor: Cursor? = this.contentResolver.query(
            CallLog.Calls.CONTENT_URI,
            null, null, null, null
        )
        val number: Int = managedCursor!!.getColumnIndex(CallLog.Calls.NUMBER)
        val type: Int = managedCursor.getColumnIndex(CallLog.Calls.TYPE)
        val date: Int = managedCursor.getColumnIndex(CallLog.Calls.DATE)
        val duration: Int = managedCursor.getColumnIndex(CallLog.Calls.DURATION)

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

    @Composable
    fun CallLogHistory() {
        LazyColumn() {
            items(callLogs.reversed()){ log ->
                LogItem(log)
            }
        }

    }


}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    LimitphonecallTheme {
//        LogItem()
    }
}



@Composable
fun LogItem(log: Map<String, String>) {
    val phoneNumber = log["phoneNumber"]
    val type = log["type"]
    val callDuration = log["callDuration"]
    val callTime = log["callTime"]?.slice(IntRange(0, 9))

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(text = "$phoneNumber")

            Row {
                Text(text = "$callDuration Sec")
                Text(text = " ($callTime)")
            }
        }

        Text(text = "$type")


    }
}

@Composable
fun CalculatorScreen(telecomManager: TelecomManager?, context: Context) {
    var displayValue by remember{mutableStateOf("")} // Stores the current input
    var timeLimit by remember {
        mutableStateOf("10")
    }

    fun showToastMsg(c:Context, msg:String) {
        val toast = Toast.makeText(c, msg, Toast.LENGTH_LONG)

        toast.setGravity(Gravity.CENTER, 0, 0)
        toast.show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Bottom
    ) {
        TextField(
            value = timeLimit,
            onValueChange = { newValue ->
                timeLimit = newValue
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("Provide the time limit (minutes)") },
            label = { Text("Time") },
            singleLine = true // make it single line by default
        )
        // Display area
        Text(
            "$displayValue",
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(16.dp),
            color = Color.Black,
            fontSize = 32.sp,
            textAlign = TextAlign.Right
        )

        val buttonSize = 70.dp
        val fontSize = 30.sp

        Column(modifier = Modifier.fillMaxWidth()) {
            for (i in 0..2) {
                // Number buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Add more button logic for other numbers and functions
                    for (j in 1..3) {
                        val number = j + (3 * i)
                        Button(
                            onClick = {
                                displayValue += number
                            },
                            colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        ) {
                            Text("$number", modifier = Modifier
                                .size(buttonSize)
                                .wrapContentHeight(), textAlign = TextAlign.Center, style = TextStyle(
                                fontSize = fontSize
                            ))
                        }
                    }
                }
                Spacer(Modifier.size(16.dp))
            }
        }

        // Special buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { displayValue += '*' },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("*", modifier = Modifier
                    .size(buttonSize)
                    .wrapContentHeight(), textAlign = TextAlign.Center,  style = TextStyle(
                    fontSize = fontSize,
                ))
            }
            Button(
                onClick = { displayValue += "0" },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("0", modifier = Modifier
                    .size(buttonSize)
                    .wrapContentHeight(), textAlign = TextAlign.Center,  style = TextStyle(
                    fontSize = fontSize,
                ))
            }
            Button(
                onClick = { displayValue += '#' },
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray)
            ) {
                Text("#", modifier = Modifier
                    .size(buttonSize)
                    .wrapContentHeight(), textAlign = TextAlign.Center,  style = TextStyle(
                    fontSize = fontSize,
                ))
            }
        }
        Spacer(Modifier.size(16.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            Box {
                Button(onClick = {
                    println("This is it")
                    val phoneNumber = Uri.fromParts("tel", "123456789", null)

                    // Create an Intent with the ACTION_CALL action
                    val callIntent = Intent(Intent.ACTION_CALL, phoneNumber)
                    println("This is it")
                    Toast.makeText(context, "Button clicked 1", Toast.LENGTH_SHORT)
                    // Use TelecomManager to place the call

                    if (ActivityCompat.checkSelfPermission(
                            context,
                            Manifest.permission.CALL_PHONE
                        ) == PackageManager.PERMISSION_GRANTED
                    ) {
                        println("This is it: per")
//                        Toast.makeText(this, "We are here", Toast.LENGTH_SHORT)
                        Toast.makeText(context, "Button clicked 2", Toast.LENGTH_SHORT)

                        val extras = Bundle()
                        extras.putBoolean(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, true)
//                        extras.putBinder(TelecomManager.EXTRA_OUTGOING_CALL_EXTRAS, )

                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        telecomManager?.placeCall(callIntent.data, extras)
                    } else {
                        println("This is it else ")
                    }

                }, colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray), border = BorderStroke(1.dp, Color.Gray),
                    shape = RoundedCornerShape(100)
                ) {
                    Icon(imageVector =  Icons.Default.Call, contentDescription = "Make a phone call", modifier = Modifier
                        .padding(16.dp)
                        .size(24.dp)
                        .fillMaxSize(0.6F))
                }
//
                if(displayValue.isNotBlank()) {
                    Button(onClick = {
                        displayValue = displayValue.dropLast(1)
                    },
                        colors = ButtonDefaults.buttonColors(backgroundColor = Color.Gray),
                        border = BorderStroke(1.dp, Color.Gray),
                        shape = CutCornerShape(topStartPercent = 50, bottomStartPercent = 50 ),
                        modifier = Modifier.offset(x = 130.dp, y = 10.dp)
                    ) {
                        Icon(imageVector =  Icons.Default.Clear, contentDescription = "Delete")
                    }
                }

            }

        }
    }
}


