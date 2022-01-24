package com.themarkettheory.user.ui.main.activity

import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.util.Patterns
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.GraphRequest
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.*
import com.themarkettheory.user.model.CountryList
import com.themarkettheory.user.model.SocialLoginResponse
import com.themarkettheory.user.model.SocialModel
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.newmodels.signup.signUpUserData
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.LoginViewModel
import com.themarkettheory.user.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.annotations.NonNull
import io.reactivex.internal.util.HalfSerializer.onComplete
import kotlinx.android.synthetic.main.activity_signin.*
import kotlinx.android.synthetic.main.activity_signup.*
import kotlinx.android.synthetic.main.activity_signup.llFacebook
import kotlinx.android.synthetic.main.activity_signup.llGoogle
import kotlinx.android.synthetic.main.activity_signup.login_button
import kotlinx.android.synthetic.main.button_progress.*
import org.json.JSONException
import java.net.MalformedURLException
import java.net.URL
import java.util.*

@AndroidEntryPoint
class SignupActivity : BaseActivity(), View.OnClickListener,
    GoogleApiClient.OnConnectionFailedListener {
    private lateinit var registerViewModel: RegisterViewModel
    private lateinit var loginViewModel: LoginViewModel
    private var documentPath: String? = ""
    var socialModel = SocialModel()

    /*private lateinit var loginViewModel: LoginViewModel*/
    internal lateinit var callbackManager: CallbackManager
    lateinit var mGoogleSignInClient: GoogleSignInClient
    private var mGoogleApiClient: GoogleApiClient? = null
    private lateinit var auth: FirebaseAuth
    var list = ArrayList<CountryList>()

    //Layout Components
    private lateinit var tvSignUp: MaterialTextView
    private lateinit var tvSigninFromSignUp: MaterialTextView
    private lateinit var edEmailSignUp: TextInputEditText
    private lateinit var edMobileSignup: TextInputEditText
    private lateinit var edPasswordSignUp: TextInputEditText
    private lateinit var lylPasswordWithEyeIcon: ConstraintLayout
    private var isShowPassword = false
    private lateinit var socialLoginResponse: SocialLoginResponse
    private var lastClickTime = 0L
    private var is_edit = "0" //default
    private var login_via = Constants.loginViaNormal //default
    private var social_id = ""
    private var social_profile_pic = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        callbackManager = CallbackManager.Factory.create()
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        loginViewModel = ViewModelProvider(this).get(LoginViewModel::class.java)

        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("", "getInstanceId failed", task.exception)
                return@addOnCompleteListener
            }
            prefs.setToken(this, task.result.trim())
        }
        setStatusBarColor(R.color.black)
        btn.text = getString(R.string.request_otp)

        // region Added by Milan Sheth
        //Sign Up Textview instead of image and setting its gradient color to its text
        tvSignUp = findViewById(R.id.tvSignUp)
        tvSigninFromSignUp = findViewById(R.id.tvSignInFromSignUp)
        edEmailSignUp = findViewById(R.id.edEmailSignUp)
        edMobileSignup = findViewById(R.id.edMobileSignUp)
        edPasswordSignUp = findViewById(R.id.edPasswordSignUp)
        lylPasswordWithEyeIcon = findViewById(R.id.lylPasswordWithEyeIcon)

        tvSignUp.paint.shader = PubFun.getShader(this@SignupActivity, tvSignUp)

        //region Added by Milan Sheth
        var socialLoginResponse: SocialLoginResponse

        val strEmail = myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbVerifyOTPEmail)
        edEmailSignUp.setText(strEmail)

        edEmailSignUp.isEnabled = strEmail.isNullOrEmpty()
        lylPasswordWithEyeIcon.visibility =
            if (Config.isLoginWithSocialButton) View.GONE else View.VISIBLE

        ivHideShowPasswordSignUp.setOnClickListener(this)
        if (Config.isLoginWithSocialButton) {
            val socialLoginResponse = Gson().fromJson(
                intent.getStringExtra(Config.socialLoginResponseBundle),
                SocialLoginResponse::class.java
            )
            if (socialLoginResponse != null) {
                edEmailSignUp.setText(socialLoginResponse.email)
                edEmailSignUp.isEnabled = !socialLoginResponse.email.isNotEmpty()
                Log.e("loginEmail:", socialLoginResponse.email.isNotEmpty().toString())

            }
        }

        edMobileSignup.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        if (myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbSocialLogin) != null) {
            socialLoginResponse = Config.gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbSocialLogin),
                SocialLoginResponse::class.java
            )
            if (socialLoginResponse != null) {
                if (!socialLoginResponse.email.isNullOrEmpty()) {
                    is_edit = "0"
                }
            }
        } else {
            is_edit = "1"
        }

        auth = Firebase.auth
        //endregion

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestIdToken(resources.getString(R.string.googleAccountWebClientID))
            .build()

        mGoogleApiClient = GoogleApiClient.Builder(this)
            .enableAutoManage(this, this)
            .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
            .build()

        val gso1 = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.googleAccountWebClientID))
            .requestEmail()
            .build()

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        getFacebookData()
        /*setCountrySpinner()*/

        getRegisterResponse()
        tvSigninFromSignUp.setOnClickListener(this)
        btn.setOnClickListener(this)
        /*ivProfile.setOnClickListener(this)*/
        llGoogle.setOnClickListener(this)
        llFacebook.setOnClickListener(this)


        if (myRoomDatabase.daoConfig()
                .selectConfigTableByField(Config.dbVerifyOTPNavigatesFrom) == Config.signUpActivity
        ) {
            val signUpUserData = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbSignUpData),
                signUpUserData::class.java
            )
            edEmailSignUp.setText(signUpUserData.signUpEmailId)
            edMobileSignup.setText(signUpUserData.signUpMobile)
            edPasswordSignUp.setText(signUpUserData.signUpPassword)
        }
    }

/*private fun setCountrySpinner() {

    registerViewModel.countries()

    registerViewModel.responseCountries.observe(this, Observer {
        if (it.status!!) {
            list.clear()
            list.addAll(it.data?.list!!)
            val dataAdapter: ArrayAdapter<CountryList> =
                ArrayAdapter<CountryList>(this, R.layout.row_spinner, it.data?.list!!)
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spCountryCode.adapter = dataAdapter
        }
    })

}*/

//    override fun onStart() {
//        super.onStart()
//        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
//            //Firebase.auth.signOut()
//            mGoogleSignInClient.signOut()
//            Log.e("loginUser", mGoogleSignInClient.toString());
////            startActivity(Intent(this, ProfileActivity::class.java))
////            finish()
//        }
//    }


    fun getFacebookData() {
        login_button.setPermissions(Arrays.asList("email", "public_profile"))
        login_button.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                val s = loginResult.recentlyGrantedPermissions

                println("value of permission $s")

                setFacebookData(loginResult)
            }

            override fun onCancel() {
            }

            override fun onError(exception: FacebookException) {
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
                Config.email = email

                is_edit = "0"
                edEmailSignUp.setText(email)
                social_id = id
                social_profile_pic = profile_pic.toString()

                loginViewModel.social_login(
                    Constants.loginViaFacebook,
                    prefs.getToken(this),
                    Constants.deviceType,
                    socialModel.facebook_token,
                    socialModel.fullName,
                    socialModel.email,
                    socialModel.profileImage
                )

                /*loginViewModel.social_login(
                    socialModel.login_via,
                    prefs.getToken(this),
                    Constants.deviceType,
                    socialModel.facebook_token,
                    socialModel.fullName,
                    socialModel.email,
                    socialModel.profileImage
                )*/
                /*startActivity(Intent(this,SignupActivity::class.java)
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


    private fun getRegisterResponse() {
        registerViewModel.isLoadingRegister.observe(this, Observer {
            if (it!!) {
                pb.visibility = View.VISIBLE
                btn.visibility = View.GONE
            } else {
                pb.visibility = View.GONE
                btn.visibility = View.VISIBLE
            }
        })

        /*loginViewModel.isLoadingSocial.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })*/

        /* registerViewModel.responseRegister.observe(this, Observer<LoginResponse> {
             if (it.status!!) {
                 it.data?.let {
                     *//*Utils.navigateScreen(this, it)*//*
                    *//*prefs.setLoginModel(it)
                    prefs.setAccessToken(this,it.token!!)
                    startActivity(Intent(this, ProfileActivity::class.java))
                    Utils.slideEnter(this)*//*
                }
            } else {
                *//*Utils.showSnackbar(this, it.message)*//*
                PubFun.showToast(this, Config.TYPE_ERROR, it.message, Toast.LENGTH_LONG)
            }
        })*/

        //region Added by Milan Sheth
        registerViewModel.responseCheckEmailMobile.observe(this, {
            when (it.status) { // Success
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
        //endregion

        /*loginViewModel.responseSocialLogin.observe(this, Observer<LoginResponseNew> {
            when (it.status) {
                0 -> {
                    it.message.let { msg ->
                        PubFun.showToast(this, Config.TYPE_WARNING, msg, Toast.LENGTH_LONG)
                    }
                }
                1 -> {
                    prefs.setAccessToken(this@SignupActivity, it.data.token)
                    prefs.setLoginModel(it.data)
                    edEmailSignUp.setText(it.data.email.trim())
                }
            }
        })*/
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
                prefs.setAccessToken(this@SignupActivity, res.data.token)
                prefs.setLoginModel(res.data)
                if (res.data.emailVerified == 0) {
                    showMsgDialogAndProceed(res, "", false)
                } else if (res.data.mobileVerified == 0) {
                    showMsgDialogAndProceed(res, "", false)
                } else if (res.data.emailVerified == 1 &&
                    res.data.mobileVerified == 1 &&
                    res.data.zip.isEmpty()
                ) {
                    startActivity(Intent(this@SignupActivity, ProfileActivity::class.java))
                    Utils.slideEnter(this)
                    finish()
                } else if (res.data.emailVerified == 1 &&
                    res.data.mobileVerified == 1 &&
                    res.data.zip.isNotEmpty()
                ) {
                    startActivity(Intent(this@SignupActivity, CitySelectionActivity::class.java))
                    Utils.slideEnter(this)
                    finish()
                }
            }
        } else {
            showMsgDialogAndProceed(res, "", false)
        }
    }


    private fun showMsgDialogAndProceed(res: NewLoginResponse?, msg: String, isMsgShow: Boolean) {
        try {
            val myDialog = DialogToast(this@SignupActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Sign-Up"
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
                                                    val signUpUserData = signUpUserData()
                                                    signUpUserData.apply {
                                                        signUpEmailId =
                                                            edEmailSignUp.text.toString().trim()
                                                        signUpMobile =
                                                            edMobileSignup.text.toString().trim()
                                                        signUpPassword =
                                                            edPasswordSignUp.text.toString().trim()
                                                    }

                                                    myRoomDatabase.daoConfig().apply {
                                                        deleteConfigTableByField(Config.dbSignUpData)
                                                        insertConfigTable(
                                                            TableConfig(
                                                                Config.dbSignUpData,
                                                                gson.toJson(signUpUserData)
                                                            )
                                                        )
                                                        deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                                                        insertConfigTable(
                                                            TableConfig(
                                                                Config.dbVerifyOTPNavigatesFrom,
                                                                Config.signUpActivity
                                                            )
                                                        )
                                                    }
                                                    val intent = Intent(
                                                        applicationContext,
                                                        VerifyOtpActivity::class.java
                                                    )
                                                    val strEmail = myRoomDatabase.daoConfig()
                                                        .selectConfigTableByField(Config.dbVerifyOTPEmail)

                                                    if (!strEmail.isNullOrEmpty())
                                                        intent.putExtra("isSocialLogin", "1")
                                                    startActivity(intent)
                                                    Utils.slideEnter(this@SignupActivity)
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
                    try {
                        myDialog.dismiss()
                        if (!isMsgShow) {
                            if (res != null) {
                                if (res.data != null) {
                                    if (res.data.mobileVerified == 0) {
                                        val signUpUserData = signUpUserData()
                                        signUpUserData.apply {
                                            signUpEmailId = edEmailSignUp.text.toString().trim()
                                            signUpMobile = edMobileSignup.text.toString().trim()
                                            signUpPassword = edPasswordSignUp.text.toString().trim()
                                        }

                                        myRoomDatabase.daoConfig().apply {
                                            deleteConfigTableByField(Config.dbSignUpData)
                                            insertConfigTable(
                                                TableConfig(
                                                    Config.dbSignUpData,
                                                    gson.toJson(signUpUserData)
                                                )
                                            )
                                            deleteConfigTableByField(Config.dbVerifyOTPNavigatesFrom)
                                            insertConfigTable(
                                                TableConfig(
                                                    Config.dbVerifyOTPNavigatesFrom,
                                                    Config.signUpActivity
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
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun signIn() {
//        FirebaseAuth.getInstance().signOut();
//        Firebase.auth.signOut()
//        mGoogleSignInClient.signOut()
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 3000) return
        lastClickTime = SystemClock.elapsedRealtime()
        when (v) {
            tvSigninFromSignUp -> {
                onBackPressed()
            }

            /* ivProfile -> {
                 CropImage.activity()
                     .setAspectRatio(1, 1)
                     .setGuidelines(CropImageView.Guidelines.ON)
                     .start(this)
             }*/

            llFacebook -> {
                if (PubFun.isInternetConnection(this@SignupActivity)) {
                    login_via = Constants.loginViaFacebook
                    Config.isLoginWithSocialName = Constants.loginViaFacebook
                    LoginManager.getInstance().logOut();
                    login_button.performClick()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            llGoogle -> {
                if (PubFun.isInternetConnection(this@SignupActivity)) {
                    login_via = Constants.loginViaGoogle
                    Config.isLoginWithSocialName = Constants.loginViaGoogle
                    LoginManager.getInstance().logOut();
                    signIn()
                } else {
                    showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                }
            }

            btn -> {
                Config.email = edEmailSignUp.text.toString()
                Config.phone = edMobileSignup.text.toString()
                Config.password = edMobileSignup.text.toString()
                if (checkValidation()) {
                    if (PubFun.isInternetConnection(this@SignupActivity)) {
                        checkEmailMobile()
                    } else {
                        showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                    }
                }
            }

            /* {
            var body: MultipartBody.Part? = null
            if (!documentPath.isNullOrEmpty()) {
                body = Utils.createMutilPart(File(documentPath), "profile_pic")
            }

            if (!Utils.isEmptyTextInputEditText(
                    tilEmail,
                    edEmail,
                    "Please enter Email"
                ) //!Utils.isEmptyTextInputEditText(tilFullName, edFullName, "Please enter full name")
                && !Utils.isEmptyTextInputEditText(tilMobile, edMobile, "Please enter Mobile")
                && !Utils.isEmptyTextInputEditText(
                    tilPassword,
                    edPassword,
                    "Please enter Password"
                )
                && !Utils.isEmailCorrect(edEmail)
            ) {
                if(!cb.isChecked){
                    Utils.showToast(this,"Please accept terms & conditions")
                }else {

    //                    }
            }
        }*/

            ivHideShowPasswordSignUp -> hideShowPassword()
        }
    }

    private fun checkValidation(): Boolean {
        //region email
        if (edEmailSignUp.text!!.trim().isEmpty()) {
            showMsgDialogAndProceed(null, "Please enter your email id", true)
            return false
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(edEmailSignUp.text!!).matches()) {
            showMsgDialogAndProceed(null, "Please enter valid email address", true)
            return false
        }
        //endregion

        //region mobile
        if (edMobileSignUp.text!!.trim().isEmpty()) {
            showMsgDialogAndProceed(null, "Please enter your mobile number", true)
            return false
        }

        if (!Patterns.PHONE.matcher(edMobileSignUp.text!!).matches()) {
            showMsgDialogAndProceed(null, "Please enter valid mobile number", true)
            return false
        }

        if (edMobileSignup.text!!.trim().length < 10) {
            showMsgDialogAndProceed(null, "Your mobile number must be 10 characters long", true)
            return false
        }
        //endregion

        //region password
        if (!Config.isLoginWithSocialButton) {
            if (edPasswordSignUp.text!!.trim().isEmpty()) {
                showMsgDialogAndProceed(null, "Please enter your password", true)
                return false
            }

            if (edPasswordSignUp.text!!.length <= 5) {
                showMsgDialogAndProceed(null, "Please enter minimum 6 char password", true)
                return false
            }
        }
        //endregion

        return true
    }

    private fun checkEmailMobile() {
        /*var body: MultipartBody.Part? = null
        if (!documentPath.isNullOrEmpty()) {
            body = Utils.createMutilPart(File(documentPath), "profile_pic")
        }
           registerViewModel.register(
            Utils.createRequestBody(""), //Utils.createRequestBody(edFullName.text.toString())
            Utils.createRequestBody(edEmail.text.toString()),
            Utils.createRequestBody(edMobile.text.toString()),
            Utils.createRequestBody(edPassword.text.toString()),
            Utils.createRequestBody(prefs.getToken(this)),
            Utils.createRequestBody(Constants.deviceType),
            Utils.createRequestBody(list[spCountryCode.selectedItemPosition].id.toString()),
            body
        )*/

        Log.e("email", edMobileSignup.text!!.toString().trim())
        Log.e("countryCode", Config.countryCode.toString())
        Log.e("edEmailSignUp", edEmailSignUp.text!!.toString().trim())
        Log.e("is_edit", is_edit)
        Log.e("login_via", login_via)
        Log.e("social_id", social_id)

        registerViewModel.checkEmailMobile(
            edMobileSignup.text!!.toString().trim(),
            Config.countryCode, //Hardcoded as of now.
            edEmailSignUp.text!!.toString().trim(),
            is_edit,
            login_via,
            if (edPasswordSignUp.text?.length == 0) "xxxxxx" else edPasswordSignUp.text.toString()
                .trim(),
            social_id,
            social_profile_pic

        )
//        Log.e("Email id", edMobileSignup.text!!.toString().trim())
//        Log.e("is_edit", is_edit)
//        Log.e("login_via", login_via)
    }

    //Added by Milan Sheth on 15-Jul-2021 14:50
    private fun hideShowPassword() {
        if (isShowPassword) {
            edPasswordSignUp.transformationMethod =
                PasswordTransformationMethod()
            ivHideShowPasswordSignUp.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_visible_eye
                )
            )
            isShowPassword = false
        } else {
            edPasswordSignUp.transformationMethod = null
            ivHideShowPasswordSignUp.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.ic_invisible_eye
                )
            )
            isShowPassword = true
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, SigninActivity::class.java))
        Utils.slideExit(this)
        finish()
    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        callbackManager.onActivityResult(requestCode, resultCode, data)
        /*val result = CropImage.getActivityResult(data)
        if(result != null) {
            documentPath = result?.getUriFilePath(this)
            ivProfile.setImageURI(result.uriContent)
        }*/
        if (requestCode == 100) {

            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                System.out.println("firebaseAuthWithGoogle:" + account.id)

                socialLoginResponse = SocialLoginResponse()
                socialLoginResponse.login_via = Constants.loginViaGoogle
                socialLoginResponse.device_token = prefs.getToken(this)
                socialLoginResponse.device_type = Constants.deviceType
                socialLoginResponse.social_id = account.id!!
                socialLoginResponse.name = account.displayName!!
                socialLoginResponse.email = account.email!!
                socialLoginResponse.image = account.photoUrl!!.toString()
                Config.socialId = account.id!!
                Config.email = account.email!!

                is_edit = "0"
                edEmailSignUp.setText(account.email!!)
                social_id = account.id!!
                social_profile_pic = account.photoUrl!!.toString()

                loginViewModel.social_login(
                    Constants.loginViaGoogle,
                    prefs.getToken(this),
                    Constants.deviceType,
                    account.id,
                    account.displayName,
                    account.email,
                    account.photoUrl.toString()
                )

//                firebaseAuthWithGoogle(account.idToken!!)
                /*loginViewModel.social_login(
                    Constants.loginViaGoogle,
                    prefs.getToken(this),
                    Constants.deviceType,
                    account.id,
                    account.displayName,
                    account.email,
                    account.photoUrl.toString()
                )*/
            } catch (e: ApiException) {
                System.out.println("Google sign in failed: " + e)

//                Log.w(TAG, "Google sign in failed", e)
                // ...
            }
        }
    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }
}