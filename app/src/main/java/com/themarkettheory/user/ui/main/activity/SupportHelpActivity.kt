package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import kotlinx.android.synthetic.main.activity_support_help.*
import java.text.SimpleDateFormat
import java.util.*

class SupportHelpActivity : BaseActivity(), View.OnClickListener {
    /*Login Response*/
    private lateinit var newLoginResponse: NewLoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_support_help)
        try {
            /*Login Response*/
            newLoginResponse = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewLoginRes)!!,
                NewLoginResponse::class.java
            )

            /*Set On Click Listener*/
            ivSupportBack.setOnClickListener(this)
            btnSupportCall.setOnClickListener(this)
            btnSupportEmail.setOnClickListener(this)

            init()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            if (!Config.isMyPointClickedFromHome) {
                Config.bottomBarClickedName = Config.menuBottomBarClick
            }
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivSupportBack -> onBackPressed()
                btnSupportCall -> {
                    val intentDialCall = Intent(Intent.ACTION_DIAL)
                    intentDialCall.data = Uri.parse("tel:${newLoginResponse.data!!.admin_mobile}")
                    startActivity(intentDialCall)
                }
                btnSupportEmail -> {
                    if (PubFun.isInternetConnection(this@SupportHelpActivity)) {
                        val intent = Intent(Intent.ACTION_SENDTO)
                        intent.data = Uri.parse("mailto:")
                        intent.putExtra(
                            Intent.EXTRA_EMAIL,
                            arrayOf(newLoginResponse.data!!.admin_email.trim())
                        )
                        intent.putExtra(Intent.EXTRA_SUBJECT, "The market Theory | Support")
                        intent.putExtra(Intent.EXTRA_TEXT, "")
                        startActivity(intent)
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }

                    /*val i = Intent(Intent.ACTION_SEND)
                    i.type = "text/plain"
                    i.putExtra(
                        Intent.EXTRA_EMAIL,
                        arrayOf(newLoginResponse.data!!.admin_email.trim())
                    )
                    i.putExtra(Intent.EXTRA_SUBJECT, "The market Theory | Support")
                    i.putExtra(Intent.EXTRA_TEXT, "")
                    val pm = getPackageManager()
                    val matches = pm.queryIntentActivities(i, 0)
                    var best: ResolveInfo? = null
                    for (info in matches)
                        if (info.activityInfo.packageName.endsWith(".gm") || info.activityInfo.name
                                .lowercase(Locale.getDefault()).contains("gmail")
                        )
                            best = info
                    if (best != null)
                        i.setClassName(best.activityInfo.packageName, best.activityInfo.name)
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."))
                    } catch (ex: android.content.ActivityNotFoundException) {
                        showMsgDialogAndProceed("There are no email clients installed.")
                    }*/
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun init() {
        try {
            /*Check for Admin Start & End Time and set visibility accordingly*/
            if (newLoginResponse.data!!.admin_start_time.isNotEmpty()
                && newLoginResponse.data!!.admin_end_time.isNotEmpty()
            ) {
                val adminStartTime = SimpleDateFormat(Config.requestTimeFormat, Locale.getDefault())
                    .parse(newLoginResponse.data!!.admin_start_time.trim())!!.time
                val adminEndTime = SimpleDateFormat(Config.requestTimeFormat, Locale.getDefault())
                    .parse(newLoginResponse.data!!.admin_end_time.trim())!!.time
                btnSupportCall.visibility = if (adminStartTime < System.currentTimeMillis() ||
                    adminEndTime > System.currentTimeMillis()
                ) View.GONE else View.VISIBLE
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@SupportHelpActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.support_help)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}