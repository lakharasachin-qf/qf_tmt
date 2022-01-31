package com.themarkettheory.user.helper

object Constants {

    //stored preference constants
    const val FCM_TOKEN: String = "fcm_token"
    const val PREFERENCE_NAME = "iKholo"

    const val deviceType = "2" // web = 1 , android = 2, iOS = 3 throughout project. In our case it will be always 2
    const val login_model = "login_model"
    const val register_model = "register_model"
    const val fcm_token = "fcm_token"
    const val accessToken = "accessToken"
    const val isSlideShown = "isSlideShown"
    const val userType = "userType"
    const val loginViaNormal = "0"
    const val loginViaFacebook = "1"
    const val loginViaGoogle = "4"

    //category
    const val RESTAURANT = "restaurant"
    const val BAKERY = "bakery"
    const val GYM = "gym"
    const val SALON = "salon"
    const val SPA = "spa"
    const val RETAIL = "retail"
    const val ATTIRE = "attire"

    //type
    const val Order = "order"
    const val Booking = "booking"
    const val image = "image"

    //coupon type
    const val regular = "regular"
    const val buzzer = "buzzer"
    const val cross_promotion = "cross promotion"


    const val android = "android"
    const val RANDOM = "random"
}