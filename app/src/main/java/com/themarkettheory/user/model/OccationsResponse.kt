package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class OccationsResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: ArrayList<OccationsDatum>? = null
}

class OccationsDatum {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("created_at")
    @Expose
    val createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    val updatedAt: Any? = null

    override fun toString(): String {
        return name.toString()
    }
}