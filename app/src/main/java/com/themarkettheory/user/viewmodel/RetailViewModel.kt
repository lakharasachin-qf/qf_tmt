package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class RetailViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseRetailMenus = MutableLiveData<RetailMenuResponse>()
    val responseRetailMenuDetail = MutableLiveData<RetailMenuDetailResponse>()
    val responsePickup = MutableLiveData<PickupResponse>()
    val responseAddCart = MutableLiveData<GeneralResponse>()

    fun retailMenus(id: String?) {
        isLoading.value = true
        disposable = apiService
            .retail_menus(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseRetailMenus.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun add_cart(menu_id: String?, qty: String?, service_id: String?) {
        isLoading.value = true
        disposable = apiService
            .add_cart(menu_id, qty, service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddCart.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun retailMenuDetail(id: String?) {
        isLoading.value = true
        disposable = apiService
            .retail_menu_detail(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseRetailMenuDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun pickup(
        service_id: String?,
        subtotal: String?,
        tax_amount: String?,
        tax: String?,
        discount_type: String?,
        discount: String?,
        total: String?,
        payment_id: String?,
        offer_id: String?,
        orders: JSONArray,
        points: String?,
        special_instruction: String?,
        sizeId: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .reatilpickup(
                service_id,
                subtotal,
                tax_amount,
                tax,
                discount_type,
                discount,
                total,
                payment_id,
                offer_id,
                orders,
                points,
                special_instruction,
                sizeId
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responsePickup.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }
}