package com.themarkettheory.user.newmodels.bucketcart


import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.coupons.NewOfferListData

data class Data(
    @SerializedName("list")
    var list: List<CartList>?,
    @SerializedName("coupon_detail")
    var couponData: NewOfferListData,
    @SerializedName("booking")
    var booking: Booking?,
    @SerializedName("service_details")
    var serviceDetails: ServiceDetails?

)