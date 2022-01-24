package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.GymPackagesResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class GymViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseGymPackages = MutableLiveData<GymPackagesResponse>()
    val responseGymPackageEnquiry = MutableLiveData<GeneralResponse>()

    fun gym_packages(
        id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .gym_packages(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGymPackages.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun gym_enquiry(
        package_id: String?,
        service_id: String?,
        person: String?,
        special_instruction: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .gym_enquiry(package_id, service_id, person, special_instruction)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGymPackageEnquiry.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }


}