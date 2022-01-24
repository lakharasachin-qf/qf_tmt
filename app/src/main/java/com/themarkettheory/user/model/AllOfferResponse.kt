package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Offer

class AllOfferResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: AllOfferData? = null
}

class AllOfferData {
    @SerializedName("list")
    @Expose
    var list: List<Offer>? = null
}