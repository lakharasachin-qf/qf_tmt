package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.activity_change_password.*
import kotlinx.android.synthetic.main.button_progress.*
import kotlinx.android.synthetic.main.toolbar.*

class ChangePasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var generalViewModel: GeneralViewModel
    private var hideShowPassword = false
    private var hideShowNewPassword = false
    private var hideShowConfirmPassword = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
        tvTitle.setText(getString(R.string.change_password))
        btn.setText(getString(R.string.submit))

        getResponse()
        ivBack.setOnClickListener(this)
        btn.setOnClickListener(this)

        ivHideShowPassword.setOnClickListener(this)
        ivHideShowNewPassword.setOnClickListener(this)
        ivHideShowConfirmPassword.setOnClickListener(this)
    }

    private fun getResponse() {
        generalViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                btn.visibility = View.GONE
                pb.visibility = View.VISIBLE
            } else {
                btn.visibility = View.VISIBLE
                pb.visibility = View.GONE
            }
        })

        generalViewModel.responseChangePassword.observe(this, Observer {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> showMsgDialogAndProceed("Password changed successfully")
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@ChangePasswordActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Change Password"
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
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@ChangePasswordActivity)) {
                super.onBackPressed()
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

            btn -> {
                if (isValid()) {
                    if (PubFun.isInternetConnection(this@ChangePasswordActivity)) {
                        generalViewModel.change_password(
                            edNewPassword.text.toString(),
                            edPassword.text.toString()
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }

            ivHideShowPassword -> {
                PubFun.hideShowPassword(
                    this@ChangePasswordActivity,
                    hideShowPassword,
                    edPassword,
                    ivHideShowPassword
                )
                hideShowPassword = !hideShowPassword
            }
            ivHideShowNewPassword -> {
                PubFun.hideShowPassword(
                    this@ChangePasswordActivity,
                    hideShowNewPassword,
                    edNewPassword,
                    ivHideShowNewPassword
                )
                hideShowNewPassword = !hideShowNewPassword
            }
            ivHideShowConfirmPassword -> {
                PubFun.hideShowPassword(
                    this@ChangePasswordActivity,
                    hideShowConfirmPassword,
                    edConfirmPassword,
                    ivHideShowConfirmPassword
                )
                hideShowConfirmPassword = !hideShowConfirmPassword
            }
        }
    }

    private fun isValid(): Boolean {
        var isValid: Boolean = true
        if (edPassword.text.toString().isBlank()) {
            showMsgDialogAndProceed("Password can not be blank")
            isValid = false
        } else if (edPassword.text.toString().length < 6) {
            showMsgDialogAndProceed("Password should be of minimum 6 character")
            isValid = false
            return isValid
        } else if (edNewPassword.text.toString().isBlank()) {
            showMsgDialogAndProceed("New password can not be blank")
            isValid = false
            return isValid
        } else if (edNewPassword.text.toString().length < 6) {
            showMsgDialogAndProceed("New password should be of minimum 6 character ")
            isValid = false
            return isValid
        } else if (edConfirmPassword.text.toString().isBlank()) {
            showMsgDialogAndProceed("Confirm password can not be blank")
            isValid = false
            return isValid
        } else if (edConfirmPassword.text.toString().length < 6) {
            showMsgDialogAndProceed("Confirm password should be of minimum 6 character  ")
            isValid = false
            return isValid
        } else if (edConfirmPassword.text.toString() != edNewPassword.text.toString()) {
            showMsgDialogAndProceed("New password and confirm password should be same")
            isValid = false
            return isValid
        } else {
            isValid = true
        }
        return isValid
    }
}