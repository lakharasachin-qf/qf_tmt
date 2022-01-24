package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.BookSaloonResponse
import com.themarkettheory.user.model.SaloonSpaServicesResponse
import com.themarkettheory.user.model.SpecialistListResponse
import com.themarkettheory.user.newmodels.overview.NewServiceDetailsRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.json.JSONArray


class SaloonViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingSocial = MutableLiveData<Boolean>()
    val responseServiceDetail = MutableLiveData<NewServiceDetailsRes>()
    val responseSaloonServices = MutableLiveData<SaloonSpaServicesResponse>()
    val responseSpecialists = MutableLiveData<SpecialistListResponse>()
    val responseBookSalon = MutableLiveData<BookSaloonResponse>()

    fun saloon_spa_services(
        id: String?,
        gender: String
    ) {
        isLoading.value = true
        disposable = apiService
            .saloon_spa_services(id, gender)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSaloonServices.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun service_details(id: String?) {
//        isLoading.value = true
        disposable = apiService
            .service_details(id, "23.0401", "72.5315")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseServiceDetail.value = result
//                isLoading.value = false
            }, { error ->
//                isLoading.value = false
            })
    }

    fun book_saloon(
        service_id: String?,
        subtotal: String?,
        tax_amount: String?,
        tax: String?,
        discount_type: String?,
        discount: String?,
        total: String?,
        payment_id: String?,
        offer_id: String?,
        services: JSONArray,
        specialist_id: String?,
        date: String?,
        time: String?,
        total_person: String?,
        adult: String?,
        child: String?,
        occasion_id: String?,
        special_instruction: String?,
    ) {
        isLoading.value = true
        disposable = apiService
            .book_saloon(
                service_id,
                subtotal,
                tax_amount,
                tax,
                discount_type,
                discount,
                total,
                payment_id,
                offer_id,
                services,
                specialist_id,
                date,
                time,
                total_person,
                adult,
                child,
                occasion_id,
                special_instruction
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseBookSalon.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun specialists(
        id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .specialists(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSpecialists.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }


}