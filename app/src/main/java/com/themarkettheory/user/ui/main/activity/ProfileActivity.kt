package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.*
import com.themarkettheory.user.model.CountryList
import com.themarkettheory.user.model.Data
import com.themarkettheory.user.model.SocialLoginResponse
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.ProfileViewModel
import com.themarkettheory.user.viewmodel.RegisterViewModel
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.button_progress.*
import okhttp3.MultipartBody
import java.io.File
import java.util.*

class ProfileActivity : BaseActivity(), View.OnClickListener {
    lateinit var profileViewModel: ProfileViewModel
    private lateinit var registerViewModel: RegisterViewModel
    private var documentPath: String? = ""
    private var phone: String? = ""
    private var countryId: String? = ""


    private val socialLoginResponse: String? = ""
    var model: Data? = Data()
    var gender: String? = "0"
    var socialId = ""

    //Layout Components
    private lateinit var tvProfileLogo: MaterialTextView
    private lateinit var tvCheckBoxText: MaterialTextView
    private var lastClickTime = 0L

    var list = ArrayList<CountryList>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        tvProfileLogo = findViewById(R.id.tvProfileLogo)
        tvProfileLogo.paint.shader = PubFun.getShader(this, tvProfileLogo)
        tvCheckBoxText = findViewById(R.id.tvCheckBoxText)

        profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        registerViewModel = ViewModelProvider(this).get(RegisterViewModel::class.java)
        setStatusBarColor(R.color.black)
        btn.text = getString(R.string.update)

        getProfileResponse()

        if (Config.isLoginWithSocialButton) {
            val socialLoginResponse = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbSocialLogin),
                SocialLoginResponse::class.java
            )
            Glide.with(this).load(socialLoginResponse.image)
                .error(R.drawable.ic_camera)
                .into(ivProfile)
            edFullName.setText(socialLoginResponse.name)
        }

        /*setCountrySpinner()*/

        /*if(!prefs.getLoginModel().facebookToken.isNullOrEmpty() || !prefs.getLoginModel().instagramToken.isNullOrEmpty()
            || !prefs.getLoginModel().appleToken.isNullOrEmpty() || !prefs.getLoginModel().googleToken.isNullOrEmpty()){
            llPhone.visibility = View.VISIBLE
        }else{
            llPhone.visibility = View.GONE
            phone = null
            countryId = null
        }*/

        btn.setOnClickListener(this)
        llMale.setOnClickListener(this)
        llFemale.setOnClickListener(this)
        ivProfile.setOnClickListener(this)
        edDob.setOnClickListener(this)
        tvCheckBoxText.setOnClickListener(this)
    }

    private fun getProfileResponse() {
//        profileViewModel.profile()

        profileViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                pb.visibility = View.VISIBLE
                btn.visibility = View.GONE
            } else {
                pb.visibility = View.GONE
                btn.visibility = View.VISIBLE
            }
        })

        Log.e("user Name:", gson.toJson(model?.name));
        profileViewModel.responseGetProfile.observe(this, Observer {
            if (it.status!! == 1) {
                model = it.data
                edFullName.setText(it.data?.name)
                edMobile.setText(it.data?.mobile)
                edZip.setText(it.data?.zip)
                countryId = it.data?.country_id.toString()
                Glide.with(this).load(it.data?.profilePic)
                    .error(R.drawable.ic_camera)
                    .into(ivProfile)
                updateSpinnerPosition()
            }
        })

        profileViewModel.responseUpdateProfile.observe(this, Observer {
            when (it.status) {
                0 -> showMsgDialogAndProceed(null, it.message, true)
                1 -> showMsgDialogAndProceed(it, "", false)
            }
        })
    }

    override fun onClick(v: View?) {
        if (SystemClock.elapsedRealtime() - lastClickTime < 2000) return
        lastClickTime = SystemClock.elapsedRealtime()
        when (v) {
            btn -> {
                tilFullName.error = ""
                tilDob.error = ""
                tilZip.error = ""

                if (isValid()) {
                    if (PubFun.isInternetConnection(this@ProfileActivity)) {
                        updateProfile()
                    } else {
                        showMsgDialogAndProceed(null, Config.msgToastForInternet, true)
                    }
                }
            }

            tvCheckBoxText -> showMsgDialogAndProceed(
                null,
                "Terms & Condition will be display here",
                true
            )

            edDob -> {
                Utils.setDatePickerDialog(this, edDob) {

                }
            }

            llMale -> {
                gender = "1"
                llMale.background = ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg)
                llFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_disable_bg)
            }

            llFemale -> {
                gender = "2"
                llFemale.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg)
                llMale.background = ContextCompat.getDrawable(this, R.drawable.ic_gender_disable_bg)
            }

            ivProfile -> {
                CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@ProfileActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.edit_profile)
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

    private fun showMsgDialogAndProceed(res: NewLoginResponse?, msg: String, isMsgShow: Boolean) {
        try {
            val myDialog = DialogToast(this@ProfileActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Profile"
                it.tvMessage.text = if (isMsgShow) msg else res!!.message.trim()
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                                if (res?.data != null) {
                                    prefs.setLoginModel(res.data)
                                    if (res.data.zip.isNotEmpty()) {
                                        startActivity(
                                            Intent(
                                                this@ProfileActivity,
                                                CitySelectionActivity::class.java
                                            )
                                        )
                                        Utils.slideEnter(this@ProfileActivity)
                                        finish()
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
                    try {
                        myDialog.dismiss()
                        if (res!!.data != null) {
                            prefs.setLoginModel(res.data!!)
                            if (res.data!!.zip.isNotEmpty()) {
                                startActivity(Intent(this, CitySelectionActivity::class.java))
                                Utils.slideEnter(this)
                                finish()
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

    private fun isValid(): Boolean {
        when {
            edFullName.text.toString().isBlank() -> {
                showMsgDialogAndProceed(null, "Please enter full name", true)
                return false
            }
            edZip.text.toString().isBlank() -> {
                showMsgDialogAndProceed(null, "Please enter zip code", true)
                return false
            }
            edZip.text.toString().length < 6 -> {
                showMsgDialogAndProceed(null, "Please enter valid zip code", true)
                return false
            }
            !cbTermsNConditions.isChecked -> {
                showMsgDialogAndProceed(null, "Please accept terms & conditions", true)
                return false
            }
            else -> return true
        }
    }

    private fun updateProfile() {
        if (Config.phone.isEmpty()) Config.phone =
            prefs.getLoginModel().mobile.toString().trim()
        if (Config.email.isEmpty()) Config.email = prefs.getLoginModel().email.toString().trim()

        var body: MultipartBody.Part? = null
        if (!documentPath.isNullOrEmpty()) {
            body = Utils.createMultiPart(File(documentPath), "profile_pic")
        }
        phone = edMobile.text.toString()
        countryId = Config.countryCode

        val TAG = ""
        val email = Utils.createRequestBody(Config.email)
        Log.d(TAG, "updateProfile: Config.email = " + Config.email)
        val mobile = Utils.createRequestBody(Config.phone)
        Log.d(TAG, "updateProfile: Config.phone = " + Config.phone)
        val country_code = Utils.createRequestBody(Config.countryCode)
        Log.d(TAG, "updateProfile: Config.countryCode = " + Config.countryCode)
        val name = Utils.createRequestBody(edFullName.text.toString())
        Log.d(TAG, "updateProfile: edFullName.text.toString() = " + edFullName.text.toString())
        val zip = Utils.createRequestBody(edZip.text.toString())
        Log.d(TAG, "updateProfile: edZip.text.toString() = " + edZip.text.toString())
        val device_token = Utils.createRequestBody(Prefs(this).getToken(this))
        Log.d(TAG, "updateProfile: Prefs(this).getToken(this) = " + Prefs(this).getToken(this))
        val device_type = Utils.createRequestBody(Constants.deviceType)
        Log.d(TAG, "updateProfile: Constants.deviceType = " + Constants.deviceType)
        val login_via = Utils.createRequestBody(Config.isLoginWithSocialName)
        Log.d(
            TAG,
            "updateProfile: Config.isLoginWithSocialName = " + Config.isLoginWithSocialName
        )
        val dob = Utils.createRequestBody(
            Utils.formatDate(
                edDob.text.toString(),
                "dd MMM yyyy",
                "yyyy-MM-dd"
            )
        )
        Log.d(TAG, "updateProfile: dob = " + dob)
        val sendGender = Utils.createRequestBody(gender)

        Log.d(TAG, "updateProfile: gender == )  = " + gender)

        val password = Utils.createRequestBody(Config.password)
        Log.d(TAG, "updateProfile: Config.password = " + Config.password)
        val social_id = Utils.createRequestBody(Config.socialId)
        Log.d(TAG, "updateProfile: socialId = " + Config.socialId)
        if (PubFun.isInternetConnection(this)) {
            profileViewModel.update_profile(
                email,
                name,
                mobile,
                country_code,
                sendGender,
                dob,
                zip,
                body
            )
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }

        /*profileViewModel.register(
            email,
            mobile,
            country_code,
            name,
            zip,
            device_token,
            device_type,
            login_via,
            dob,
            sendGender,
            password,
            social_id,
            body
        )*/
    }

    override fun onStart() {
        super.onStart()
        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            val socialLoginResponse = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbSocialLogin),
                SocialLoginResponse::class.java
            )
            Glide.with(this).load(socialLoginResponse.image)
                .error(R.drawable.ic_camera)
                .into(ivProfile)
            edFullName.setText(socialLoginResponse.name)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = CropImage.getActivityResult(data)
        if (data != null) {
            documentPath = result?.getUriFilePath(this)
            ivProfile.setImageURI(result?.uriContent)
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
                updateSpinnerPosition()
            }
        })

    }*/

    private fun updateSpinnerPosition() {
        list.mapIndexed { index, countryList ->
            if (countryList.id.toString() == countryId) {
                spCountryCode.setSelection(index)
            }
        }
    }

}