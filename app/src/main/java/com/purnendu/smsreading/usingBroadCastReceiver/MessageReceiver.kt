package com.purnendu.smsreading.usingBroadCastReceiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage


class MessageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        val data = intent.extras
//        val a=data?.getByteArray("pdus")
//        SmsMessage.createFromPdu(a)


        val pdus = data!!["pdus"] as Array<Any>?
        for (i in pdus!!.indices) {
            val smsMessage: SmsMessage = SmsMessage.createFromPdu(pdus[i] as ByteArray)
            val message = "Sender : " + smsMessage.displayOriginatingAddress
                .toString() + "Email From: " + smsMessage.emailFrom
                .toString() + "Emal Body: " + smsMessage.emailBody
                .toString() + "Display message body: " + smsMessage.displayMessageBody
                .toString() + "Time in millisecond: " + smsMessage.timestampMillis
                .toString() + "Message: " + smsMessage.messageBody
            mListener?.messageReceived(message)
        }
    }

    companion object {
        private var mListener: MessageListener? = null
        fun bindListener(listener: MessageListener?) {
            mListener = listener
        }
    }
}