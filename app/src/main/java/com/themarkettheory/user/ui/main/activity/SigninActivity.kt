package com.themarkettheory.user.ui.main.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.provider.Settings
import android.text.SpannableString
import android.text.method.PasswordTransformationMethod
import android.text.style.UnderlineSpan
import android.util.Log
import android.util.Patterns
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.*
import com.themarkettheory.user.model.SocialLoginResponse
import com.themarkettheory.user.model.SocialModel
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.button_progress.*
import org.json.JSONException
import java.net.MalformedURLException
import java.net.URL
import java.util.*


@AndroidEntryPoint
class SigninActivity : BaseActivity(), View.OnClickListener {
    private lateinit var loginViewModel: LoginViewModel
    var deviceID = ""
    var socialModel = SocialModel()
    internal lateinit var callbackManager: CallbackManager

    //Layout Components
    private lateinit var tvSignIn: MaterialTextView
    private lateinit var edEmail: TextInputEditText
    private lateinit var edPassword: TextInputEditText
    private var isShowPassword = false
    private lateinit var socialLoginResponse: SocialLoginResponse
    private var lastClickTime = 0L

    private lateinit var auth: FirebaseAuth
    lateinit var mGoogleSignInClient: GoogleSignInClient

    private var onActivityResultCode = 0
    private val GOOGLE_SIGN_IN = 100

    private val RC_SIGN_IN = 123;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signin)

        callbackManager = CallbackManager.Factory.create()
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        edEmail = findViewById(R.id.edEmail)
        edPassword = findViewById(R.id.edPassword)


        //Sign In Textview instead of image and setting its gradient color to its text
        tvSignIn = findViewById(R.id.tvSignin)
        tvSignIn.paint.shader = PubFun.getShader(this@SigninActivity, tvSignIn)

        //Underline the textview
        val spanContent = SpannableString(getString(R.string.recover_my_password))
        spanContent.setSpan(UnderlineSpan(), 0, spanContent.length, 0)
        tvRecoverMyPassword.text = spanContent

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.e("", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }

            var token = task.result.trim().toString()
            Log.e("getInstanceId failed", "getInstanceId  "+task.result.trim())
            prefs.setToken(this, task.result.trim())

         //   prefs.setToken(this, "test_android")

        }

        deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        /*Utils.setGradient(this, tvSignin, getString(R.string.sign_in))*/
        btn.text = getString(R.string.sign_in)
        setStatusBarColor(R.color.black)

        getLoginResponse()
        getFacebookData()

        llFacebook.setOnClickListener(this)
        llGoogle.setOnClickListener(this)
        tvSignup.setOnClickListener(this)
        tvRecoverMyPassword.setOnClickListener(this)
        ivHideShowPassword.setOnClickListener(this)
        btn.setOnClickListener(this)
        PubFun.hideKeyboard(this@SigninActivity)

        myRoomDatabase.daoConfig().apply {
            deleteConfigTableByField(Config.dbIsDashboard)
            insertConfigTable(
                TableConfig(
                    Config.dbIsDashboard,
                    "false"
                )
            )
        }


        // prefs.setToken(this, Firebase.getInstance().getToken()!!)

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.googleAccountWebClientID))
            .requestEmail()
            .build()

        myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbSocialLogin)
        myRoomDatabase.daoConfig().deleteConfigTableByField(Config.email)
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions)
        if (mGoogleSignInClient != null) {
            myRoomDatabase.daoConfig().deleteConfigTableByField(Config.email)
        }

//        if (mGoogleSignInClient != null)
//            mGoogleSignInClient.signOut()

        auth = Firebase.auth
//        if (auth != null)
//            auth.signOut()

        tvSmsAppCode.text =
            AppSignatureHelper(this@SigninActivity).appSignatures.toString().replace("[", "")
                .replace("]", "")
    }

    private val onActivityResultSignIn: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback {
            when (onActivityResultCode) {
                GOOGLE_SIGN_IN -> {

                    if (it.resultCode == Activity.RESULT_OK) {
                        try {
                            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
                            // Google Sign In was successful, authenticate with Firebase
                            val account = task.getResult(ApiException::class.java)!!
                         //   Log.e("GMAIL - ", gson.toJson(account))
                            System.out.println("firebaseAuthWithGoogle:" + account.id)


                            socialLoginResponse = SocialLoginResponse()
                            socialLoginResponse.login_via = Constants.loginViaGoogle
                            socialLoginResponse.device_token = prefs.getToken(this)
                            socialLoginResponse.device_type = Constants.deviceType
                            socialLoginResponse.social_id = account.id!!
                            socialLoginResponse.name = account.displayName!!
                            socialLoginResponse.email = account.email!!
                            socialLoginResponse.image = account.photoUrl.toString()
                            Config.socialId = account.id!!
                            myRoomDatabase.daoConfig()?.apply {
                                deleteConfigTableByField(Config.dbSocialLogin)
                                insertConfigTable(
                                    TableConfig(
                                        Config.dbSocialLogin,
                                        gson.toJson(socialLoginResponse)
                                    )
                                )

                                deleteConfigTableByField(Config.dbVerifyOTPEmail)
                                insertConfigTable(
                                    TableConfig(
                                        Config.dbVerifyOTPEmail,
                                        account.email!!
                                    )
                                )
                            }

                            loginViewModel.social_login(
                                Constants.loginViaGoogle,
                                prefs.getToken(this),
                                Constants.deviceType,
                                account.id,
                                account.displayName,
                                account.email,
                                account.photoUrl.toString()
                            )
                            mGoogleSignInClient.signOut()

                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    } else {
                        Log.e("data", "data not load")
                    }
                }
            }
        }
    )

    private fun signIn() {


//        Firebase.auth.signOut()
//        mGoogleSignInClient.signOut()
        val signInIntent = mGoogleSignInClient.signInIntent
        onActivityResultCode = GOOGLE_SIGN_IN
        onActivityResultSignIn.launch(signInIntent)

    }

    private fun firebaseAuthWithGoogle(idToken: String) {

        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    //                    Log.d(TAG, "signInWithCredential:success")
                    val user = auth.currentUser
                    updateUI(user)
                } else {
                    // If sign in fails, display a message to the user.
                    // ...
                    val v = auth.currentUser
                    Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_LONG).show()
                    //                    Snackbar.make(view, "Authentication Failed.", Snackbar.LENGTH_SHORT).show()
                    updateUI(null)
                }

            }
    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            val name = user.displayName
            val email = user.email
            val photoUrl = user.photoUrl
            val mobile = user.phoneNumber

            val emailVerified = user.isEmailVerified
            val uid = user.uid

            System.out.println(mobile)

            socialModel.fullName = name
            socialModel.profileImage = photoUrl.toString()
            socialModel.google_token = uid
            socialModel.email = email
            Toast.makeText(this, "Authentication successful.", Toast.LENGTH_LONG).show()

            loginViewModel.social_login(
                Constants.loginViaGoogle,
                prefs.getToken(this),
                Constants.deviceType,
                user.uid,
                name,
                email,
                photoUrl.toString()
            )

        } else {
            Firebase.auth.signOut()
        }

    }

    fun getFacebookData() {
        login_button.setPermissions(listOf("email", "public_profile"))
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val s = loginResult.recentlyGrantedPermissions
                println("value of permission $s")
                setFacebookData(loginResult)
            }

            override fun onCancel() {

                // App code
                var a = ""
            }

            override fun onError(exception: FacebookException) {
                // App code
                var a = ""
            }
        })

    }

    private fun setFacebookData(loginResult: LoginResult) {

        val request = GraphRequest.newMeRequest(
            loginResult.accessToken
        ) { `object`, response ->
            try {
                val id = `object`!!.getString("id")
                val first_name =
                    `object`.getString("first_name") + " " + `object`.getString("last_name")
                val last_name = `object`.getString("last_name")

                var profile_pic: URL? = null
                val image = "https://graph.facebook.com/$id/picture?type=large"
                try {
                    profile_pic = URL("https://graph.facebook.com/$id/picture?type=large")

                } catch (e: MalformedURLException) {
                    e.printStackTrace()
                }

                var email = ""
                if (`object`.has("email")) {
                    email = `object`.getString("email")
                }

                socialModel.email = email
                socialModel.facebook_token = id
                socialModel.fullName = first_name
                socialModel.login_via = Constants.loginViaFacebook
                socialModel.profileImage = profile_pic.toString()

                socialLoginResponse = SocialLoginResponse()
                socialLoginResponse.login_via = socialModel.login_via!!
                socialLoginResponse.device_token = prefs.getToken(this)
                socialLoginResponse.device_type = Constants.deviceType
                socialLoginResponse.social_id = socialModel.facebook_token!!
                socialLoginResponse.name = socialModel.fullName!!
                socialLoginResponse.email = socialModel.email!!
                socialLoginResponse.image = socialModel.profileImage!!
                Config.socialId = id

                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbSocialLogin)
                    insertConfigTable(
                        TableConfig(
                            Config.dbSocialLogin,
                            gson.toJson(socialLoginResponse)
                        )
                    )
                }

                myRoomDatabase.daoConfig().apply {
                    deleteConfigTableByField(Config.dbVerifyOTPEmail)
                    insertConfigTable(
                        TableConfig(
                            Config.dbVerifyOTPEmail,
                            email
                        )
                    )
                }

                loginViewModel.social_login(
                    socialModel.login_via,
                    prefs.getToken(this),
                    Constants.deviceType,
                    socialModel.facebook_token,
                    socialModel.fullName,
                    socialModel.email,
                    socialModel.profileImage
                )
                /*     startActivity(Intent(this,SignupActivity::class.java)
                         .putExtra("email",email)
                         .putExtra("fullName",first_name )
                         .putExtra("profile_pic",image)
                         .putExtra("socialID",id)
                         .putExtra("loginVia","facebook")
                     )*/

            } catch (e: JSONException) {
                e.printStackTrace()
            }
        }
        val parameters = Bundle()
        parameters.putString(
            "fields",
            "id,first_name,last_name,email"
        )
        request.parameters = parameters
        request.executeAsync()
    }

    private fun getLoginResponse() {
        loginViewModel.isLoading.observe(this, Observer {
            if (it) {
                btn.visibility = View.GONE
                pb.visibility = View.VISIBLE
            } else {
                btn.visibility = View.VISIBLE
                pb.visibility = View.GONE
            }
        })

        loginViewModel.isLoadingSocial.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        loginViewModel.responseLogin.observe(this, Observer<NewLoginResponse> {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it, "", false)
                1 -> {
                    checkResponse(it)
                }
            }
        })

        loginViewModel.responseSocialLogin.observe(this, Observer<NewLoginResponse> {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it, "", false)
                1 -> {
                    checkResponse(it)
                }
            }
        })
    }


    private fun checkResponse(res: NewLoginResponse) {
        if (res.data != null) {
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
                prefs.setAccessToken(this@SigninActivity, res.data.token)
                prefs.setLoginModel(res.data)
                if (res.data.emailVerified == 0) {
                    showMsgDialogAndProceed(res, "", false)
                } else if (res.data.mobileVerified == 0) {
                    showMsgDialogAndProceed(res, "", false)
                } else if (res.data.emailVerified == 1 &&
                    res.data.mobileVerified == 1 &&
                    res.data.zip.isEmpty()
                ) {
                    showMsgDialogAndProceed(res, "", false)
                } else if (res.data.emailVerified == 1 &&
                    res.data.mobileVerified == 1 &&
                    res.data.zip.isNotEmpty()
                ) {
                    showMsgDialogAndProceed(res, "", false)
                }
            } else {
                startActivity(Intent(this@SigninActivity, SignupActivity::class.java))
                Utils.slideEnter(this)
                finish()
            }
        } else {
            showMsgDialogAndProceed(res, "", false)
        }
    }

    private fun showMsgDialogAndProceed(res: NewLoginResponse?, msg: String, isMsgShow: Boolean) {
        try {
            val myDialog = DialogToast(this@SigninActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Sign-In"
                it.tvMessage.text = if (isMsgShow) msg else res!!.message
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            try {
                                if (i == 0) {
                                    myDialog.dismiss()
                                    if (!isMsgShow) {
                                        if (res != null) {
                                            if (res.data != null) {
                                                if (res.data.mobileVerified == 0) {
                                                    try {
                                                        myRoomDatabase.daoConfig().apply {
                                                            deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                                                            insertConfigTable(
                                                                TableConfig(
                                                                    Config.dbVerifyOTPNavigatesFrom,
                                                                    Config.signInActivity
                                                                )
                                                            )
                                                        }
                                                        startActivity(
                                                            Intent(
                                                                applicationContext,
                                                                VerifyOtpActivity::class.java
                                                            )
                                                        )
                                                        Utils.slideEnter(this@SigninActivity)
                                                        finish()
                                                    } catch (e: Exception) {
                                                        e.printStackTrace()
                                                    }
                                                } else if (res.data.emailVerified == 1 &&
                                                    res.data.mobileVerified == 1 &&
                                                    res.data.zip.isNotEmpty()
                                                ) {
                                                    startActivity(
                                                        Intent(
                                                            this@SigninActivity,
                                                            CitySelectionActivity::class.java
                                                        )
                                                    )
                                                    Utils.slideEnter(this@SigninActivity)
                                                    finish()
                                                } else if (res.data.emailVerified == 1 &&
                                                    res.data.mobileVerified == 1 &&
                                                    res.data.zip.isEmpty()
                                                ) {
                                                    startActivity(
                                                        Intent(
                                                            this@SigninActivity,
                                                            ProfileActivity::class.java
                                                        )
                                                    )
                                                    Utils.slideEnter(this@SigninActivity)
                                                    finish()
                                                }
                                            }
                                        }
                                    }
                                } else {
                                    i--
                                    postDelayed(this, 1000)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
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
                                if (res.data.mobileVerified == 0) {
                                    try {
                                        myRoomDatabase.daoConfig().apply {
                                            deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                                            insertConfigTable(
                                                TableConfig(
                                                    Config.dbVerifyOTPNavigatesFrom,
                                                    Config.signInActivity
                                                )
                                            )
                                        }
                                        startActivity(
                                            Intent(
                                                applicationContext,
                                                VerifyOtpActivity::class.java
                                            )
                                        )
                                        Utils.slideEnter(this)
                                        finish()
                                    } catch (e: Exception) {
                                        e.printStackTrace()
                                    }
                                } else if (res.data.emailVerified == 1 &&
                                    res.data.mobileVerified == 1 &&
                                    res.data.zip.isNotEmpty()
                                ) {
                                    startActivity(
                                        Intent(
                                            this@SigninActivity,
                                            CitySelectionActivity::class.java
                                        )
                                    )
                                    Utils.slideEnter(this)
                                    finish()
                                } else if (res.data.emailVerified == 1 &&
                                    res.data.mobileVerified == 1 &&
                                    res.data.zip.isEmpty()
                                ) {
                                    startActivity(
                                        Intent(
                                            this@SigninActivity,
                                            ProfileActivity::class.java
                                        )
                                    )
                                    Utils.slideEnter(this)
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

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 3000) return
        lastClickTime = SystemClock.elapsedRealtime()
        when (v) {
            tvSignup -> {
                Firebase.auth.signOut()
                mGoogleSignInClient.signOut()
                myRoomDatabase.daoConfig().deleteConfigTableByField(Config.email)

                if (PubFun.isInternetConnection(this@SigninActivity)) {
                    Config.isLoginWithSocialButton = false
                    Config.isLoginWithSocialName = Constants.loginViaNormal
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbSocialLogin)
                    myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbVerifyOTPEmail)
                    Firebase.auth.signOut()
                    mGoogleSignInClient.signOut()

                    startActivity(Intent(this, SignupActivity::class.java))
                    Utils.slideEnter(this)
                    finish()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            llGoogle -> {
                if (PubFun.isInternetConnection(this@SigninActivity)) {
                    Config.isLoginWithSocialButton = true
                    Config.isLoginWithSocialName = Constants.loginViaGoogle
                    signIn()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            tvRecoverMyPassword -> {
                startActivity(Intent(this, RecoverPasswordActivity::class.java))
                Utils.slideEnter(this)
                finish()
            }

            llFacebook -> {
                if (PubFun.isInternetConnection(this@SigninActivity)) {
                    Config.isLoginWithSocialButton = true
                    Config.isLoginWithSocialName = Constants.loginViaFacebook
                    //LoginManager.getInstance().logOut();
                    login_button.performClick()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            btn -> if (checkValidation()) {
                if (PubFun.isInternetConnection(this@SigninActivity)) {
                    performLogin()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            ivHideShowPassword -> hideShowPassword()
        }
    }

    //Added by Milan Sheth on 12-Jul-2021 16:50
    private fun hideShowPassword() {
        if (isShowPassword) {
            edPassword.transformationMethod =
                PasswordTransformationMethod()
            ivHideShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visible_eye
                )
            )
            isShowPassword = false
        } else {
            edPassword.transformationMethod = null
            ivHideShowPassword.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_invisible_eye
                )
            )
            isShowPassword = true
        }
    }

    //Added by Milan Sheth on 12-Jul-2021 16:00
    private fun checkValidation(): Boolean {
        if (edEmail.text!!.trim().isEmpty()) {
            showMsgDialogAndProceed(null, "Please enter mobile number or email id", true)
            return false
        }

        val number = edEmail.text!!.toString().toLongOrNull()
        if (number == null && !Patterns.EMAIL_ADDRESS.matcher(edEmail.text!!).matches()) {
            showMsgDialogAndProceed(null, "Please enter valid email address", true)
            return false
        } else if (number != null && !Patterns.PHONE.matcher(edEmail.text!!).matches()) {
            showMsgDialogAndProceed(null, "Please enter valid mobile number", true)
            return false
        } else if (number != null && (edEmail.text!!.length < 10 || edEmail.text!!.length > 10)) {
            showMsgDialogAndProceed(
                null,
                "Your mobile number cannot be less or more than 10 digits",
                true
            )
            return false
        }

        if (edPassword.text!!.trim().isEmpty()) {
            showMsgDialogAndProceed(null, "Please enter password", true)
            return false
        }

        if (edPassword.text!!.length <= 5) {
            showMsgDialogAndProceed(null, "Please enter minimum 6 char password", true)
            return false
        }
        return true
    }

    //Added by Milan Sheth on 12-Jul-2021 16:06
    private fun performLogin() {
        Config.isLoginWithSocialButton = false
        loginViewModel.login(
            edEmail.text.toString(),
            edPassword.text.toString(),
            Constants.loginViaNormal,
            Constants.deviceType,
            prefs.getToken(this)
        )
        updateUI(null)
    }


    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (currentFocus != null) {
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(currentFocus!!.windowToken, 0)
        }
        return super.dispatchTouchEvent(ev)
    }

}