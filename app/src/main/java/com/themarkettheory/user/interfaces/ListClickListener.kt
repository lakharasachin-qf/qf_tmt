package com.themarkettheory.user.interfaces

import android.view.View

interface ListClickListener {
    fun onClickListener(view: View, pos: Int, objects: Any)
}