package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.MenuViewModel
import kotlinx.android.synthetic.main.activity_business_profile.*
import kotlinx.android.synthetic.main.toolbar.*

class BusinessProfileActivity : BaseActivity(), View.OnClickListener {
    lateinit var menuViewModel: MenuViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_business_profile)

        menuViewModel = ViewModelProvider(this).get(MenuViewModel::class.java)
        try {
            tvTitle.text = getString(R.string.for_business_profile)

            getResponse()
            edFullName.setText(prefs.getLoginModel().name)
            edMobile.setText(prefs.getLoginModel().mobile)
            edEmail.setText(prefs.getLoginModel().email)

            edFullName.filters = PubFun.filterEmojisFromEditText()
            edEmail.filters = PubFun.filterEmojisFromEditText()
            edDetails.filters = PubFun.filterEmojisFromEditText()

            ivBack.setOnClickListener(this)
            btnSubmit.setOnClickListener(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getResponse() {
        try {
            menuViewModel.isLoading.observe(this, Observer {
                if (it!!) {
                    Utils.showProgress(this)
                } else {
                    Utils.hideProgress(this)
                }
            })

            menuViewModel.responseSwitchToBusiness.observe(this, Observer {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message!!.trim())
                    1 -> onBackPressed()
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@BusinessProfileActivity)) {
                if (!Config.isMyPointClickedFromHome) {
                    Config.bottomBarClickedName = Config.menuBottomBarClick
                }
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btnSubmit -> {
                try {
                    if (checkValidatation()) {
                        if (PubFun.isInternetConnection(this@BusinessProfileActivity)) {
                            menuViewModel.switch_to_business(
                                edFullName.text.toString(),
                                edEmail.text.toString(),
                                edMobile.text.toString(),
                                edDetails.text.toString()
                            )
                        } else {
                            showMsgDialogAndProceed(Config.msgToastForInternet)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkValidatation(): Boolean {
        try {
            var isValid = true

            if (edFullName.text!!.isEmpty() || (edFullName.length() < 3)) {
                showMsgDialogAndProceed("Please enter full name")
                isValid = false
            } else if (edEmail.text!!.isEmpty()) {
                showMsgDialogAndProceed("Please enter email id")
                isValid = false
            } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.text!!).matches()) {
                showMsgDialogAndProceed("Please enter a valid email id")
                isValid = false
            } else if (edMobile.text!!.isEmpty()) {
                showMsgDialogAndProceed("Please enter mobile number")
                isValid = false
            } else if (edMobile.length() < 10) {
                showMsgDialogAndProceed("Please enter a valid mobile number")
                isValid = false
            }
            return isValid
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@BusinessProfileActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Business Profile"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
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