package com.themarkettheory.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.newmodels.login.NewLoginResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class LoginViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingSocial = MutableLiveData<Boolean>()
    val responseLogin = MutableLiveData<NewLoginResponse>()
    val responseSocialLogin = MutableLiveData<NewLoginResponse>()

    fun login(
        email: String,
        password: String,
        login_via: String,
        device_type: String,
        device_token: String
    ) {
        Log.e("login data",email)
        Log.e("password",password)
        Log.e("login_via",login_via)
        Log.e("device_type",device_type)
        Log.e("device_token",device_token)
        isLoading.value = true
        disposable = apiService
            .login(email, password, login_via, device_type, device_token)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseLogin.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun social_login(
        login_via: String?,
        device_token: String?,
        device_type: String?,
        social_id: String?,
        name: String?,
        email: String?,
        image: String?
    ) {
        login_via?.let { Log.e("login_via", it) }
        device_type?.let { Log.e("device_type", it) }
        device_token?.let { Log.e("device_token", it) }
        name?.let { Log.e("name", it) }
        email?.let { Log.e("email", it) }
        image?.let { Log.e("image", it) }
        social_id?.let { Log.e("social_id", it) }



        isLoadingSocial.value = true
        disposable = apiService
            .social_login(
                login_via,
                device_token,
                device_type,
                social_id,
                name,
                email,
                image
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSocialLogin.value = result
                isLoadingSocial.value = false
            }, { error -> isLoadingSocial.value = false })
    }

}