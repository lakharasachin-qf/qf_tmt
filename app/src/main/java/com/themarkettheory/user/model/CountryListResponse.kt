package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CountryListResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: CountryData? = null

}

class CountryData {
    @SerializedName("list")
    @Expose
    var list: ArrayList<CountryList>? = null
}

class CountryList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("iso")
    @Expose
    var iso: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("nicename")
    @Expose
    var nicename: String? = null

    @SerializedName("iso3")
    @Expose
    var iso3: String? = null

    @SerializedName("numcode")
    @Expose
    var numcode: String? = null

    @SerializedName("phonecode")
    @Expose
    var phonecode: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    override fun toString(): String {
        return "+" + phonecode + " (" + nicename + ")"
    }
}

