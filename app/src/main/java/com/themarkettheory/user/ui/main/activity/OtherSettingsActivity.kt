package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.gson.Gson
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.activity_other_settings.*
import kotlinx.android.synthetic.main.activity_setting_new.*
import kotlinx.android.synthetic.main.toolbar.*

class OtherSettingsActivity : BaseActivity(), View.OnClickListener {
    lateinit var generalViewModel: GeneralViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting_new)

        gson = Gson()
        try {
            generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
            tvTitle.setText(getString(R.string.settings))
            /*if (PubFun.isInternetConnection(this)) {
                generalViewModel.profile()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }*/
            getResponse()

            tvChangePasswordText.setOnClickListener(this)
            tvdeleteAccountText.setOnClickListener(this)
            ivBack.setOnClickListener(this)
            ivChangePasswordArrow.setOnClickListener(this)
            ivDeleteAccountArrow.setOnClickListener(this)
            setNotificationButtonStatus()

// temporarly commenting this dont remove it can be use in future
            /*   switchRemindMe.setOnCheckedChangeListener { buttonView, isChecked ->
                   generalViewModel.change_notification_status(switchNotification.isChecked.toInt(),switchRemindMe.isChecked.toInt())
               }*/

            /*   swSwitchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                   generalViewModel.change_notification_status(
                       swSwitchNotification.isChecked.toInt(),
                      0// switchRemindMe.isChecked.toInt()
                   )
               }*/
            swSwitchNotification.setOnClickListener(View.OnClickListener {
                if (PubFun.isInternetConnection(this)) {
                    generalViewModel.update_notification_status(
                        swSwitchNotification.isChecked.toInt()
                    )
                } else {
                    swSwitchNotification.isChecked = swSwitchNotification.isChecked.xor(true)
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            })

            /*swSwitchNotification.setOnCheckedChangeListener { buttonView, isChecked ->
                generalViewModel.update_notification_status(
                    swSwitchNotification.isChecked.toInt()
                )
            }*/
            /* tvChangePasswordText.setOnClickListener(this)
             tvdeleteAccountText.setOnClickListener(this)*/
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setNotificationButtonStatus() {
        try {
            swSwitchNotification.isChecked = Config.isnotification == 1
//            swSwitchNotification.isChecked = Config.isnotification == 1
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponse() {
        try {
            generalViewModel.isLoading.observe(this, Observer {
                if (it!!) {
                    Utils.showProgress(this)
                } else {
                    Utils.hideProgress(this)
                }
            })

            generalViewModel.responseDeleteAccount.observe(this, Observer {
                if (it.status == 1) {
                    //{"message":"Message not found with key: ACCOUNT_DELETED","status":1}
                  //  Log.e("Deleted Data", gson.toJson(it.response))
                    prefs.clear()
                    startActivity(Intent(this, SigninActivity::class.java))
                    finishAffinity()
                } else {
                    Utils.showSnackbar(this, it.message)
                }
            })

            /*generalViewModel.responseGetProfile.observe(this, Observer {
                if (it.status!! == 1) {
                    swSwitchNotification.isChecked = it.data?.notification_status == 1
                    // switchRemindMe.isChecked = it.data?.remind_me == 1
                }
            })*/

            generalViewModel.responseChangeNotificationStatus.observe(this, Observer {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> swSwitchNotification.isChecked = !swSwitchNotification.isChecked.xor(true)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun Boolean.toInt() = if (this) 1 else 0

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
                ivChangePasswordArrow, tvChangePasswordText -> {
                    if (PubFun.isInternetConnection(this@OtherSettingsActivity)) {
                        startActivity(Intent(this, ChangePasswordActivity::class.java))
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }

                ivDeleteAccountArrow, tvdeleteAccountText -> {
                    if (PubFun.isInternetConnection(this)) {
                        Utils.showDialog(
                            this,
                            "Alert",
                            "Are you sure you want to delete account?"
                        ) {
                            generalViewModel.deleteAccount()
                        }
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }

                }
                ivBack -> onBackPressed()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@OtherSettingsActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Other Settings"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                            } else {
                                i--
                                postDelayed(this, 1000)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}