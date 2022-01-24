package com.themarkettheory.user.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.google.gson.annotations.Expose

import kotlinx.android.parcel.Parcelize

import com.google.gson.annotations.SerializedName


@SuppressLint("ParcelCreator")
@Parcelize
class NotificationListResponse(
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("data")
    @Expose
    val data: ArrayList<NotificationList>? = null
) : Parcelable

@SuppressLint("ParcelCreator")
@Parcelize
class NotificationList(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("user_id")
    var user_id: Int? = 0,
    @SerializedName("title")
    var title: String? = "",
    @SerializedName("message")
    var message: String? = "",
    @SerializedName("is_read")
    var is_read: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("service_id")
    var service_id: Int? = 0,
    @SerializedName("order_id")
    var order_id: Int? = 0,
    @SerializedName("type")
    var type: Int? = 0,
    @SerializedName("coupon_id")
    var coupon_id: Int? = 0,
    @SerializedName("date")
    var date: String? = "",
    @SerializedName("time")
    var time: String? = "",
    @SerializedName("sender_id")
    var senderId: Int? = 0,
    @SerializedName("receiver_id")
    var receiverId: Int? = 0,
    @SerializedName("notification_type")
    var notificationType: Int? = 0,
    @SerializedName("image")
    var image: String? = "",
    @SerializedName("ios_link")
    var iosLink: String? = "",
    @SerializedName("android_link")
    var androidLink: String? = "",
    @SerializedName("notification_time")
    var notification_time: String? = ""
) : Parcelable