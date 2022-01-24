package com.themarkettheory.user.newmodels.overview


import com.google.gson.annotations.SerializedName

data class Offer(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("description")
    var description: String? = "",
    @SerializedName("start_time")
    var startTime: String? = "",
    @SerializedName("end_time")
    var endTime: String? = "",
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("coupon_code")
    var couponCode: String? = "",
    @SerializedName("discount_type")
    var discountType: Int? = 0,
    @SerializedName("max_discount")
    var maxDiscount: Int? = 0,
    @SerializedName("discount_amount")
    var discountAmount: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("type")
    var type: String? = "",
    @SerializedName("offer_for")
    var offerFor: String? = "",
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("terms")
    var terms: String? = "",
    @SerializedName("menu_id")
    var menuId: Int? = 0,
    @SerializedName("use_via")
    var useVia: Int? = 0,
    @SerializedName("buy_qty")
    var buyQty: Int? = 0,
    @SerializedName("get_qty")
    var getQty: Int? = 0,
    @SerializedName("max_avail_coupon")
    var maxAvailCoupon: Int? = 0,
    @SerializedName("expire_in")
    var expireIn: String? = "",
    @SerializedName("service_name")
    var serviceName: String? = "",
    @SerializedName("is_favourite")
    var isFavourite: Int? = 0
)