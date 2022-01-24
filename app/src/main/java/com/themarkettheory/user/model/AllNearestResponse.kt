package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Nearby

class AllNearestResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: AllNearestData? = null
}

class AllNearestData {
    @SerializedName("list")
    @Expose
    var list: List<Nearby>? = null
}