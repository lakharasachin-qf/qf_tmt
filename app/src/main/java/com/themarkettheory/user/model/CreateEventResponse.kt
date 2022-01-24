package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class CreateEventResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: CreateEventData? = null

}

class CreateEventData {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("user_id")
    @Expose
    var userId: Int? = null

    @SerializedName("type_id")
    @Expose
    var typeId: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("subtotal")
    @Expose
    var subtotal: String? = null

    @SerializedName("tax_amount")
    @Expose
    var taxAmount: String? = null

    @SerializedName("discount_type")
    @Expose
    var discountType: String? = null

    @SerializedName("discount")
    @Expose
    var discount: String? = null

    @SerializedName("tax")
    @Expose
    var tax: String? = null

    @SerializedName("total")
    @Expose
    var total: String? = null

    @SerializedName("ticket_number")
    @Expose
    var ticketNumber: String? = null

    @SerializedName("offer_id")
    @Expose
    var offerId: Int? = null

    @SerializedName("payment_id")
    @Expose
    var paymentId: String? = null

    @SerializedName("status")
    @Expose
    var status: Int? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    @SerializedName("points")
    @Expose
    var points: Int? = null

    @SerializedName("special_instruction")
    @Expose
    var specialInstruction: String? = null
}