package com.themarkettheory.user.newmodels.addlocation


import com.google.gson.annotations.SerializedName

data class Data(
    @SerializedName("address")
    var address: String?,
    @SerializedName("apple_token")
    var appleToken: String?,
    @SerializedName("country_code")
    var countryCode: String?,
    @SerializedName("country_id")
    var countryId: Int?,
    @SerializedName("created_at")
    var createdAt: String?,
    @SerializedName("device_token")
    var deviceToken: String?,
    @SerializedName("device_type")
    var deviceType: Int?,
    @SerializedName("dob")
    var dob: String?,
    @SerializedName("email")
    var email: String?,
    @SerializedName("email_token")
    var emailToken: String?,
    @SerializedName("email_verified")
    var emailVerified: Int?,
    @SerializedName("facebook_token")
    var facebookToken: String?,
    @SerializedName("gender")
    var gender: Int?,
    @SerializedName("google_token")
    var googleToken: String?,
    @SerializedName("id")
    var id: Int?,
    @SerializedName("instagram_token")
    var instagramToken: String?,
    @SerializedName("is_block")
    var isBlock: Int?,
    @SerializedName("is_deleted")
    var isDeleted: Int?,
    @SerializedName("is_verify_vendor")
    var isVerifyVendor: Int?,
    @SerializedName("last_name")
    var lastName: String?,
    @SerializedName("latitude")
    var latitude: String?,
    @SerializedName("login_via")
    var loginVia: Int?,
    @SerializedName("longitude")
    var longitude: String?,
    @SerializedName("mobile")
    var mobile: String?,
    @SerializedName("mobile_verified")
    var mobileVerified: Int?,
    @SerializedName("name")
    var name: String?,
    @SerializedName("otp")
    var otp: String?,
    @SerializedName("points")
    var points: Int?,
    @SerializedName("profile_pic")
    var profilePic: String?,
    @SerializedName("refer_by")
    var referBy: String?,
    @SerializedName("referral_code")
    var referralCode: String?,
    @SerializedName("role")
    var role: Int?,
    @SerializedName("service_id")
    var serviceId: Int?,
    @SerializedName("status")
    var status: Int?,
    @SerializedName("token")
    var token: String?,
    @SerializedName("updated_at")
    var updatedAt: String?,
    @SerializedName("user_ref_id")
    var userRefId: Int?,
    @SerializedName("user_type")
    var userType: Int?,
    @SerializedName("vendor_role")
    var vendorRole: Any?,
    @SerializedName("zip")
    var zip: String?
)