package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.newmodels.overview.Image
import com.themarkettheory.user.ui.main.activity.FullScreenActivity

class VendorPhotoListAdapter(
    val context: Context,
    val isOverview: Boolean,
    val data: ArrayList<Image>,
    val flag: Boolean
) :
    PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.row_vendor_photo, view, false)!!
        val ivRestaurant = imageLayout.findViewById<ImageView>(R.id.ivRestaurant)
        val ivPlay = imageLayout.findViewById<ImageView>(R.id.ivPlay)
//       imageView.setImageResource(data[position])

        if (!flag) {
            ivRestaurant.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }


        Glide.with(context).load(data[position].image).into(ivRestaurant)
        if (isOverview) {
            data[position].videoUrl?.let {
                if (it.endsWith(".mp4") || it.endsWith(".m4a") || it.endsWith(".3gp")) {
                    ivPlay.visibility = View.VISIBLE
                } else {
                    ivPlay.visibility = View.GONE
                }
            }
        } else {
            ivPlay.visibility = View.GONE
        }

// purposly commentd by harish
//        ivRestaurant.setOnClickListener {
//            if (!flag) {
//                context.startActivity(
//                    Intent(context, SeeAllPhotosActivity::class.java)
//                        .putExtra("position", position)
//                        .putExtra("data", data)
//                )
//            }
//        }

        ivPlay.setOnClickListener {
            playVideo()
        }
        view.addView(imageLayout, 0)

        return imageLayout
    }

    fun playVideo() {
        context.startActivity(
            Intent(context, FullScreenActivity::class.java)
                .putExtra("videoUrl", data[0].videoUrl)
        )
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}