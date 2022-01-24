package com.themarkettheory.user.newmodels.login


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    var address: String = "",
    @SerializedName("apple_token")
    var appleToken: String = "",
    @SerializedName("country_code")
    var countryCode: String = "",
    @SerializedName("country_id")
    var countryId: Int = 0,
    @SerializedName("created_at")
    var createdAt: String = "",
    @SerializedName("device_token")
    var deviceToken: String = "",
    @SerializedName("device_type")
    var deviceType: Int = 0,
    @SerializedName("dob")
    var dob: String = "",
    @SerializedName("email")
    var email: String = "",
    @SerializedName("email_token")
    var emailToken: String = "",
    @SerializedName("email_verified")
    var emailVerified: Int = 0,
    @SerializedName("facebook_token")
    var facebookToken: String = "",
    @SerializedName("gender")
    var gender: Int = 0,
    @SerializedName("google_token")
    var googleToken: String = "",
    @SerializedName("id")
    var id: Int = 0,
    @SerializedName("instagram_token")
    var instagramToken: String = "",
    @SerializedName("is_block")
    var isBlock: Int = 0,
    @SerializedName("is_deleted")
    var isDeleted: Int = 0,
    @SerializedName("is_verify_vendor")
    var isVerifyVendor: Int = 0,
    @SerializedName("last_name")
    var lastName: String = "",
    @SerializedName("latitude")
    var latitude: String = "",
    @SerializedName("login_via")
    var loginVia: Int = 0,
    @SerializedName("longitude")
    var longitude: String = "",
    @SerializedName("mobile")
    var mobile: String = "",
    @SerializedName("mobile_verified")
    var mobileVerified: Int = 0,
    @SerializedName("name")
    var name: String = "",
    @SerializedName("otp")
    var otp: String = "",
    @SerializedName("points")
    var points: Int = 0,
    @SerializedName("profile_pic")
    var profilePic: String = "",
    @SerializedName("refer_by")
    var referBy: String = "",
    @SerializedName("referral_code")
    var referralCode: String = "",
    @SerializedName("role")
    var role: Int = 0,
    @SerializedName("service_id")
    var serviceId: Int = 0,
    @SerializedName("status")
    var status: Int = 0,
    @SerializedName("token")
    var token: String = "",
    @SerializedName("updated_at")
    var updatedAt: String = "",
    @SerializedName("user_ref_id")
    var userRefId: Int = 0,
    @SerializedName("user_type")
    var userType: Int = 0,
    @SerializedName("vendor_role")
    var vendorRole: Any = "",
    @SerializedName("zip")
    var zip: String = "",
    @SerializedName("admin_start_time")
    var admin_start_time: String = "",
    @SerializedName("admin_end_time")
    var admin_end_time: String = "",
    @SerializedName("admin_email")
    var admin_email: String = "",
    @SerializedName("admin_mobile")
    var admin_mobile: String = "",
    @SerializedName("terms_condition")
    var terms_condition: String = "",
    @SerializedName("is_notification")
    var is_notification: Int = 0
)