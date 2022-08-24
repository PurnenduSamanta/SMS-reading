package com.purnendu.smsreading.usingBroadCastReceiver

interface MessageListener {

    fun messageReceived(message: String?)
}