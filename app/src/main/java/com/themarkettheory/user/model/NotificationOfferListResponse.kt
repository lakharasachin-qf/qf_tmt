package com.themarkettheory.user.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose

import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.ServiceDetails
import kotlinx.android.parcel.RawValue


@SuppressLint("ParcelCreator")
@Parcelize
class NotificationOfferListResponse(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    @Expose
    val data: ArrayList<NotificationOfferList>? = null
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
class NotificationOfferList(
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
    @SerializedName("distance")
    var distance: String? = "",
    @SerializedName("service_id")
    var service_id: Int? = 0,
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
    @SerializedName("days")
    var days: Int? = 0,
    @SerializedName("vaild_til")
    var vaild_til: Int? = 0,
    @SerializedName("buzzer_coupon_no")
    var buzzer_coupon_no: Int? = 0,
    @SerializedName("min_offer_val")
    var min_offer_val: Int? = 0,
    @SerializedName("is_delete")
    var is_delete: Int? = 0,
    @SerializedName("is_admin_create")
    var is_admin_create: Int? = 0,
    @SerializedName("service_details")
    var serviceDetails: @RawValue ServiceDetails? = ServiceDetails(),
    //val serviceDetails: @RawValue ArrayList<ServiceDetailss>? = null,
    @SerializedName("expire_in")
    var expireIn: String? = "",
    @SerializedName("is_favourite")
    var isFavourite: Int? = 0
) : Parcelable

//@SuppressLint("ParcelCreator")
//@Parcelize
//class ServiceDetailss(
//    @SerializedName("id")
//    var id: Int? = 0,
//    @SerializedName("title")
//    var title: String? = "",
//    @SerializedName("category_id")
//    var categoryId: Int? = 0,
//    @SerializedName("image")
//    var image: String? = "",
//    @SerializedName("address")
//    var address: String? = "",
//    @SerializedName("latitude")
//    var latitude: String? = "",
//    @SerializedName("longitude")
//    var longitude: String? = "",
//    @SerializedName("rating")
//    var rating: String? = "",
//    @SerializedName("total_rating")
//    var totalRating: String? = "",
//    @SerializedName("is_veg")
//    var isVeg: Int? = 0,
//    @SerializedName("is_non_veg")
//    var isNonVeg: Int? = 0,
//    @SerializedName("is_egg")
//    var isEgg: Int? = 0
//) : Parcelable