package com.themarkettheory.user.newmodels.review


import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    var id: Int? = 0,
    @SerializedName("user_ref_id")
    var userRefId: Int? = 0,
    @SerializedName("service_id")
    var serviceId: Int? = 0,
    @SerializedName("email")
    var email: String? = "",
    @SerializedName("login_via")
    var loginVia: Int? = 0,
    @SerializedName("mobile")
    var mobile: String? = "",
    @SerializedName("name")
    var name: String? = "",
    @SerializedName("profile_pic")
    var profilePic: String? = "",
    @SerializedName("dob")
    var dob: Any? = Any(),
    @SerializedName("gender")
    var gender: Int? = 0,
    @SerializedName("facebook_token")
    var facebookToken: String? = "",
    @SerializedName("instagram_token")
    var instagramToken: String? = "",
    @SerializedName("apple_token")
    var appleToken: String? = "",
    @SerializedName("device_type")
    var deviceType: Int? = 0,
    @SerializedName("device_token")
    var deviceToken: String? = "",
    @SerializedName("is_block")
    var isBlock: Int? = 0,
    @SerializedName("status")
    var status: Int? = 0,
    @SerializedName("email_verified")
    var emailVerified: Int? = 0,
    @SerializedName("mobile_verified")
    var mobileVerified: Int? = 0,
    @SerializedName("zip")
    var zip: String? = "",
    @SerializedName("address")
    var address: String? = "",
    @SerializedName("latitude")
    var latitude: String? = "",
    @SerializedName("longitude")
    var longitude: String? = "",
    @SerializedName("otp")
    var otp: String? = "",
    @SerializedName("refer_by")
    var referBy: String? = "",
    @SerializedName("referral_code")
    var referralCode: String? = "",
    @SerializedName("points")
    var points: Int? = 0,
    @SerializedName("is_deleted")
    var isDeleted: Int? = 0,
    @SerializedName("role")
    var role: String? = "",
    @SerializedName("vendor_role")
    var vendorRole: Int? = 0,
    @SerializedName("user_type")
    var userType: Int? = 0,
    @SerializedName("created_at")
    var createdAt: String? = "",
    @SerializedName("updated_at")
    var updatedAt: String? = "",
    @SerializedName("google_token")
    var googleToken: String? = "",
    @SerializedName("country_id")
    var countryId: Int? = 0,
    @SerializedName("country_code")
    var countryCode: String? = "",
    @SerializedName("last_name")
    var lastName: String? = "",
    @SerializedName("email_token")
    var emailToken: String? = "",
    @SerializedName("is_verify_vendor")
    var isVerifyVendor: Int? = 0,
    @SerializedName("is_password_change")
    var isPasswordChange: Int? = 0,
    @SerializedName("referral_points")
    var referralPoints: Int? = 0,
    @SerializedName("admin_start_time")
    var adminStartTime: String? = "",
    @SerializedName("admin_end_time")
    var adminEndTime: String? = ""
)