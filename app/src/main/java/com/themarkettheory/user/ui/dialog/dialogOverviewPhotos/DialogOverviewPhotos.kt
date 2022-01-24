package com.themarkettheory.user.ui.dialog.dialogOverviewPhotos

import android.app.Activity
import android.app.Dialog
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.viewpager.widget.ViewPager
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R
import me.relex.circleindicator.CircleIndicator

/**
 * Created by SW11 on 8/13/2015.
 */
class DialogOverviewPhotos(context: Activity) : Dialog(context) {
    var holder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_overview_photos)
        holder = ViewHolder(window!!.decorView)

        val lp = WindowManager.LayoutParams()
        val manager = context.getSystemService(Activity.WINDOW_SERVICE) as WindowManager
        lp.copyFrom(window!!.attributes)
        val point = Point()
        manager.defaultDisplay.getSize(point)
        lp.width = point.x
        lp.height = point.y
        window!!.attributes = lp
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        window!!.setGravity(Gravity.CENTER)
    }

    inner class ViewHolder internal constructor(view: View) {
        var viewPager: ViewPager = view.findViewById(R.id.dialogOverviewViewpager)
        var indicator: CircleIndicator = view.findViewById(R.id.dialogOverViewindicator)
        var btnClose: MaterialTextView = view.findViewById(R.id.ivDialogOverviewClose)
    }
}