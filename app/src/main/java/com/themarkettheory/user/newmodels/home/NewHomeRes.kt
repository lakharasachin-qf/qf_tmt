package com.themarkettheory.user.newmodels.home


import com.google.gson.annotations.SerializedName

data class NewHomeRes(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    var `data`: Data? = Data(),
    @SerializedName("is_feedback")
    var isFeedBack: Int? = 0,
    @SerializedName("feedback_remain_service_id")
    var serviceId: Int? = 0,
)