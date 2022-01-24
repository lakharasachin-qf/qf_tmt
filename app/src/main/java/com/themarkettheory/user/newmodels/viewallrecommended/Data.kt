package com.themarkettheory.user.newmodels.viewallrecommended


import com.google.gson.annotations.SerializedName
import com.themarkettheory.user.newmodels.home.Restaurant

data class Data(
    @SerializedName("list")
    var list: List<Restaurant>?
)