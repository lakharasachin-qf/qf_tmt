package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.activity_recover_password.*
import kotlinx.android.synthetic.main.button_progress.*
import kotlinx.android.synthetic.main.toolbar.*

class RecoverPasswordActivity : BaseActivity(), View.OnClickListener {
    lateinit var generalViewModel: GeneralViewModel

    //LayoutComponents
    private lateinit var tvRecoverPasswordTitle: MaterialTextView
    private lateinit var ivBackRecoverPassword: ShapeableImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recover_password)

        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)

        /*tvTitle.setText(getString(R.string.recover_password))*/
        btn.setText(getString(R.string.submit))

        ivBackRecoverPassword = findViewById(R.id.ivBackRecoverPassword)
        ivBackRecoverPassword.setOnClickListener { onBackPressed() }
        tvRecoverPasswordTitle = findViewById(R.id.tvRecoverPasswordTitle)
        tvRecoverPasswordTitle.paint.shader =
            PubFun.getShader(this@RecoverPasswordActivity, tvRecoverPasswordTitle)

        getResponse()

        /*ivBack.setOnClickListener(this)*/
        btn.setOnClickListener(this)
    }

    private fun getResponse() {
        generalViewModel.isLoading.observe(this, Observer {
            if (it) {
                btn.visibility = View.GONE
                pb.visibility = View.VISIBLE
            } else {
                btn.visibility = View.VISIBLE
                pb.visibility = View.GONE
            }
        })

        generalViewModel.responseForgotPassword.observe(this, Observer {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it.message!!, false)
                1 -> {
                    showMsgDialogAndProceed(it.message!!, true)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String, isStatusTrue: Boolean) {
        try {
            val myDialog = DialogToast(this@RecoverPasswordActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Recover Password"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable{
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                                if (isStatusTrue) {
                                    onBackPressed()
                                }
                            } else {
                                i--
                                postDelayed(this, 500)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                    if (isStatusTrue) {
                        onBackPressed()
                    }
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()

            btn -> {
                if (edEmail.text!!.isBlank()) {
                    showMsgDialogAndProceed("Email address can't be blank", false)
                } else if (!Patterns.EMAIL_ADDRESS.matcher(edEmail.text!!).matches()) {
                    showMsgDialogAndProceed("Please enter valid email address", false)
                } else {
                    if (PubFun.isInternetConnection(this@RecoverPasswordActivity)) {
                        generalViewModel.forget_password(edEmail.text.toString())
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet, false)
                    }
                }
            }
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@RecoverPasswordActivity)) {
                super.onBackPressed()
                startActivity(Intent(this@RecoverPasswordActivity, SigninActivity::class.java))
                Utils.slideExit(this)
                finish()
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet, false)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}