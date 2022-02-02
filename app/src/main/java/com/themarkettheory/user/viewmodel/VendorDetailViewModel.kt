package com.themarkettheory.user.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.OccationsResponse
import com.themarkettheory.user.model.ReviewListResponse
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.booking.NewBookingRes
import com.themarkettheory.user.newmodels.livedeals.NewLiveDealRes
import com.themarkettheory.user.newmodels.menulist.NewMenuListRes
import com.themarkettheory.user.newmodels.overview.NewServiceDetailsRes
import com.themarkettheory.user.newmodels.overview.allServiceImages.AllServiceImages
import com.themarkettheory.user.newmodels.review.NewReviewDataRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class VendorDetailViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val isLoadingBookTable = MutableLiveData<Boolean>()
    val responseServiceDetail = MutableLiveData<NewServiceDetailsRes>()
    val responseLiveDealMenu = MutableLiveData<NewLiveDealRes>()

    //    val responseReviews = MutableLiveData<ReviewListResponse>()
    val responseReviews = MutableLiveData<NewReviewDataRes>()
    val responseAddCart = MutableLiveData<GeneralResponse>()
    val responseMenuAddCart = MutableLiveData<NewGeneralRes>()
    val responseAddReport = MutableLiveData<NewGeneralRes>()
    val responseFavoriteService = MutableLiveData<NewGeneralRes>()

    //    val responseGetMenus = MutableLiveData<GetMenuResponse>()
    val responseGetMenus = MutableLiveData<NewMenuListRes>()
    val responseBookTable = MutableLiveData<NewBookingRes>()
    val responseOccations = MutableLiveData<OccationsResponse>()
    val responseAllServiceImages = MutableLiveData<AllServiceImages>()

    val responseCheckRestaurantTime = MutableLiveData<NewGeneralRes>()

    fun service_details(id: String?) {
        isLoading.value = true
        disposable = apiService
            .service_details(id, "23.0401", "72.5315")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseServiceDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun all_service_images(id: String) {
        isLoading.value = true
        disposable = apiService
            .all_service_images(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAllServiceImages.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun live_deals(id: String?) {
        isLoading.value = true
        disposable = apiService
            .live_deals(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseLiveDealMenu.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun report_service(id: String?, description: String?, type: String?, question: String?) {
        isLoading.value = true
        disposable = apiService
            .report_service(id, description, type, question)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddReport.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }


    fun occations() {
//        isLoading.value = true
        disposable = apiService
            .occations()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseOccations.value = result
//                isLoading.value = false
            }, { error ->
//                isLoading.value = false
            })
    }

    fun book_table(
        service_id: String?,
        date: String?,
        time: String?,
        total_person: String?,
        adult: String?,
        child: String?,
        occasion_id: String?,
        special_request: String?
    ) {
        Log.e("service_id",service_id.toString())
        Log.e("date",date.toString())
        Log.e("time",time.toString())
        Log.e("total_person",total_person.toString())
        Log.e("adult",adult.toString())
        Log.e("child",child.toString())
        Log.e("occasion_id",occasion_id.toString())
        Log.e("special_request",special_request.toString())
        isLoadingBookTable.value = true
        disposable = apiService
            .book_table(
                service_id,
                date,
                time,
                total_person,
                adult,
                child,
                occasion_id,
                special_request
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseBookTable.value = result
                isLoadingBookTable.value = false
            }, { error -> isLoadingBookTable.value = false })
    }

    fun favourite_service(service_id: String?) {
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

    fun add_cart(menu_id: String?, qty: String?, service_id: String?, is_redeem: String?) {
        isLoading.value = true
        disposable = apiService
            .add_cart(menu_id, qty, service_id, is_redeem)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddCart.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun menu_add_cart(
        service_id: String?,
        dish_id: String?,
        is_redeem: String?,
        qty: String?,
        booking_id: String,
        isLiveDeal: Int,
        isDineIn: Int
    ) {

        Log.e("data",service_id.toString())
        isLoading.value = true
        disposable = apiService
            .menu_add_cart(service_id, dish_id, is_redeem, qty, booking_id, isLiveDeal, isDineIn)
            .subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseMenuAddCart.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }


    fun review(id: String?) {
        isLoading.value = true
        disposable = apiService
            .review(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                id?.let { Log.e("review id", it) }
                responseReviews.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun menus(id: String?) {
        isLoading.value = true
        disposable = apiService
            .menus(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetMenus.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }
    fun menusNew(id: String?,booking_id: String?) {
        isLoading.value = true
        disposable = apiService
            .menusNew(id,booking_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetMenus.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun checkRestaurantTime(id: Int?, time: String?) {
        if (time != null) {
            Log.e("Pick Up Time", time)
        }
        isLoading.value = true
        disposable = apiService
            .checkRestaurantTime(id, time)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                responseCheckRestaurantTime.value = it
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message!!.toString().trim()
            })
    }




}