package com.purnendu.smsreading.usingGoogleSMSRetrieverAPI


import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.purnendu.smsreading.ui.theme.SMSReadingTheme

class MainActivity2 : ComponentActivity() {


    private var message by mutableStateOf("")
    private var hash by mutableStateOf("")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val appSignatureHashHelper = AppSignatureHashHelper(this)
        hash = appSignatureHashHelper.appSignatures[0]

        setContent {
            SMSReadingTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                    {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Text(text = message, fontSize = 30.sp)
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = "Sender hash code should be $hash", fontSize = 15.sp)
                            Button(onClick = {
                                startSmsRetriever()
                            }) {
                                Text(text = "Start listening")

                            }
                        }


                    }


                }
            }


        }


    }

    private fun startSmsRetriever() {
        val client = SmsRetriever.getClient(this)

        val task = client.startSmsRetriever()

        task.addOnSuccessListener { _ ->
            Toast.makeText(this@MainActivity2, "Started listening", Toast.LENGTH_SHORT).show()
            listenSms()
        }

        task.addOnFailureListener { e ->
            message = e.message.toString()
        }

    }

    private fun listenSms() {
        SmsReceiver.bindListener(object : SmsListener {
            override fun onSuccess(code: String) {
                message = code
            }
            override fun onError(errorMessage: String) {
                message = errorMessage
            }
        })
    }


}