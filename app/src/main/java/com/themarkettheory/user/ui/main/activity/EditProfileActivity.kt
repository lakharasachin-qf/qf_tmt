package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.util.Patterns
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageView
import com.google.android.material.textfield.TextInputEditText
import com.google.gson.GsonBuilder
import com.squareup.picasso.Callback
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.model.CountryList
import com.themarkettheory.user.network.ApiService
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.ProfileViewModel
import kotlinx.android.synthetic.main.activity_edit_profile.*
import kotlinx.android.synthetic.main.activity_edit_profile.spCountryCode
import kotlinx.android.synthetic.main.activity_edit_profile_new.*
import kotlinx.android.synthetic.main.activity_edit_profile_new.llFemalEdit
import kotlinx.android.synthetic.main.activity_edit_profile_new.llMaleEdit
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.button_progress.*
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.toolbar.*
import okhttp3.MultipartBody
import java.io.File


class EditProfileActivity : BaseActivity(), View.OnClickListener {
    lateinit var profileViewModel: ProfileViewModel
    private var documentPath: String? = ""
    var gender: String? = null
    var pincode: String? = ""
    var strUser: String? = ""
    lateinit var dob: String
    var list = ArrayList<CountryList>()
    lateinit var edFullName: TextInputEditText
    lateinit var edMobileNo: TextInputEditText
    lateinit var edEmail: TextInputEditText
    lateinit var edDob: TextInputEditText
    lateinit var edZip: TextInputEditText
    private lateinit var loginRes: NewLoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            setContentView(R.layout.activity_edit_profile_new)
            profileViewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
            tvTitle.text = getString(R.string.edit_profile)
            btn.setText(getString(R.string.submit))
            strUser = intent.getStringExtra("userName")
            pincode = intent.getStringExtra("zip")
            dob = intent.getStringExtra("dob").toString()

            ivBack.setOnClickListener(this)
            ivEditProfileImage.setOnClickListener(this)
            btn.setOnClickListener(this)
            edEditProfileDob.setOnClickListener(this)
            llMaleEdit.setOnClickListener(this)
            llFemalEdit.setOnClickListener(this)
            initializeVariables()
            setCountrySpinner()

//            getProfileResponse()
            setProfileInfo()


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun initializeVariables() {
        try {
            edFullName = findViewById(R.id.edEditProfileFullName)
            edMobileNo = findViewById(R.id.edEditProfileMobileNo)
            edEmail = findViewById(R.id.edEditProfileEmail)
            edDob = findViewById(R.id.edEditProfileDob)
            edZip = findViewById(R.id.edEditProfileZipEdit)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setProfileInfo() {
        try {
            gson = GsonBuilder().serializeNulls().create()
            myRoomDatabase = MyRoomDatabase.getDB(this)!!

            loginRes = gson.fromJson(
                myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewLoginRes),
                NewLoginResponse::class.java
            )
            // setting datag

            Log.e("user", "zipCode:" + loginRes.data!!.zip.toString())
            Log.e("user", "Birthdate:" + loginRes.data!!.dob.toString())
            Log.e("user", "Data" + loginRes.data!!)

            edEditProfileEmail.setText(loginRes.data!!.email.trim())
            edEditProfileMobileNo.setText(loginRes.data!!.mobile.trim())


            if (strUser != null)
                edEditProfileFullName.setText(strUser)

            edEditProfileZipEdit.setText(pincode)
            //edEditProfileZipEdit.setText(loginRes.data!!.zip.trim())

//            if (loginRes.data!!.dob != null) {
//                edEditProfileDob.setText(
//                    PubFun.parseDate(
//                        loginRes.data!!.dob.trim(),
//                        Config.requestDateFormat,
//                        Config.defaultDateFormat
//                    )
//                )
//            }

            if (loginRes.data!!.dob != null) {
                edEditProfileDob.setText(
                    PubFun.parseDate(
                        loginRes.data!!.dob.trim(),
                        Config.requestDateFormat,
                        Config.editProfileDateFormat
                    )
                )
            }

            //region Setting Profile Photo
            var profilePicUrl = loginRes.data!!.profilePic.trim()
            profilePicUrl = if (profilePicUrl.startsWith("https")) profilePicUrl else
                "${
                    ApiService.BASE_URL.replace(
                        "/api/v1/",
                        ""
                    )
                }/uploads/profile_pics/${profilePicUrl}"

            Utils.showProgress(this@EditProfileActivity)
            if (profilePicUrl.isEmpty()) {
                ivEditProfileImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        this@EditProfileActivity,
                        R.drawable.ic_profile
                    )
                )
            } else {
                Picasso.get().load(profilePicUrl).into(ivEditProfileImage, object : Callback {
                    override fun onSuccess() {
                        Utils.hideProgress(this@EditProfileActivity)
                    }

                    override fun onError(e: java.lang.Exception?) {
                        Utils.hideProgress(this@EditProfileActivity)
                        ivEditProfileImage.setImageDrawable(
                            ContextCompat.getDrawable(
                                this@EditProfileActivity,
                                R.drawable.ic_profile
                            )
                        )
                    }
                })
            }
            //endregion
            // setting gender
            gender = loginRes.data!!.gender.toString()
            if (loginRes.data!!.gender.equals(1)) llMaleEdit.background =
                ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg) else
                llFemalEdit.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg)

            //Progress Bar Response
            profileViewModel.isLoading.observe(this, {
                if (it!!) {
                    Utils.showProgress(this@EditProfileActivity)
                } else {
                    Utils.hideProgress(this@EditProfileActivity)
                }
            })

            //Profile Update Response
            profileViewModel.responseUpdateProfile.observe(this, Observer {
                when (it.status) {
                    0 -> Toast.makeText(this, it.message.toString(), Toast.LENGTH_LONG).show()
                    1 -> loginResponse(it)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loginResponse(res: NewLoginResponse) {
        try {
            Log.e("LOGN RES",gson.toJson(res))
            myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbNewLoginRes)
            myRoomDatabase.daoConfig()
                .insertConfigTable(TableConfig(Config.dbNewLoginRes, gson.toJson(res)))
            Toast.makeText(this, res.message.trim(), Toast.LENGTH_LONG).show()
            //   setProfileInfo()
            ivBack.performClick()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        try {
            if (PubFun.isInternetConnection(this@EditProfileActivity)) {
                Config.bottomBarClickedName = Config.menuBottomBarClick
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

            ivEditProfileImage -> {
                CropImage.activity()
                    .setAspectRatio(1, 1)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .start(this)
            }

            edEditProfileDob -> {
                Utils.setDatePickerDialog(this, edEditProfileDob) {

                }

            }
            llMaleEdit -> {
                gender = "1"
                llMaleEdit.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg)
                llFemalEdit.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_disable_bg)
            }

            llFemalEdit -> {
                gender = "2"
                llFemalEdit.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_enable_bg)
                llMaleEdit.background =
                    ContextCompat.getDrawable(this, R.drawable.ic_gender_disable_bg)
            }

            btn -> {
                var body: MultipartBody.Part? = null
                if (!documentPath.isNullOrEmpty()) {
                    body = Utils.createMultiPart(File(documentPath), "profile_pic")
                }
                if (PubFun.isInternetConnection(this)) {
                    if (isValid()) {

                        profileViewModel.update_profile(
                            Utils.createRequestBody(edEmail.getText().toString()),
                            Utils.createRequestBody(edFullName.getText().toString()),
                            Utils.createRequestBody(edMobileNo.getText().toString()),
                            Utils.createRequestBody(Config.countryCode),
                            Utils.createRequestBody(gender.toString()),
                            Utils.createRequestBody(
                                PubFun.parseDate(
                                    edEditProfileDob.getText().toString(),
                                    Config.editProfileDateFormat,
                                    Config.requestDateFormat
                                )
                            ),
                            Utils.createRequestBody(edZip.getText().toString()),
                            body
                        )

                        Log.e(
                            "Data:",
                            edEmail.getText().toString() + edFullName.getText()
                                .toString() + edMobileNo.getText()
                                .toString() + Config.countryCode + edEditProfileDob.getText()
                                .toString() + edZip.getText().toString()
                        )
                        Log.e("gender:", gender.toString())
                    }
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@EditProfileActivity)
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
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun isValid(): Boolean {
        when {
            edEditProfileFullName.getText().toString().isBlank() -> {
                showMsgDialogAndProceed("Please enter your Name")
                return false
            }
            edEditProfileEmail.getText().toString().isBlank() -> {
                showMsgDialogAndProceed("Please enter email")
                return false
            }
            !Patterns.EMAIL_ADDRESS.matcher(edEditProfileEmail.text.toString()).matches() -> {
                showMsgDialogAndProceed("Please enter valid email")
                return false
            }

            edEditProfileMobileNo.getText().toString().isBlank() -> {
                showMsgDialogAndProceed("Please enter your mobile number")
                return false
            }
            edEditProfileMobileNo.getText().toString().length < 9 -> {
                showMsgDialogAndProceed("Please enter valid mobile number")
                return false
            }
            edEditProfileZipEdit.getText().toString().isBlank() -> {
                showMsgDialogAndProceed("Please enter Zip Code")
                return false
            }
            edEditProfileZipEdit.getText().toString().length != 6 -> {
                showMsgDialogAndProceed("Please enter valid Zip Code")
                return false
            }
            else -> return true
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val result = CropImage.getActivityResult(data)
        try {
            if (result != null) {
                documentPath = result?.getUriFilePath(this)
                ivEditProfileImage.setImageURI(result?.uriContent)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setCountrySpinner() {
        if (PubFun.isInternetConnection(this)) {
            profileViewModel.countries()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }

        profileViewModel.responseCountries.observe(this, Observer {
            if (it.status!!) {
                list.clear()
                list.addAll(it.data?.list!!)
                val dataAdapter: ArrayAdapter<CountryList> =
                    ArrayAdapter<CountryList>(this, R.layout.row_spinner, it.data?.list!!)
//        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                spCountryCode.adapter = dataAdapter
            }
        })

    }
}