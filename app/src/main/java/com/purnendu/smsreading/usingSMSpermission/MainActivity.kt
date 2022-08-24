package com.purnendu.smsreading.usingSMSpermission

import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.purnendu.smsreading.ui.theme.SMSReadingTheme
import java.util.*

class MainActivity : ComponentActivity() {

    private val smsList = mutableListOf<SMS>()

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {

            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.READ_SMS,
                )
            )

            val lifeCycleOwner = LocalLifecycleOwner.current
            DisposableEffect(key1 = lifeCycleOwner)
            {
                val observer = LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_START) {
                        permissionState.launchMultiplePermissionRequest()
                    }
                }
                lifeCycleOwner.lifecycle.addObserver(observer)

                onDispose {
                    lifeCycleOwner.lifecycle.removeObserver(observer)
                }
            }

            val context = LocalContext.current

            SMSReadingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val smsReadingPermission = permissionState.permissions[0]

                    if (smsReadingPermission.status.isGranted) {

                        val cursor: Cursor? = contentResolver.query(
                            Uri.parse("content://sms/inbox"),
                            null,
                            null,
                            null,
                            null
                        )
                        if (cursor != null) {
                            if (cursor.moveToFirst()) { // must check the result to prevent exception
                                var messageId=0L
                                var threadId=0L
                                var address=""
                                var contactId=0L
                                var contactIdString=""
                                var timestamp=0L
                                var body=""

                                do {
                                    for (idx in 0 until cursor.columnCount) {
                                        messageId = cursor.getLong(0)
                                         threadId = cursor.getLong(1)
                                         address = cursor.getString(2)
                                         contactId = cursor.getLong(3)
                                         contactIdString = contactId.toString()
                                         timestamp = cursor.getLong(4)
                                         body = cursor.getString(12)

                                        Log.d("Reading", cursor.getColumnName(idx) + ":" + cursor.getString(idx))
                                    }
                                    smsList.add(
                                        SMS(
                                            messageId,
                                            threadId,
                                            address,
                                            contactIdString,
                                            Date(timestamp),
                                            body
                                        )
                                    )

                                    // use msgData
                                } while (cursor.moveToNext())
                            } else {
                                cursor.close()
                            }
                            cursor.close()

                            LazyColumn()
                            {
                                items(smsList)
                                { item ->
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(5.dp)
                                    ) {

                                        Text(text = "message_id: ${item.messageId}, thread_id:${item.threadId}, address:${item.address}, contact_Id:${item.contactId_String}, timeStamp:${item.timeStamp}, body:${item.body} ")
                                    }


                                }
                            }
                        }




                    } else if (smsReadingPermission.status.shouldShowRationale) {
                        Toast.makeText(
                            context,
                            "SMS permission is needed to read SMS",
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (!smsReadingPermission.status.isGranted && !smsReadingPermission.status.shouldShowRationale) {
                        Toast.makeText(
                            context,
                            "SMS permission permanently denied ,you can enable it by going to app setting",
                            Toast.LENGTH_SHORT
                        ).show()

                    }


                }
            }
        }
    }
}

data class SMS(
    val messageId: Long,
    val threadId: Long,
    val address: String,
    val contactId_String: String,
    val timeStamp: Date,
    val body: String
)
