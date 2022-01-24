package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class GetActivatedCouponsResponse(
    @SerializedName("status")
    @Expose
    var status: Boolean? = null,
    @SerializedName("message")
    @Expose
    var message: String? = null,
    @SerializedName("data")
    @Expose
    var data: List<ActivatedCouponDatum>? = null
) : Serializable

data class ActivatedCouponDatum(
    @SerializedName("id")
    val id: Long? = null,

    @SerializedName("user_id")
    val userID: Long? = null,

    @SerializedName("offer_id")
    val offerID: Long? = null,

    @SerializedName("created_at")
    val createdAt: String? = null,

    @SerializedName("updated_at")
    val updatedAt: String? = null,

    @SerializedName("offer")
    val offer: OfferDatum? = null
) : Serializable