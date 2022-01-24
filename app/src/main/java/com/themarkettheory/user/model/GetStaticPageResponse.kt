package com.themarkettheory.user.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


class GetStaticPageResponse {
    @SerializedName("status")
    @Expose
    val status: Boolean? = null

    @SerializedName("message")
    @Expose
    val message: String? = null

    @SerializedName("data")
    @Expose
    val data: StaticPage? = null

}

class StaticPage {
    @SerializedName("id")
    @Expose
    var id: Int? = null

    @SerializedName("type")
    @Expose
    var type: String? = null

    @SerializedName("description")
    @Expose
    var description: String? = null

    @SerializedName("created_at")
    @Expose
    var createdAt: Any? = null

    @SerializedName("updated_at")
    @Expose
    var updatedAt: Any? = null
}