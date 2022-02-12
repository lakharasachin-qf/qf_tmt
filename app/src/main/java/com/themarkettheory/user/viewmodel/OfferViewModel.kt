package com.themarkettheory.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.helper.Constants
import com.themarkettheory.user.model.*
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.coupons.NewOfferListRes
import com.themarkettheory.user.newmodels.coupons.apply.CheckPromoCodeRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class OfferViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingSocial = MutableLiveData<Boolean>()
    val isLoadingActivateCoupon = MutableLiveData<Boolean>()
    val responseOffersRegular = MutableLiveData<OfferListResponse>()
    val responseOffersBuzzer = MutableLiveData<OfferListResponse>()
    val responseOffersCrossPromotion = MutableLiveData<OfferListResponse>()
    val responseOffers = MutableLiveData<NewOfferListRes>()
    val responseFavoriteCoupon = MutableLiveData<NewGeneralRes>()
    val responseCoupons = MutableLiveData<OfferListResponse>()
    val responseCheckPromode = MutableLiveData<CheckPromoCodeResponse>()
    val responseCheckPromoCodeNew = MutableLiveData<CheckPromoCodeRes>()
    val responseActivatedCoupons = MutableLiveData<GetActivatedCouponsResponse>()
    val responseActivateCoupon = MutableLiveData<GeneralResponse>()
    val responsePickNowNew = MutableLiveData<GeneralResponse>()

    fun pickNowNew(id: Int?, time: Int?) {

        isLoading.value = true
        disposable = apiService
            .pickup_new(id, time)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responsePickNowNew.value = it
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message!!.toString().trim()
            })
    }

    fun offer_list(
        id: String?,
        type: String?
    ) {
        isLoading.value = true
        disposable = apiService
            //.offers_list(id,type)
            .offers_list2(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOffers.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun offerFavoriteCoupon(
        offer_id: String
    ) {
        isLoading.value = true
        disposable = apiService
            .favourite_coupon(offer_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                isLoading.value = false
                responseFavoriteCoupon.value = result
            }, { error -> isLoading.value = false })
    }

    fun activate_coupon(offer_id: String) {
        isLoadingActivateCoupon.value = true
        disposable = apiService
            .activate_coupon(offer_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseActivateCoupon.value = result
                isLoadingActivateCoupon.value = false
            }, { error ->
                isLoadingActivateCoupon.value = false
                errorMsg.value = error.message
            })
    }

    fun couponsRegular() {
        isLoading.value = true
        disposable = apiService
            .coupons(Constants.regular)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOffersRegular.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun couponsBuzzer() {
        isLoading.value = true
        disposable = apiService
            .coupons(Constants.buzzer)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOffersBuzzer.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun get_activated_coupons() {
        isLoading.value = true
        disposable = apiService
            .get_activated_coupons()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseActivatedCoupons.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun couponsCrossPrmotion() {
        isLoading.value = true
        disposable = apiService
            .coupons(Constants.cross_promotion)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOffersCrossPromotion.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun check_promocode(
        promocode: String?,
        id: String?,
        type: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .check_promocode(promocode, id, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCheckPromode.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun check_promo_code_new(
        id: String?,
        promo_code: String?,
        booking_id: String
    ) {
        isLoading.value = true
        disposable = apiService
            .check_promo_code_new(id, promo_code, booking_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCheckPromoCodeNew.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }
}