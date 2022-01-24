package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class EventsViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseGetEvents = MutableLiveData<GetEventsResponse>()
    val responseEventDetail = MutableLiveData<EventDetailResponse>()
    val responseCreateEvent = MutableLiveData<CreateEventResponse>()
    val responseFavoriteEvent = MutableLiveData<GeneralResponse>()

    fun get_events() {
        isLoading.value = true
        disposable = apiService
            .get_events()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetEvents.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun favourite_event(eventId: String?) {
//        isLoading.value = true
        disposable = apiService
            .favourite(eventId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseFavoriteEvent.value = result
//                    isLoading.value = false
            }, { error ->
//                    isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun get_event_details(event_id: String?, latitude: String?, longitude: String?) {
        isLoading.value = true
        disposable = apiService
            .get_event_details(event_id, latitude, longitude)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseEventDetail.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun participate(
        event_id: String?,
        subtotal: String?,
        tax_amount: String?,
        tax: String?,
        discount_type: String?,
        discount: String?,
        total: String?,
        payment_id: String?,
        offerId: String?,
        ticket: JSONArray
    ) {
        isLoading.value = true
        disposable = apiService
            .participate(
                event_id,
                subtotal,
                tax_amount,
                tax,
                discount_type,
                discount,
                total,
                payment_id,
                offerId,
                ticket
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseCreateEvent.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

}