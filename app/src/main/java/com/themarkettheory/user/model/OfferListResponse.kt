package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

class OfferListResponse : Serializable {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: OfferData? = null
}

class OfferData : Serializable {
    @SerializedName("list")
    @Expose
    var list: List<OfferDatum>? = null
}

class OfferDatum : Serializable {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("terms")
    @Expose
    var terms: String? = null

    @SerializedName("menu_id")
    @Expose
    var menu_id: Int? = null

    @SerializedName("use_via")
    @Expose
    var use_via: Int? = null

    @SerializedName("expire_in")
    @Expose
    var expire_in: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("category_id")
    @Expose
    var category_id: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("is_active")
    @Expose
    var is_active: Int? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("discount_type")
    @Expose
    var discountType: Int? = null

    @SerializedName("max_discount")
    @Expose
    var maxDiscount: Float? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Float? = null

    @SerializedName("applicable_discount_amount")
    @Expose
    var applicable_discount_amount: Float? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("offer_for")
    @Expose
    var offerFor: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("service_name")
    @Expose
    var service_name: String? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    var finaldiscountedAmount: Float? = null

    @SerializedName("currency")
    @Expose
    var currency: String? = ""
}