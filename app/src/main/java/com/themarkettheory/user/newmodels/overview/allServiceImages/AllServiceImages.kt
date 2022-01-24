package com.themarkettheory.user.newmodels.overview.allServiceImages

import com.themarkettheory.user.newmodels.overview.Image

data class AllServiceImages(
    var `data`: ArrayList<Image>?,
    var message: String?,
    var status: Int?
)