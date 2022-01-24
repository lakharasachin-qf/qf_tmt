package com.themarkettheory.user.viewmodel

import android.annotation.SuppressLint
import android.app.Application
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.gson.Gson
import com.themarkettheory.user.model.*
import com.themarkettheory.user.newmodels.NewGeneralRes
import com.themarkettheory.user.newmodels.addreview.AddReviewRes
import com.themarkettheory.user.newmodels.changepassword.ChangePasswordRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class GeneralViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseGetProfile = MutableLiveData<LoginResponse>()
    val responseAddReview = MutableLiveData<AddReviewRes>()
    val responseChangePassword = MutableLiveData<ChangePasswordRes>()
    val responseDeleteAccount = MutableLiveData<DeleterResponse>()
    val responseForgotPassword = MutableLiveData<NewGeneralRes>()
    val responseGetCart = MutableLiveData<GetCartResponse>()
    val navigateToBooking = MutableLiveData<Boolean>()
    val responseNotifications = MutableLiveData<NotificationListResponse>()
    val responseNotificationsOffer = MutableLiveData<NotificationOfferListResponse>()
    val responseChangeNotificationStatus = MutableLiveData<NewGeneralRes>()

    fun profile() {
//        isLoading.value = true
        disposable = apiService
            .profile()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetProfile.value = result
//                    isLoading.value = false
            }, { error ->
//                    isLoading.value = false
            })
    }

    fun get_notifications() {
        val gson = Gson()

        isLoading.value = true
        disposable = apiService
            .get_notifications()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseNotifications.value = result
                // Log.e("notifications Data", gson.toJson(result.data))
                isLoading.value = false
            }, { error ->
                error.printStackTrace()
                isLoading.value = false
            })
    }

    fun get_notifications_offerList() {

        isLoading.value = true
        disposable = apiService
            .get_notifications_offer("coupon")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseNotificationsOffer.value = result
                isLoading.value = false
            }, { error ->
                error.printStackTrace()
                isLoading.value = false
            })
    }


    fun change_notification_status(notification_status: Int?, remind_me: Int?) {
//        isLoading.value = true
        disposable = apiService
            .change_notification_status(notification_status, remind_me)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseChangeNotificationStatus.value = result
//                isLoading.value = false
            }, { error ->
                //isLoading.value = false
            })
    }


    fun update_notification_status(notification_status: Int?) {
//        isLoading.value = true
        disposable = apiService
            .update_notification(notification_status)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseChangeNotificationStatus.value = result
//                isLoading.value = false
            }, { error ->
                //isLoading.value = false
            })
    }

    fun deleteAccount() {
        val gson = Gson()
        isLoading.value = true
        disposable = apiService
            .delete_account()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                Log.e("deleted Response", gson.toJson(result))
                responseDeleteAccount.value = result
                isLoading.value = false
            }, { error ->
                error.printStackTrace()
                isLoading.value = false
            })
    }

    fun forget_password(email: String?) {
        isLoading.value = true
        disposable = apiService
            .forget_password(email)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseForgotPassword.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun get_cart() {
//        isLoading.value = true
        disposable = apiService
            .get_cart()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetCart.value = result
//                isLoading.value = false
            }, { error ->
//                isLoading.value = false
            })
    }

    fun add_review(
        service_id: Int?,
        rating: Array<Float>,
        comment: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .add_review(service_id, rating[0], rating[1], rating[2], rating[3], comment)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddReview.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun change_password(new_password: String?, old_password: String?) {
        isLoading.value = true
        disposable = apiService
            .change_password(new_password, old_password)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseChangePassword.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

}