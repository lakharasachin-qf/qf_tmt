package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class SpecialistListResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: SpecialistData? = null
}

class SpecialistData {
    @SerializedName("list")
    @Expose
    var list: List<Data>? = null
}