package com.themarkettheory.user.ui.main.adapter

import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.themarkettheory.user.R

class AttireImageListAdapter(val context: Context, val list: ArrayList<String>) : PagerAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)
    var images = intArrayOf(R.drawable.ic_attire1, R.drawable.ic_attire2, R.drawable.ic_attire1)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val imageLayout = inflater.inflate(R.layout.row_attire_images, view, false)!!
        val imageView = imageLayout.findViewById<ImageView>(R.id.iv)
        Glide.with(context).load(list[position]).into(imageView)
//       imageView.setImageResource(data[position])

        view.addView(imageLayout, 0)

        return imageLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}