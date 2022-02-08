package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Telephony
import android.view.View
import com.facebook.CallbackManager
import com.facebook.share.model.ShareLinkContent
import com.facebook.share.widget.ShareDialog
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.dialog.dialogshare.DialogShare
import kotlinx.android.synthetic.main.activity_invite_and_earn.tvPromoCode
import kotlinx.android.synthetic.main.activity_invite_and_earn_new.*
import kotlinx.android.synthetic.main.toolbar.*


class InviteAndEarnActivity : BaseActivity(), View.OnClickListener {
    var shareContent = ""

    //facebook
    private lateinit var shareDialog: ShareDialog
    private lateinit var callbackManager: CallbackManager
    private lateinit var dialogShare: DialogShare
    private var facebook: Int = 0
    private var whatsApp: Int = 0
    private var message: Int = 0
    private var email: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_invite_and_earn_new)
            tvTitle.text = getString(R.string.refer_and_earn)
            tvPromoCode.text = prefs.getLoginModel().referralCode
            shareContent =
                "Use my referral code ${
                    tvPromoCode.text.toString().trim()
                }.\nDownload the app: ${
                    Uri.parse("market://details?id=$packageName")
                }\n\n\nSent from my Android"
            /*+ Uri.parse("market://details?id=$packageName")*/
            ivBack.setOnClickListener(this)

            ivInviteAndEarnFacebook.setOnClickListener(this)
            tvInviteAndEarnFacebookText.setOnClickListener(this)

            ivInviteAndEarnMessenger.setOnClickListener(this)
            tvInviteAndEarnMessengerText.setOnClickListener(this)

            ivInviteAndEarnWhatsapp.setOnClickListener(this)
            tvInviteAndEarnWhatsappText.setOnClickListener(this)

            ivInviteAndEarnMore.setOnClickListener(this)
            tvInviteAndEarnMoreText.setOnClickListener(this)
            btnInviteAndEarnFriends.setOnClickListener(this)
            ivInviteAndEarnFacebook.performClick()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
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
                ivBack -> onBackPressed()
                btnInviteAndEarnFriends -> {
                    /* val share = Intent(Intent.ACTION_SEND)
                     share.type = "text/plain"
                     share.putExtra(
                         Intent.EXTRA_TEXT,
                         shareContent
                     )
                     startActivity(Intent.createChooser(share, "Share"))*/
                    if (facebook == 1) {
                        if (PubFun.isInternetConnection(this@InviteAndEarnActivity)) {
                            shareOnFacebook()
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    }
                    if (whatsApp == 1) {
                        if (PubFun.isInternetConnection(this@InviteAndEarnActivity)) {
                            shareOnWhatsapp()
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    }
                    if (message == 1) {
                        if (PubFun.isInternetConnection(this@InviteAndEarnActivity)) {
                            shareOnMessage()
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }

                    }
                    if (email == 1) {
                        if (PubFun.isInternetConnection(this@InviteAndEarnActivity)) {
                            shareOnEmail()
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    }
                }

                ivInviteAndEarnFacebook, tvInviteAndEarnFacebookText -> {
                    facebook = 1
                    whatsApp = 0
                    message = 0
                    email = 0
                    ivInviteAndEarnFacebook.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_background))
                    ivInviteAndEarnWhatsapp.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp_blank))
                    ivInviteAndEarnMessenger.setImageDrawable(resources.getDrawable(R.drawable.text_msg))
                    ivInviteAndEarnMore.setImageDrawable(resources.getDrawable(R.drawable.ic_email_blank))
                }

                ivInviteAndEarnWhatsapp, tvInviteAndEarnWhatsappText -> {
                    facebook = 0
                    whatsApp = 1
                    message = 0
                    email = 0
                    ivInviteAndEarnWhatsapp.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp_filled_background))
                    ivInviteAndEarnMessenger.setImageDrawable(resources.getDrawable(R.drawable.text_msg))
                    ivInviteAndEarnFacebook.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_blank))
                    ivInviteAndEarnMore.setImageDrawable(resources.getDrawable(R.drawable.ic_email_blank))

                }

                ivInviteAndEarnMessenger, tvInviteAndEarnMessengerText -> {
                    //   shareOnMessage()
                    facebook = 0
                    whatsApp = 0
                    message = 1
                    email = 0
                    ivInviteAndEarnMessenger.setImageDrawable(resources.getDrawable(R.drawable.ic_message_filled_background))
                    ivInviteAndEarnWhatsapp.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp_blank))
                    ivInviteAndEarnFacebook.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_blank))
                    ivInviteAndEarnMore.setImageDrawable(resources.getDrawable(R.drawable.ic_email_blank))
                }

                ivInviteAndEarnMore, tvInviteAndEarnMessengerText -> { // this means email
                    //   shareOnMessage()
                    facebook = 0
                    whatsApp = 0
                    message = 0
                    email = 1
                    ivInviteAndEarnMore.setImageDrawable(resources.getDrawable(R.drawable.ic_email_filled_background))
                    ivInviteAndEarnMessenger.setImageDrawable(resources.getDrawable(R.drawable.text_msg))
                    ivInviteAndEarnWhatsapp.setImageDrawable(resources.getDrawable(R.drawable.ic_whatsapp_blank))
                    ivInviteAndEarnFacebook.setImageDrawable(resources.getDrawable(R.drawable.ic_facebook_blank))
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareOnFacebook() {
        try {
            callbackManager = CallbackManager.Factory.create();
            shareDialog = ShareDialog(this);

            if (ShareDialog.canShow(ShareLinkContent::class.java)) {
                val linkContent = ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("http://developers.facebook.com/android"))
                    .setQuote(shareContent)
                    .build()
                shareDialog.show(linkContent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareOnWhatsapp() {
        try {
            val sendIntent = Intent()
            sendIntent.setPackage("com.whatsapp")
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent)
            sendIntent.type = "text/html"
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun shareOnMessage() {
        try {
            /*val uri = Uri.parse("smsto:+91")
            val smsIntent = Intent(Intent.ACTION_SENDTO,uri)
            smsIntent.putExtra("sms_body", shareContent.trim())
            startActivity(smsIntent)*/

            val defaultSmsPackageName =
                Telephony.Sms.getDefaultSmsPackage(this)
            val sendIntent = Intent(Intent.ACTION_SEND)
            sendIntent.type = "text/plain"
            sendIntent.putExtra(Intent.EXTRA_TEXT, shareContent.trim())
            if (defaultSmsPackageName != null) {
                sendIntent.setPackage(defaultSmsPackageName)
            }
            startActivity(sendIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun shareOnEmail() {
        try {
            /* dialogShare = DialogShare(this@InviteAndEarnActivity)
             val sendIntent = Intent()
             sendIntent.action = Intent.ACTION_SEND
             sendIntent.putExtra(
                 Intent.EXTRA_TEXT,
                 Html.fromHtml(StringBuilder().append(shareContent).toString())
             )
             sendIntent.type = "text/html"
             sendIntent.setType("message/rfc822")
             startActivity(Intent.createChooser(sendIntent, "Email:"))
             dialogShare.dismiss()*/
            val emailIntent = Intent(
                Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "", null
                )
            )
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Share your refer code")
            emailIntent.putExtra(
                Intent.EXTRA_TEXT,
                "User my referral code ${prefs.getLoginModel().referralCode} \n" +
                        "Download the app: ${Config.playStoreUrl}"
            )
            this.startActivity(Intent.createChooser(emailIntent, null))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@InviteAndEarnActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.edit_profile)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 500)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}