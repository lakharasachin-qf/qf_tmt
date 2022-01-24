package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.newmodels.searchrestaurant.SearchRestaurantRes
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class SearchViewModel(application: Application) : BaseViewModel(application) {
    val isLoadingSearch = MutableLiveData<Boolean>()
    val responseSearch = MutableLiveData<SearchRestaurantRes>()

    fun allSearch(search_string: String) {
        isLoading.value = true
        disposable = apiService
            .all_search(search_string)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseSearch.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message!!.toString()
            })
    }
}