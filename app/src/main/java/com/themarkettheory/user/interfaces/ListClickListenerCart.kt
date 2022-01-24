package com.themarkettheory.user.interfaces

import android.view.View

interface ListClickListenerCart {
    fun onClickListener(view: View, pos: Int, objects: Any, isItemAdded: Boolean)
}