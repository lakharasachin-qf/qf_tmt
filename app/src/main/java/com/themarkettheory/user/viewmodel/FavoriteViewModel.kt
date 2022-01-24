package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.GetFavoriteServicesResponse
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.getcategories.NewGetCategoriesRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class FavoriteViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingSocial = MutableLiveData<Boolean>()
    val responseGetFavoriteServices = MutableLiveData<GetFavoriteServicesResponse>()
    val responseFavoriteService = MutableLiveData<NewGeneralRes>()
    val responseFavoriteEvent = MutableLiveData<GeneralResponse>()
    val responseGetCategories = MutableLiveData<NewGetCategoriesRes>()

    fun get_favourite_services(
        category_id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .get_favourite_services(category_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetFavoriteServices.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun favourite_services(
        service_id: String
    ) {
        isLoading.value = true
        disposable = apiService
            .favourite_services(service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseFavoriteService.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun favourite(
        event_id: String
    ) {
        isLoading.value = true
        disposable = apiService
            .favourite(event_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseFavoriteEvent.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun getCategories() {
//        isLoading.value = true
        disposable = apiService
            .get_categories()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetCategories.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }
}