package com.purnendu.smsreading.usingGoogleSMSUserConsentAPI

import android.content.*
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.purnendu.smsreading.ui.theme.SMSReadingTheme
import org.jetbrains.annotations.Nullable
import java.util.regex.Matcher
import java.util.regex.Pattern


class MAinActivity3 : ComponentActivity() {


    private var text by mutableStateOf("")
    private var senderPhoneNo by mutableStateOf("")
    private val REQ_USER_CONSENT = 200
    var smsBroadcastReceiver: SmsBroadcastReceiver? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {

            SMSReadingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {

                        TextField(value = senderPhoneNo, onValueChange ={senderPhoneNo=it},
                        placeholder = { Text(text = "Enter sender ph no with proper format like +919614472290")})
                        Button(onClick = {
                            if(senderPhoneNo.isEmpty())
                                return@Button
                            if(!isValidMobileNo(senderPhoneNo))
                                return@Button
                            startSmsUserConsent(senderPhoneNo)
                        }) {
                            Text(text = "Start Listening")
                        }
                        Text(text = text, fontSize = 20.sp)
                    }

                }
            }

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQ_USER_CONSENT) {
            if (resultCode == RESULT_OK && data != null) {
                //That gives all message to us.
                // We need to get the code from inside with regex
                var message = data.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
                Toast.makeText(this@MAinActivity3, message, Toast.LENGTH_LONG).show()
                if (message != null) {
                    text=message
                }
                if (message != null) {
                    getOtpFromMessage(message)
                }
            }
        }
    }

    private fun getOtpFromMessage(message: String) {
        // This will match any 6 digit number in the message
//        val pattern: Pattern = Pattern.compile("(|^)\\d{6}")
//        val matcher: Matcher = pattern.matcher(message)
//        if (matcher.find()) {
//            otpText.setText(matcher.group(0))
//        }
        text=message
    }


    private fun startSmsUserConsent(senderPhNo:String) {
        val client = SmsRetriever.getClient(this@MAinActivity3)
        client.startSmsUserConsent(senderPhNo).addOnSuccessListener {
            Toast.makeText(
                this@MAinActivity3,
                "Started Listening",
                Toast.LENGTH_SHORT
            ).show()
        }.addOnFailureListener {
            Toast.makeText(this@MAinActivity3, "Failed listening,try checking ph no format ", Toast.LENGTH_SHORT).show()
        }
    }

    private fun registerBroadcastReceiver() {
        smsBroadcastReceiver = SmsBroadcastReceiver()
        smsBroadcastReceiver!!.smsBroadcastReceiverListener =
            object : SmsBroadcastReceiver.SmsBroadcastReceiverListener {
                override fun onSuccess(intent: Intent?) {
                    startActivityForResult(intent, REQ_USER_CONSENT)
                }

                override fun onFailure() {
                    Toast.makeText(this@MAinActivity3, "Failed listening,try checking ph no format ", Toast.LENGTH_SHORT).show()
                }
            }
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(smsBroadcastReceiver, intentFilter)
    }

    override fun onStart() {
        super.onStart()
        registerBroadcastReceiver()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(smsBroadcastReceiver)
    }

    fun isValidMobileNo(str: String): Boolean {
//        val ptrn: Pattern = Pattern.compile("(0/91)?[7-9][0-9]{9}")
//        val match: Matcher = ptrn.matcher(str)
//        return match.find() && match.group().equals(str)
        return android.util.Patterns.PHONE.matcher(str).matches()
    }
}