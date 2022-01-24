package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ReviewListResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: List<ReviewDatum>? = null
}

class ReviewDatum {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("service_id")
    @Expose
    var serviceId: Int? = null

    @SerializedName("rating")
    @Expose
    var rating: String? = null

    @SerializedName("review_time")
    @Expose
    var review_time: String? = null

    @SerializedName("comment")
    @Expose
    var comment: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("user")
    @Expose
    var user: User? = null
}

class User {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("email")
    @Expose
    var email: String? = null

    @SerializedName("login_via")
    @Expose
    var loginVia: Int? = null

    @SerializedName("mobile")
    @Expose
    var mobile: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("profile_pic")
    @Expose
    var profilePic: String? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("gender")
    @Expose
    var gender: Int? = null

    @SerializedName("facebook_token")
    @Expose
    var facebookToken: Any? = null

    @SerializedName("instagram_token")
    @Expose
    var instagramToken: Any? = null

    @SerializedName("apple_token")
    @Expose
    var appleToken: String? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: Int? = null

    @SerializedName("device_token")
    @Expose
    var deviceToken: String? = null

    @SerializedName("is_block")
    @Expose
    var isBlock: Int? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("email_verified")
    @Expose
    var emailVerified: Int? = null

    @SerializedName("mobile_verified")
    @Expose
    var mobileVerified: Int? = null

    @SerializedName("zip")
    @Expose
    var zip: Any? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Any? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Any? = null

    @SerializedName("otp")
    @Expose
    var otp: Any? = null

    @SerializedName("refer_by")
    @Expose
    var referBy: Any? = null

    @SerializedName("referral_code")
    @Expose
    var referralCode: String? = null

    @SerializedName("points")
    @Expose
    var points: Any? = null

    @SerializedName("is_deleted")
    @Expose
    var isDeleted: Int? = null

    @SerializedName("role")
    @Expose
    var role: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null
}