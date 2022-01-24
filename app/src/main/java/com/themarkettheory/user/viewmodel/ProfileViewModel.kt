package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.CountryListResponse
import com.themarkettheory.user.model.LoginResponse
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.MultipartBody
import okhttp3.RequestBody


class ProfileViewModel(application: Application) : BaseViewModel(application) {

    val responseGetProfile = MutableLiveData<LoginResponse>()
    val responseGetProfileNew = MutableLiveData<NewLoginResponse>()
    val responseCountries = MutableLiveData<CountryListResponse>()
    val responseUpdateProfile = MutableLiveData<NewLoginResponse>()
    // val responseUpdateProfile = MutableLiveData<NewLoginResponse>()

    fun profile() {
        isLoading.value = true
        disposable = apiService
            .profile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetProfile.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun profileNew() {
        isLoading.value = true
        disposable = apiService
            .profileNew()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                isLoading.value = false
                responseGetProfileNew.value = result
            }, { error -> isLoading.value = false })
    }

    fun update_profile(
        email: RequestBody,
        name: RequestBody,
        mobile: RequestBody,
        country_code: RequestBody,
        gender: RequestBody,
        dob: RequestBody,
        zip: RequestBody,
        body: MultipartBody.Part?
    ) {
        isLoading.value = true
        disposable = apiService
            .update_profile(email, name, mobile, country_code, gender, dob, zip, body)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseUpdateProfile.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun register(
        email: RequestBody,
        mobile: RequestBody,
        country_code: RequestBody,
        name: RequestBody,
        zip: RequestBody,
        device_token: RequestBody,
        device_type: RequestBody,
        login_via: RequestBody,
        dob: RequestBody,
        gender: RequestBody,
        password: RequestBody,
        social_id: RequestBody,
        body: MultipartBody.Part?
    ) {
        isLoading.value = true
        disposable = apiService
            .register(
                email,
                mobile,
                country_code,
                name,
                zip,
                device_token,
                device_type,
                login_via,
                dob,
                gender,
                password,
                social_id, body
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                /*responseUpdateProfile.value = result*/
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun countries() {
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
    }

}