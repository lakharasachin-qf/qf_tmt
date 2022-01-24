package com.themarkettheory.user.newmodels.signup

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class signUpUserData {
    @SerializedName("signUpEmailId")
    @Expose
    var signUpEmailId = ""

    @SerializedName("signUpMobile")
    @Expose
    var signUpMobile = ""

    @SerializedName("signUpPassword")
    @Expose
    var signUpPassword = ""

}