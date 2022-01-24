package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.PointsDetailResponse
import com.themarkettheory.user.model.TotalPointsDetailResponse
import com.themarkettheory.user.newmodels.mypoints.NewMyPointsRes
import com.themarkettheory.user.newmodels.pointhistory.NewPointHistoryRes
import com.themarkettheory.user.newmodels.totalpoints.NewTotalPointRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class PointsViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseMyPoints = MutableLiveData<NewMyPointsRes>()
    val responseTotalPointsDetail = MutableLiveData<TotalPointsDetailResponse>()

    //    val responseTotalPoints = MutableLiveData<TotalPointsResponse>()
    val responseTotalPoints = MutableLiveData<NewTotalPointRes>()
    val responsePointsHistory = MutableLiveData<NewPointHistoryRes>()
    val responsePointsDetail = MutableLiveData<PointsDetailResponse>()

    fun myPoints(serviceId: String?) {
        isLoading.value = true
        disposable = apiService
            .mypoints(serviceId)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseMyPoints.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun total_points_details_new(service_id: String?) {
        isLoading.value = true
        disposable = apiService
            .total_points_details_new(service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseTotalPointsDetail.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
            })
    }

    fun totalPoints(filter: String, service_id: String) {
        isLoading.value = true
        disposable = apiService
            .total_points(filter, service_id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseTotalPoints.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun points_history(get_point: String, serviceid: String) {
        isLoading.value = true
        disposable = apiService
            .points_history(get_point, serviceid)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responsePointsHistory.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun total_points_details(id: String?) {
        isLoading.value = true
        disposable = apiService
            .total_points_details(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responsePointsDetail.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

}