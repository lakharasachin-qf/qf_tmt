package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import com.themarkettheory.user.newmodels.login.NewResendOtpResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class RegisterViewModel(application: Application) : BaseViewModel(application) {

    //    @ViewModelInject
//    constructor()
    val isLoadingRegister = MutableLiveData<Boolean>()

    /*val responseRegister = MutableLiveData<LoginResponse>()*/
    val responseVerifyOTP = MutableLiveData<NewLoginResponse>()
    val resendOTP = MutableLiveData<NewResendOtpResponse>()
    /*val responseCountries = MutableLiveData<CountryListResponse>()*/

    //region Added by Milan Sheth
    val responseCheckEmailMobile = MutableLiveData<NewLoginResponse>()

    fun checkEmailMobile(
        mobile: String,
        countryCode: String,
        email: String,
        is_edit: String,
        login_via: String,
        password: String,
        social_id: String,
        profile_pic: String
    ) {
        isLoadingRegister.value = true
        disposable = apiService
            .check_email_mobile(
                mobile,
                countryCode,
                email,
                is_edit,
                login_via,
                password,
                social_id,
                profile_pic
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCheckEmailMobile.value = result
                isLoadingRegister.value = false
            }, { error ->
                isLoadingRegister.value = false
                errorMsg.value = error.message
            })
    }
    //endregion

//    fun register(
//        name: RequestBody,
//        email: RequestBody,
//        mobile: RequestBody,
//        password: RequestBody,
//        device_token: RequestBody,
//        device_type: RequestBody,
//        country_id: RequestBody,
//        body: MultipartBody.Part?
//    ) {
//        isLoadingRegister.value = true
//        disposable = apiService
//            .register(name, email, mobile, password, device_token, device_type, country_id, body)
//            .subscribeOn(Schedulers.io())
//            .observeOn(AndroidSchedulers.mainThread())
//            .subscribe({ result ->
//                responseRegister.value = result
//                isLoadingRegister.value = false
//            }, { error ->
//                isLoadingRegister.value = false
//                errorMsg.value = error.message
//            })
//    }

    fun verifyOTP(mobileNumber: String, otp: String, countryCode: String) {
        isLoading.value = true
        disposable = apiService
            .otp_verification(mobileNumber, otp, countryCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseVerifyOTP.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun resendOTP(mobile: String, countryCode: String) {
        isLoading.value = true
        disposable = apiService
            .resend_otp(mobile, countryCode)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                resendOTP.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    /*fun countries() {
//        isLoading.value = true
        disposable = apiService
            .countries()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCountries.value = result
//                isLoading.value = false
            }, { error ->
//                isLoading.value = false
                errorMsg.value = error.message
            })
    }*/
}


