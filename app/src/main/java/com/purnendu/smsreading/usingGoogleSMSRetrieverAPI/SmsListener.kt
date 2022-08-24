package com.purnendu.smsreading.usingGoogleSMSRetrieverAPI

interface SmsListener {
    fun onSuccess(code: String)
    fun onError(errorMessage:String)
}