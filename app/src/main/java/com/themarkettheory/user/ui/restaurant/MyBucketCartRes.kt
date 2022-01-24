package com.themarkettheory.user.ui.restaurant

data class MyBucketCartRes(
    val serviceId: Int,
    val serviceName: String,
    val foodType: Int,
    val isSpicy: Int,
    val menuID: Int,
    val title: String,
    val categoryName: String,
    val finalPrice: Double,
    val actualPrice: Double,
    val point: Int,
    val preparingTime: String,
    var qty: Int,
    val tax: Double,
    val currency: String,
    val currencyStr: String,
    val offerMenuId: Int,
    val offerCouponCode: String,
    val offerDiscountType: Int,
    val offerDiscountAmount: Int,
    val offerBuyQty: Int,
    val offerGetQty: Int
)
