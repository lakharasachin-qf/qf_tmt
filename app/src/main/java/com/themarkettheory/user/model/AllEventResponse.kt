package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class AllEventResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: AllEventData? = null
}

class AllEventData {
    /* @SerializedName("list")
     @Expose
     var list: List<UpcomingEvent>? = null*/
}