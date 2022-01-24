package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import br.com.simplepass.loadingbutton.customViews.CircularProgressButton
import com.airbnb.paris.Paris
import com.chaos.view.PinView
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.*
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.SocialLoginResponse
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.LoginViewModel
import com.themarkettheory.user.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_verify_otp.*


class VerifyOtpActivity : BaseActivity(), View.OnClickListener {
    lateinit var registerViewModel: RegisterViewModel
    var mVerificationId: String? = ""
    var smsCode: String? = ""
    private val firebaseAuthOTPtimeOut: Long = 60
    private var mobileNumber = ""
    private var email = ""
    private var termAndCondition = ""

    //Layout Components
    private lateinit var tvOTPMobileCode: MaterialTextView
    private lateinit var tvResendOtp: MaterialTextView
    private lateinit var tvOtpResendTitle: MaterialTextView
    private lateinit var tvOTPTitle: MaterialTextView
    private lateinit var pinView: PinView
    private lateinit var btnVerify: CircularProgressButton
    val handler = Handler(Looper.getMainLooper())
    private var lastClickTime = 0L
    private lateinit var loginViewModel: LoginViewModel

    /*private var mAuth: FirebaseAuth? = null*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_otp)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        setStatusBarColor(R.color.black)
        /*mAuth = FirebaseAuth.getInstance();*/

        //region Added by Milan Sheth
        tvOTPTitle = findViewById(R.id.tvOTPTitle)
        tvOTPMobileCode = findViewById(R.id.tvOTPMobileCode)
        tvResendOtp = findViewById(R.id.tvResendOtp)
        tvOtpResendTitle = findViewById(R.id.tvOtpResendTitle)
        pinView = findViewById(R.id.pinView)
        btnVerify = findViewById(R.id.btnVerify)

        tvOTPTitle.paint.shader = PubFun.getShader(this@VerifyOtpActivity, tvOTPTitle)

        mobileNumber = prefs.getLoginModel().mobile.toString().trim()
        val strEmail = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbVerifyOTPEmail)
        email = if (strEmail.isNullOrEmpty()) prefs.getLoginModel().email.toString()
            .trim() else strEmail

        val otpMobileNumber = mobileNumber.substring(mobileNumber.length - 2)
        tvOTPMobileCode.text =
            getString(R.string.verify_mobile_for_otp, "XXXXX XXX$otpMobileNumber")

        resendOtpEnableDisable(false)
        otpTimeOutHandler()
        getOTPResponse()
        tvOtpResendTitle.visibility = View.GONE
        pinView.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {
                if (s!!.length == 4) {
                    btnVerify.performClick()
                }
            }
        })


        //endregion

//        startPhoneNumberVerification("+"+prefs.getLoginModel().country_code + prefs.getLoginModel().mobile)
        /*startPhoneNumberVerification("+91"+ prefs.getLoginModel().mobile)*/
//        startPhoneNumberVerification("+918460151124")
        btnVerify.setOnClickListener(this)
        tvResendOtp.setOnClickListener(this)

        // intiating message retriver listener
        /*startSMSRetriever()*/
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@VerifyOtpActivity)) {
                when (myRoomDatabase.daoConfig()
                    .selectConfigTableByField(Config.dbVerifyOTPNavigatesFrom)) {
                    Config.signInActivity -> startActivity(
                        Intent(
                            applicationContext,
                            SigninActivity::class.java
                        )
                    )
                    Config.signUpActivity -> startActivity(
                        Intent(
                            applicationContext,
                            SignupActivity::class.java
                        )
                    )
                }
                Utils.slideExit(this)
                finish()
            } else {
                showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getOTPResponse() {
        registerViewModel.isLoading.observe(this, Observer {
            if (it) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        registerViewModel.resendOTP.observe(this, {
            when (it.status) {
                0, 1 -> showMsgDialogAndProceed(null, it.message, true)
            }
        })
        loginViewModel.isLoadingSocial.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        registerViewModel.responseVerifyOTP.observe(this, Observer {

            btnVerifyEnableDisable(true)
            when (it.status) {
                0 -> showMsgDialogAndProceed(null, it.message, true)
                1 -> {
//                    if (Config.isLoginWithSocialButton) {
//                        val socialLoginResponse = gson.fromJson(
//                            myRoomDatabase.daoConfig()
//                                .selectConfigTableByField(Config.dbSocialLogin),
//                            SocialLoginResponse::class.java
//                        )
//                        loginViewModel.social_login(
//                            Constants.loginViaGoogle,
//                            prefs.getToken(this),
//                            Constants.deviceType,
//                            socialLoginResponse.social_id,
//                            socialLoginResponse.name,
//                            socialLoginResponse.email,
//                            socialLoginResponse.image.toString()
//                        )
//                    } else {
                    checkResponse(it)

                }
            }
        })

        loginViewModel.responseSocialLogin.observe(this, Observer<NewLoginResponse>
        {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it, "", false)
                1 -> {
                    checkResponse(it)
                }
            }
        })
    }

    private fun showMsgDialogAndProceed(res: NewLoginResponse?, msg: String, isMsgShow: Boolean) {
        try {
            val myDialog = DialogToast(this@VerifyOtpActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Verify OTP"
                it.tvMessage.text = if (isMsgShow) msg else res!!.message
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                                if (!isMsgShow) {
                                    if (res != null) {
                                        if (res.data != null) {
                                            if (res.data.emailVerified == 0) {
                                                val bundle = intent.extras
//                                                if(intent.hasExtra("isSocialLogin")){
//                                                    startActivity(
//                                                        Intent(
//                                                            applicationContext,
//                                                            ProfileActivity::class.java
//                                                        )
//                                                    )
//                                                } else {
                                                startActivity(
                                                    Intent(
                                                        applicationContext,
                                                        SigninActivity::class.java
                                                    )
                                                )
                                                // }
                                                Utils.slideExit(this@VerifyOtpActivity)
                                                finish()
                                            }


                                        }
                                    }
                                }
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
                    if (!isMsgShow) {
                        if (res != null) {
                            if (res.data != null) {
                                if (res.data.emailVerified == 0) {
                                    startActivity(
                                        Intent(
                                            applicationContext,
                                            SigninActivity::class.java
                                        )
                                    )
                                    Utils.slideExit(this)
                                    finish()
                                }
                            }
                        }
                    }
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkResponse(res: NewLoginResponse) {

        when (myRoomDatabase.daoConfig()
            .selectConfigTableByField(Config.dbVerifyOTPNavigatesFrom)) {

            Config.editProfileActivity -> {
                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                    insertConfigTable(
                        TableConfig(
                            Config.dbVerifyOTPNavigatesFrom,
                            Config.editProfileActivityVerify
                        )
                    )
                }
                Utils.slideExit(this)
                finish()
                return
        }
    }

    if (res.data != null)
    {
        if (res.data.token.isNotEmpty()) {
            myRoomDatabase.daoConfig().apply {
                deleteConfigTableByField(Config.dbNewLoginRes)
                insertConfigTable(
                    TableConfig(
                        Config.dbNewLoginRes,
                        gson.toJson(res)

                    )
                )
            }
            Log.e("User Response", gson.toJson(res))
            prefs.setAccessToken(this@VerifyOtpActivity, res.data.token)
            prefs.setLoginModel(res.data)
            termAndCondition = res.data.terms_condition

            if (res.data.emailVerified == 0) {
                showMsgDialogAndProceed(res, "", false)
            } else if (res.data.mobileVerified == 0) {
                showMsgDialogAndProceed(null, "Please verify your mobile", true)
            } else if (res.data.emailVerified == 1 && res.data.mobileVerified == 1 && res.data.zip.isEmpty()) {
                startActivity(Intent(this@VerifyOtpActivity, ProfileActivity::class.java).putExtra("terms_condition", termAndCondition))
                Utils.slideEnter(this)
                finish()
            } else if (res.data.emailVerified == 1 && res.data.mobileVerified == 1 && res.data.zip.isNotEmpty()) {
                startActivity(Intent(this@VerifyOtpActivity, CitySelectionActivity::class.java))
                Utils.slideEnter(this)
                finish()
            }
        } else {
            startActivity(Intent(this@VerifyOtpActivity, Log::class.java))
            Utils.slideEnter(this)
            finish()
        }
    } else
    {
        showMsgDialogAndProceed(null, res.message, true)
        startActivity(Intent(this, SignupActivity::class.java))
    }
}

override fun onClick(v: View?) {
    if (SystemClock.elapsedRealtime() - lastClickTime < 3000) return
    lastClickTime = SystemClock.elapsedRealtime()
    when (v) {
        btnVerify -> {
            if (pinView.text.toString().isNotEmpty() && pinView.text.toString().length == 4) {
                if (PubFun.isInternetConnection(this@VerifyOtpActivity)) {
                    btnVerifyEnableDisable(false)
                    verifyOtp()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            } else {
                showMsgDialogAndProceed(null, "Please enter OTP and then try again", true)
            }
        }

        tvResendOtp -> {
            if (PubFun.isInternetConnection(this@VerifyOtpActivity)) {
                resendOtpEnableDisable(false)
                resendOTP()
            } else {
                showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
            }
        }
    }
}

private fun btnVerifyEnableDisable(isEnable: Boolean) {
    btnVerify.isEnabled = isEnable
    val buttonStyle = R.style.button_style
    val buttonStyleDisabled = R.style.button_style_disabled
    Paris.styleBuilder(btnVerify)
        .add(if (isEnable) buttonStyle else buttonStyleDisabled)
        .apply()
}

private fun resendOtpEnableDisable(isEnable: Boolean) {
    tvResendOtp.isEnabled = isEnable
    tvResendOtp.setTextColor(
        if (isEnable)
            ContextCompat.getColor(
                this@VerifyOtpActivity,
                R.color.robins_egg_blue
            ) else
            ContextCompat.getColor(
                this@VerifyOtpActivity,
                R.color.slate_gray
            )
    )
}

private fun verifyOtp() {
    try {
        registerViewModel.verifyOTP(
            mobileNumber,
            pinView.text.toString().trim(),
            Config.countryCode
        )
        /*otpTimeOutHandler()*/
    } catch (e: Exception) {
        e.stackTrace
    }
}

private fun resendOTP() {
    try {
        registerViewModel.resendOTP(
            mobileNumber,
            Config.countryCode
        )
        otpTimeOutHandler()
    } catch (e: Exception) {
        e.stackTrace
    }
}

private fun otpTimeOutHandler() {
    //region ResendOTP timer
    handler.post(object : Runnable {
        var i = Config.otpResendTimer
        override fun run() {
            if (i == 0) {
                tvOtpResendTitle.visibility = View.GONE
                handler.removeCallbacksAndMessages(null)
                resendOtpEnableDisable(true)
            } else {
                tvOtpResendTitle.visibility = View.VISIBLE
                tvOtpResendTitle.text =
                    getString(R.string.otp_can_be_resend_in, "$i seconds")
                i--
                if (Config.otpSmsTextReceiver.isNotEmpty()) {
                    handler.removeCallbacks(this)
                    handler.removeCallbacksAndMessages(null)
                    pinView.setText(Config.otpSmsTextReceiver)
                    btnVerify.performClick()
                }
                handler.postDelayed(this, 1000)
            }
        }
    })
    //endregion
}


private fun startSMSRetriever() {
    try {
        val client = SmsRetriever.getClient(this /* context */)
        if (client != null) {
            val task = client.startSmsRetriever()
            if (task != null) {
                task.addOnSuccessListener { }
                task.addOnFailureListener { // Failed to start retriever, inspect Exception for more details
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}

/*private fun startPhoneNumberVerification(phoneNumber: String) {
    PhoneAuthProvider.getInstance().verifyPhoneNumber(
        phoneNumber, // Phone number to verify
        firebaseAuthOTPtimeOut, // Timeout duration
        TimeUnit.SECONDS, // Unit of timeout
        this, // Activity (for callback binding)
        object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                smsCode = credential.smsCode
                Log.d("", "onVerificationCompleted:$credential")
//                    otpViewModel.set_otp(userId, smsCode)
//                    setOTP()

                //signInWithPhoneAuthCredential(credential)
            }

            override fun onVerificationFailed(e: FirebaseException) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("", "onVerificationFailed", e)

                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                }

            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken
            ) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                mVerificationId = verificationId
                Log.d("", "onCodeSent:$verificationId")

                // Save verification ID and resending token so we can use them later

                // ...
            }
        } // OnVerificationStateChangedCallbacks
    )
    // mVerificationInProgress = true
}*/
}