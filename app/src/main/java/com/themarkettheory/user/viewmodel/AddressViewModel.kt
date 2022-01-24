package com.themarkettheory.user.viewmodel

import android.app.Application
import androidx.lifecycle.MutableLiveData
import com.themarkettheory.user.model.GeneralResponse
import com.themarkettheory.user.model.GetAddressListResponse
import com.themarkettheory.user.newmodels.addlocation.NewAddLocationRes
import com.themarkettheory.user.newmodels.getrecentpopularlocation.NewGetRecentPopularLocation
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers


class AddressViewModel(application: Application) : BaseViewModel(application) {

//    @ViewModelInject
//    constructor()

    val responseAddAddress = MutableLiveData<GeneralResponse>()
    val responseEditAddress = MutableLiveData<GeneralResponse>()
    val responseDeleteAddress = MutableLiveData<GeneralResponse>()
    val responseGetAddress = MutableLiveData<GetAddressListResponse>()

    val addLocation = MutableLiveData<NewAddLocationRes>()
    val responseGetRecentPopularLocations = MutableLiveData<NewGetRecentPopularLocation>()

    fun addLocation(
        address: String,
        lat: String,
        lng: String,
        place_id: String,
        type: String
    ) {
        isLoading.value = true
        disposable = apiService
            .add_location(address, lat, lng, place_id, type)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                addLocation.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun getRecentPopularLocationList() {
        isLoading.value = true
        disposable = apiService
            .get_recent_popular_location()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetRecentPopularLocations.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun add_address(
        google_address: String?,
        house_number: String?,
        floor: String?,
        tower: String?,
        type: String?,
        is_default: String?,
        address_optional: String?,
        latitude: String?,
        longitude: String?,
    ) {
        isLoading.value = true
        disposable = apiService
            .add_address(
                google_address,
                house_number,
                floor,
                tower,
                type,
                is_default,
                address_optional,
                latitude,
                longitude
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseAddAddress.value = result
                isLoading.value = false
            }, { error ->
                isLoading.value = false
                errorMsg.value = error.message
            })
    }

    fun edit_address(
        google_address: String?,
        house_number: String?,
        floor: String?,
        tower: String?,
        type: String?,
        is_default: String?,
        address_optional: String?,
        latitude: String?,
        longitude: String?,
        id: String?,
    ) {
        isLoading.value = true
        disposable = apiService
            .edit_address(
                google_address,
                house_number,
                floor,
                tower,
                type,
                is_default,
                address_optional,
                latitude,
                longitude,
                id
            )
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseEditAddress.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun delete_address(
        id: String?
    ) {
        isLoading.value = true
        disposable = apiService
            .delete_address(id)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseDeleteAddress.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

    fun addressList() {
        isLoading.value = true
        disposable = apiService
            .addresses()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({ result ->
                responseGetAddress.value = result
                isLoading.value = false
            }, { error -> isLoading.value = false })
    }

}