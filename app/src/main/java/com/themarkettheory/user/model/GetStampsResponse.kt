package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class GetStampsResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: GetStampData? = null
}

class GetStampData {
    @SerializedName("list")
    @Expose
    var list: List<StampList>? = null
}

class StampList {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("menu_id")
    @Expose
    var menuId: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("volume")
    @Expose
    var volume: String? = null

    @SerializedName("from_date")
    @Expose
    var fromDate: String? = null

    @SerializedName("to_date")
    @Expose
    var toDate: String? = null

    @SerializedName("is_unlimited")
    @Expose
    var isUnlimited: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("order_count")
    @Expose
    var orderCount: Int? = null

    @SerializedName("actual_stamp")
    @Expose
    var actual_stamp: Int? = null
}
