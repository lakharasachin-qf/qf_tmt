package com.themarkettheory.user.helper

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.google.android.gms.auth.api.phone.SmsRetriever
import android.os.Bundle
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import java.lang.Exception

/**
 * Created by MOR_MILAN on 23-Oct-15.
 */
class SmsOtpReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        try {
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent.action) {
                val extras = intent.extras
                val status = extras!![SmsRetriever.EXTRA_STATUS] as Status?
                when (status!!.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Get SMS message contents
                        var mySMS = extras[SmsRetriever.EXTRA_SMS_MESSAGE] as String
                        // initial 4 digit will be OTP
                        Config.otpSmsTextReceiver = mySMS.substring(0,3)
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        // handle timeout

                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}