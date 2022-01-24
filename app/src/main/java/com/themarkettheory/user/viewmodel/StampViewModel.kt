package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GetStampsResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class StampViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseStamps = MutableLiveData<GetStampsResponse>()

    fun stamps(service_id: String?) {
        isLoading.value = true
        disposable = apiService
            .stamps(service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseStamps.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

}