package com.themarkettheory.user.ui.dialog.dialogshare

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
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView
import com.themarkettheory.user.R

/**
 * Created by SW11 on 8/13/2015.
 */
class DialogShare(context: Activity) : Dialog(context) {
    var holder: ViewHolder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        setContentView(R.layout.dialog_invite_new)
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

    override fun onBackPressed() {
        super.onBackPressed()
        this.dismiss()
    }

    inner class ViewHolder internal constructor(view: View) {
        var ivFacebook: ShapeableImageView
        var ivWhatsApp: ShapeableImageView
        var ivEmail: ShapeableImageView
        var ivText: ShapeableImageView

        init {
            ivFacebook = view.findViewById(R.id.ivFacebook)
            ivWhatsApp = view.findViewById(R.id.ivWhatsApp)
            ivEmail = view.findViewById(R.id.ivEmail)
            ivText = view.findViewById(R.id.ivText)
        }
    }
}