package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class LoginResponse {
    @SerializedName("status")
    @Expose
    val status: Int? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: Data? = null
}

class Data {
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

    @SerializedName("country_code")
    @Expose
    var country_code: String? = null

    @SerializedName("name")
    @Expose
    var name: String? = null

    @SerializedName("profile_pic")
    @Expose
    var profilePic: Any? = null

    @SerializedName("dob")
    @Expose
    var dob: String? = null

    @SerializedName("gender")
    @Expose
    var gender: Int? = null

    @SerializedName("facebook_token")
    @Expose
    var facebookToken: String? = null

    @SerializedName("google_token")
    @Expose
    var googleToken: String? = null

    @SerializedName("instagram_token")
    @Expose
    var instagramToken: String? = null

    @SerializedName("apple_token")
    @Expose
    var appleToken: String? = null

    @SerializedName("device_type")
    @Expose
    var deviceType: Int? = null

    @SerializedName("country_id")
    @Expose
    var country_id: Int? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

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
    var zip: String? = null

    @SerializedName("latitude")
    @Expose
    var latitude: Any? = null

    @SerializedName("longitude")
    @Expose
    var longitude: Any? = null

    @SerializedName("otp")
    @Expose
    var otp: String? = null

    @SerializedName("refer_by")
    @Expose
    var referBy: String? = null

    @SerializedName("referral_code")
    @Expose
    var referralCode: String? = null

    @SerializedName("terms_condition")
    @Expose
    var terms_condition: String? = null

    @SerializedName("notification_status")
    @Expose
    var notification_status: Int? = null

    @SerializedName("remind_me")
    @Expose
    var remind_me: Int? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

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

    @SerializedName("token")
    @Expose
    var token: String? = null
}