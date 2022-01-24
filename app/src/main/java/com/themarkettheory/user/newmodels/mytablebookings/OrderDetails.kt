package com.themarkettheory.user.newmodels.mytablebookings

data class OrderDetails(
    var id: Int?,
    var order_number: String?,
    var tax: Double?,
    var total: Double?,
    var sub_total: Double?,
    var points: Int?
)