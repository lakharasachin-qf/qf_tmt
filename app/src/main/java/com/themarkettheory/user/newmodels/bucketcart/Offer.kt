package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName

data class Offer(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("title")
    var title: String?,
    @SerializedName("description")
    var description: String?,
    @SerializedName("start_time")
    var startTime: String?,
    @SerializedName("end_time")
    var endTime: String?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("coupon_code")
    var couponCode: String?,
    @SerializedName("discount_type")
    var discountType: Int?,
    @SerializedName("max_discount")
    var maxDiscount: Int?,
    @SerializedName("discount_amount")
    var discountAmount: Int? = 0,
    @SerializedName("image")
    var image: String?,
    @SerializedName("type")
    var type: String?,
    @SerializedName("offer_for")
    var offerFor: String?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("terms")
    var terms: String?,
    @SerializedName("menu_id")
    var menuId: Int?,
    @SerializedName("use_via")
    var useVia: Int?,
    @SerializedName("buy_qty")
    var buyQty: Int?,
    @SerializedName("get_qty")
    var getQty: Int?,
    @SerializedName("max_avail_coupon")
    var maxAvailCoupon: Int?,
    @SerializedName("expire_in")
    var expireIn: String?,
    @SerializedName("service_name")
    var serviceName: String?,
    @SerializedName("is_active")
    var isActive: Int?
)