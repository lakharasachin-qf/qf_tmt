package com.themarkettheory.user.newmodels.updateprofile


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("user_ref_id")
    var userRefId: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("login_via")
    var loginVia: Int?,
    @SerializedName("mobile")
    var mobile: String?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("profile_pic")
    var profilePic: String?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("gender")
    var gender: Int?,
    @SerializedName("facebook_token")
    var facebookToken: String?,
    @SerializedName("instagram_token")
    var instagramToken: String?,
    @SerializedName("apple_token")
    var appleToken: String?,
    @SerializedName("device_type")
    var deviceType: Int?,
    @SerializedName("device_token")
    var deviceToken: String?,
    @SerializedName("is_block")
    var isBlock: Int?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("email_verified")
    var emailVerified: Int?,
    @SerializedName("mobile_verified")
    var mobileVerified: Int?,
    @SerializedName("zip")
    var zip: String?,
    @SerializedName("address")
    var address: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("otp")
    var otp: String?,
    @SerializedName("refer_by")
    var referBy: String?,
    @SerializedName("referral_code")
    var referralCode: String?,
    @SerializedName("points")
    var points: Int?,
    @SerializedName("is_deleted")
    var isDeleted: Int?,
    @SerializedName("role")
    var role: String?,
    @SerializedName("vendor_role")
    var vendorRole: Any?,
    @SerializedName("user_type")
    var userType: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("google_token")
    var googleToken: String?,
    @SerializedName("country_id")
    var countryId: Int?,
    @SerializedName("country_code")
    var countryCode: String?,
    @SerializedName("last_name")
    var lastName: String?,
    @SerializedName("email_token")
    var emailToken: String?,
    @SerializedName("is_verify_vendor")
    var isVerifyVendor: Int?
)