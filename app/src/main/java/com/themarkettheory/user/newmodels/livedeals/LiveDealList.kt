package com.themarkettheory.user.newmodels.livedeals


import com.google.gson.annotations.SerializedName

data class LiveDealList(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("day")
    var day: String? = "",
    @SerializedName("start_time")
    var startTime: String? = "",
    @SerializedName("end_time")
    var endTime: String? = "",
    @SerializedName("is_auto")
    var isAuto: Int? = 0,
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("menu")
    var menu: Menu? = Menu(),
    @SerializedName("menu_base_url")
    var menuBaseUrl: String? = ""
)