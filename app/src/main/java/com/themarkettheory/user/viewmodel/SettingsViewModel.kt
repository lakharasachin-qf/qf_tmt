package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GeneralResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SettingsViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseAddAddress = MutableLiveData<GeneralResponse>()

    fun add_address(
        google_address: String?,
        house_number: String?,
        floor: String?,
        tower: String?,
        type: String?,
        is_default: String?,
        address_optional: String?,
        latitude: String?,
        longitude: String?,
    ) {
        isLoading.value = true
        disposable = apiService
            .add_address(
                google_address,
                house_number,
                floor,
                tower,
                type,
                is_default,
                address_optional,
                latitude,
                longitude
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddAddress.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

}