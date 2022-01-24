package com.themarkettheory.user.newmodels.mytablebookings

data class Cart(
    var id: Int?,
    var user_id: Int?,
    var menu_id: Int?,
    var qty: Int?,
    var service_id: Int?,
    var created_at: String?,
    var updated_at: String?,
    var is_redeem: Int?,
    var booking_id: Int?,
    var menu: Menu?
)