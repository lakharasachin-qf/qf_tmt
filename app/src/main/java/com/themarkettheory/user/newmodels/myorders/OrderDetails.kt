package com.themarkettheory.user.newmodels.myorders


import com.google.gson.annotations.SerializedName

data class OrderDetails(
    @SerializedName("id")
    var id: Int?,
    @SerializedName("order_number")
    var orderNumber: String?,
    @SerializedName("tax")
    var tax: String?,
    @SerializedName("total")
    var total: String?,
    @SerializedName("sub_total")
    var subTotal: String?,
    @SerializedName("points")
    var points: Int?
)