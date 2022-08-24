package com.purnendu.smsreading.usingBroadCastReceiver

import android.Manifest
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.shouldShowRationale
import com.purnendu.smsreading.ui.theme.SMSReadingTheme

class MainActivity1 : ComponentActivity(), MessageListener {


    private lateinit var context: Context
    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MessageReceiver.bindListener(this)

        setContent {


            val permissionState = rememberMultiplePermissionsState(
                permissions = listOf(
                    Manifest.permission.RECEIVE_SMS,
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

            context = LocalContext.current


            SMSReadingTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {

                    val smsReceivingPermission = permissionState.permissions[0]

                    if (smsReceivingPermission.status.isGranted) {

                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center)
                        {

                            Text(
                                text = "I am listening upcoming messages", fontSize = 30.sp
                            )
                        }

                    } else if (smsReceivingPermission.status.shouldShowRationale) {
                        Toast.makeText(
                            context,
                            "SMS permission is needed to read upcoming SMS",
                            Toast.LENGTH_SHORT
                        ).show()

                    } else if (!smsReceivingPermission.status.isGranted && !smsReceivingPermission.status.shouldShowRationale) {

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

    override fun messageReceived(message: String?) {

        Toast.makeText(context, "New Message Received: $message", Toast.LENGTH_SHORT).show()

    }

}