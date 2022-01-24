package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GeneralResponse {

    @SerializedName("status")
    @Expose
    var status: Boolean? = null

    @SerializedName("message")
    @Expose
    var message: String? = null

    /*@SerializedName("data")
    @Expose
    var data: String? = null*/
    @SerializedName("response")
    @Expose
    var response: String? = null

}

