package com.themarkettheory.user.ui.dialog.dialogToast

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
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R

/**
 * Created by SW11 on 8/13/2015.
 */
class DialogToast(context: Activity) : Dialog(context) {
    var holder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_toast)
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
        var tvTitle: MaterialTextView
        var tvMessage: MaterialTextView
        var tvProgressDialogLogout: MaterialTextView
        var btnDialogLogout: AppCompatButton
        var btnDialogCancel: AppCompatButton
        var imgLogout: AppCompatImageView

        init {
            tvTitle = view.findViewById(R.id.tvDialogTitle)
            tvMessage = view.findViewById(R.id.tvDialogMessage)
            tvProgressDialogLogout = view.findViewById(R.id.tvProgressDialogLogout)
            btnDialogLogout = view.findViewById(R.id.btnDialogLogout)
            btnDialogCancel = view.findViewById(R.id.btnDialogCancel)
            imgLogout = view.findViewById(R.id.imgLogout)
        }
    }
}