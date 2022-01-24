package com.themarkettheory.user.ui.restaurant.restaurant_detail

data class clsMenuList(
    val id: Int, val image: String, val foodType: Int, val isSpicy: Int,
    val point: Int, val preparingTime: String, val title: String,
    val dishQty: String, val unit: String, val categoryName: String,
    val currency: String, val finalPrice: Double, val actualPrice: Double, val isHeader: Boolean,
    var isAdded: Boolean,
)
