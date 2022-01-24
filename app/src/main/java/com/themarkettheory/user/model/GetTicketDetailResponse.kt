package com.themarkettheory.user.model

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class GetTicketDetailResponse {

    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: TicketDetail? = null

}

class TicketDetail {
    @SerializedName("ticket_number")
    @Expose
    var ticketNumber: String? = null

    @SerializedName("title")
    @Expose
    var title: String? = null

    @SerializedName("order")
    @Expose
    var order: TicketOrder? = null

    @SerializedName("image")
    @Expose
    var image: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("address")
    @Expose
    var address: String? = null

    @SerializedName("start")
    @Expose
    var start: String? = null

    @SerializedName("end")
    @Expose
    var end: String? = null

    @SerializedName("date")
    @Expose
    var date: String? = null

    @SerializedName("total_ticket")
    @Expose
    var totalTicket: String? = null

    @SerializedName("tags_new")
    @Expose
    var tags: List<VendorTag>? = null

    @SerializedName("tickets")
    @Expose
    var tickets: List<Ticket>? = null

    @SerializedName("total")
    @Expose
    var total: String? = null

    @SerializedName("base_url")
    @Expose
    var baseUrl: String? = null
}

class TicketOrder {
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

class Ticket {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("order_id")
    @Expose
    var orderId: Int? = null

    @SerializedName("event_price_id")
    @Expose
    var eventPriceId: Int? = null

    @SerializedName("qty")
    @Expose
    var qty: Int? = null

    @SerializedName("price")
    @Expose
    var price: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: String? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String? = null

    /*@SerializedName("ticket_details")
    @Expose
    var ticketDetails: Price? = null*/
}

