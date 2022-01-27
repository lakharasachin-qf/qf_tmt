package com.themarkettheory.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.GetCartResponse
import com.themarkettheory.user.model.PickupResponse
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.bucketcart.GetCartNewRes
import com.themarkettheory.user.newmodels.bucketcart.confirm_order.NewConfirmOrderRes
import com.themarkettheory.user.newmodels.orderconfirmation.GetNewOrderConfirmRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class CartViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingSocial = MutableLiveData<Boolean>()
    val responseAddCart = MutableLiveData<GeneralResponse>()
    val responsePickup = MutableLiveData<PickupResponse>()
    val responseConfirmOrder = MutableLiveData<NewConfirmOrderRes>()
    val responseCheckService = MutableLiveData<PickupResponse>()
    val responseGetCart = MutableLiveData<GetCartResponse>()

    val responsePickupNew = MutableLiveData<GetCartNewRes>()
    val responseGetCartNew = MutableLiveData<GetCartNewRes>()
    val responseOrderDetail = MutableLiveData<GetNewOrderConfirmRes>()
    val responseCancelOrder = MutableLiveData<NewGeneralRes>()

    fun add_cart(menu_id: String?, qty: String?, service_id: String?) {
//        isLoading.value = true
        disposable = apiService
            .add_cart(menu_id, qty, service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddCart.value = result
//                isLoading.value = false
            }, { error ->
//                isLoading.value = false
            })
    }

    fun my_order_details(id: String?) {
        isLoading.value = true
        disposable = apiService
            .my_order_details_new(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOrderDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun cancelOrder(id: String?, reason: String?) {
        isLoading.value = true
        disposable = apiService
            .cancelOrder(id, reason)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCancelOrder.value = result
                isLoading.value = false
            }, { error ->
                errorMsg.value = error.message!!
                isLoading.value = false
            })
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
        pickup_time: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .pickup(
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
                pickup_time
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responsePickup.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun check_service(
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
        pickup_time: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .check_service(
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
                pickup_time
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCheckService.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun confirmOrder(
        service_id: String?,
        items: JSONArray,
        subtotal: String?,
        total: String?,
        offer_id: String?,
        payment_id: String?,
        points: String?,
        special_instruction: String?,
        pickup_time: String?,
        tax: String?,
        booking_id: String?

    ) {

        pickup_time?.let { Log.e("Order Confimation time", it) }
        isLoading.value = true
        disposable = apiService
            .pickup(
                service_id,
                items,
                subtotal,
                total,
                offer_id,
                payment_id,
                points,
                special_instruction,
                pickup_time,
                tax,
                booking_id
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseConfirmOrder.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }


    fun get_cart() {
        isLoading.value = true
        disposable = apiService
            .get_cart()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetCart.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun get_cart(booking_id: Int, is_redeem: Int, isLiveDeal: Int,isDineIn: Int) {
        Log.e("booking_id","{$booking_id}")
        isLoading.value = true

        disposable = apiService
            //.get_cart(booking_id, is_redeem, isLiveDeal,isDineIn)
            .get_cartNew(booking_id, is_redeem, isLiveDeal)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetCartNew.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun special_request(
        booking_id: String,
        special_request: String,
        is_redeem: String,
        isLiveDeal: Int
    ) {
        isLoading.value = true
        disposable = apiService
            .add_special_instruction(booking_id, special_request, is_redeem, isLiveDeal)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetCartNew.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun pickup_type(type: String, schedule_time: String, is_redeem: Int, isLiveDeal: Int,isDineIn: Int) {
        isLoading.value = true
        if(schedule_time.isNotEmpty()) {
            disposable = apiService
                .schedule_pickup_type(type, schedule_time, is_redeem, isLiveDeal, isDineIn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    responseGetCartNew.value = result
                    isLoading.value = false
                }, { error -> isLoading.value = false })
        }else{
            disposable = apiService
                .schedule_pickup_type_noTime(type,  is_redeem, isLiveDeal, isDineIn)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ result ->
                    responseGetCartNew.value = result
                    isLoading.value = false
                }, { error -> isLoading.value = false })
        }
    }
    //["is_live_deal": false, "is_dine_in": false, "type": "SCHEDULE_PICKUP", "is_redeem": false]

}