package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.*
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.booking.bookingdetails.NewBookingDetailsRes
import com.themarkettheory.user.newmodels.myorders.MyOrdersNewRes
import com.themarkettheory.user.newmodels.mytablebookings.MyTableBookingNewRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class MenuViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseTableBookings = MutableLiveData<MyTableBookingNewRes>()
    val responseEvents = MutableLiveData<MyEventsResponse>()
    val responseOrders = MutableLiveData<MyOrderResonse>()
    val responseOrdersNew = MutableLiveData<MyOrdersNewRes>()
    val responseCancelOrder = MutableLiveData<NewGeneralRes>()
    val responseSwitchToBusiness = MutableLiveData<NewGeneralRes>()
    val responseOrderDetail = MutableLiveData<OrderDetailResponse>()
    val responseBookingDetail = MutableLiveData<NewBookingDetailsRes>()
    val responseGetticketDetail = MutableLiveData<GetTicketDetailResponse>()

    //new order detail
    val responseOrderDetailNew = MutableLiveData<OrderDetailResponse>()

    fun myTableBooking() {
        isLoading.value = true
        disposable = apiService
            .myTableBooking()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseTableBookings.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun ticket_details(
        order_id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .ticket_details(order_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetticketDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun myEvents() {
        isLoading.value = true
        disposable = apiService
            .my_events()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseEvents.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun cancel_table(
        booking_id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .cancel_table(booking_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCancelOrder.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun myOrders() {
        isLoading.value = true
        disposable = apiService
            .my_orders()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOrdersNew.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun switch_to_business(name: String?, email: String?, mobile: String?, details: String?) {
        isLoading.value = true
        disposable = apiService
            .switch_to_business(name, email, mobile, details)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSwitchToBusiness.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun my_order_details(id: String?) {
        isLoading.value = true
        disposable = apiService
            .my_order_details(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOrderDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun mybooking_details(id: String?) {
        isLoading.value = true
        disposable = apiService
            .mybooking_details(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseBookingDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }


}