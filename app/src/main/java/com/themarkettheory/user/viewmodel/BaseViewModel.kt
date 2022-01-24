package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.helper.Prefs
import com.themarkettheory.user.network.ApiService
import io.reactivex.disposables.Disposable

open class BaseViewModel(application: Application) : AndroidViewModel(application) {
    val isLoading = MutableLiveData<Boolean>()
    var errorMsg = MutableLiveData<String>()
    var category = MutableLiveData<String>()
    var disposable: Disposable? = null
    val prefs = Prefs(application.applicationContext)
    val apiService by lazy {
        ApiService.create(prefs.getAccessToken(application.applicationContext))
    }

    override fun onCleared() {
        disposable?.dispose()
    }
}