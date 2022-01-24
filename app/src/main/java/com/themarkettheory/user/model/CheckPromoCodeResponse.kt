package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName

class CheckPromoCodeResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: OfferDatum? = null
}

class CheckPromoCodeData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("start_time")
    @Expose
    var startTime: String? = null

    @SerializedName("end_time")
    @Expose
    var endTime: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("coupon_code")
    @Expose
    var couponCode: String? = null

    @SerializedName("discount_type")
    @Expose
    var discountType: Int? = null

    @SerializedName("max_discount")
    @Expose
    var maxDiscount: Int? = null

    @SerializedName("discount_amount")
    @Expose
    var discountAmount: Int? = null

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

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null
}