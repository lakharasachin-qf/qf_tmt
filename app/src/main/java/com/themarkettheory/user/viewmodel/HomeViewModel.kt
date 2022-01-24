package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.helper.Config.Companion.mainActivity
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.model.AllNearestResponse
import com.themarkettheory.user.model.AllOfferResponse
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.SubCategoriesResponse
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.getcategories.NewGetCategoriesRes
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.newmodels.map.NewServiceListRes
import com.themarkettheory.user.newmodels.viewallrecommended.NewAllRecommendedRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class HomeViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseHome = MutableLiveData<NewHomeRes>()

    /*val responseAllEvents = MutableLiveData<AllEventResponse>()*/
    val responseAllRecommended = MutableLiveData<NewAllRecommendedRes>()
    val responseAllNearest = MutableLiveData<AllNearestResponse>()
    val responseAllOffers = MutableLiveData<AllOfferResponse>()
    val responseServices = MutableLiveData<NewServiceListRes>()
    val responseFavoriteEvent = MutableLiveData<GeneralResponse>()
    val responseActivateCoupon = MutableLiveData<GeneralResponse>()
    val responseFavoriteService = MutableLiveData<NewGeneralRes>()
    val responseGetCategories = MutableLiveData<NewGetCategoriesRes>()
    val responseSubCategories = MutableLiveData<SubCategoriesResponse>()

    fun home(latitude: String, longitude: String) {
        isLoading.value = true
        /*responseHome.value = Gson().fromJson(getHomeRes(), HomeResponse::class.java)
        isLoading.value = false*/
        disposable = apiService
            .home(latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseHome.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun activate_coupon(offer_id: String) {
        isLoading.value = true
        disposable = apiService
            .activate_coupon(offer_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseActivateCoupon.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    /*fun view_all_event() {
        isLoading.value = true
        disposable = apiService
            .view_all_event()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAllEvents.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }*/

    fun favourite(event_id: String) {
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

    fun favourite_service(service_id: String) {
//        isLoading.value = true
        disposable = apiService
            .favourite_services(service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseFavoriteService.value = result
//                    isLoading.value = false
            }, { error ->
//                    isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun view_all_recommanded() {
        isLoading.value = true
        disposable = apiService
            .view_all_recommanded()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAllRecommended.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun view_all_nearest() {
        isLoading.value = true
        disposable = apiService
            .view_all_nearest()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAllNearest.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun view_all_offers(service_id: String?) {
        isLoading.value = true
        disposable = apiService
            .view_all_offers(service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAllOffers.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun service_list(
        category: String?,
        sub_category: String,
        type: String,
        food_type: String,
        sort: String,
        isFavourite: String,
        popularLocationId: String
    ) {
        isLoading.value = true
        disposable = apiService
            .service_list(category, sub_category, type, food_type, sort, isFavourite, popularLocationId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseServices.value = result
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

    fun get_sub_categories(id: String?) {
//        isLoading.value = true
        disposable = apiService
            .get_sub_categories(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSubCategories.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun getHomeRes(): String {
        return PubFun.readAssetsFile(mainActivity!!, "HomeRes.json")
    }
}