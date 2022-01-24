package com.themarkettheory.user.model

import java.io.Serializable

class SocialModel : Serializable {
    var facebook_token: String? = ""
    var instagram_token: String? = ""
    var google_token: String? = ""
    var login_via: String? = ""
    var fullName: String? = ""
    var email: String? = ""
    var profileImage: String? = ""
}