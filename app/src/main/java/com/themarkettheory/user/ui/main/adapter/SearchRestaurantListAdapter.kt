package com.themarkettheory.user.ui.main.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowSearchRestaurentListBinding
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.searchrestaurant.SearchRestaurantData
import com.themarkettheory.user.ui.main.activity.SearchActivity
import java.util.*
import kotlin.collections.ArrayList

class SearchRestaurantListAdapter(
    private val context: SearchActivity,
    private val listener: ListClickListener
) : RecyclerView.Adapter<SearchRestaurantListAdapter.Holder>() {
    //General
    private var searchRestaurantList = ArrayList<SearchRestaurantData>()
    private var searchRestaurantListFilter = ArrayList<SearchRestaurantData>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_search_restaurent_list,
            FrameLayout(parent.context),
            false
        )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val searchRestaurant = searchRestaurantList[position]
            //Restaurant Image
            if (searchRestaurant.image.isNullOrEmpty()) {
                holder.rowSearchRestaurantListBinding!!.ivSearchRestaurantListImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Glide.with(context).load(searchRestaurant.image)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(object : CustomTarget<Drawable>() {
                        override fun onResourceReady(
                            resource: Drawable,
                            transition: Transition<in Drawable>?
                        ) {
                            holder.rowSearchRestaurantListBinding
                                .ivSearchRestaurantListImage.setImageDrawable(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            holder.rowSearchRestaurantListBinding
                                .ivSearchRestaurantListImage.setImageDrawable(
                                    ContextCompat.getDrawable(
                                        context,
                                        R.drawable.ic_camera
                                    )
                                )
                        }
                    })
            }

            //Title
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListName.text =
                searchRestaurant.title!!.trim()
            //Star Rating
            holder.rowSearchRestaurantListBinding.rbSearchRestaurantListRating.rating =
                searchRestaurant.rating!!.trim().toFloat()
            //Rating Count
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListRateText.text =
                "(${searchRestaurant.totalRating!!.trim()})"
            //Address
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListAddress.text =
                searchRestaurant.address!!.trim()
            //Near By
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListNearBy.text =
                searchRestaurant.distance!!.trim()
            //Egg visibility
            holder.rowSearchRestaurantListBinding.ivSearchRestaurantListEggTag.visibility =
                if (searchRestaurant.isEgg == 1) View.VISIBLE else View.GONE
            //NonVeg visibility
            holder.rowSearchRestaurantListBinding.ivSearchRestaurantListNonVegTag.visibility =
                if (searchRestaurant.isNonVeg == 1) View.VISIBLE else View.GONE
            //Veg visibility
            holder.rowSearchRestaurantListBinding.ivSearchRestaurantListVegTag.visibility =
                if (searchRestaurant.isVeg == 1) View.VISIBLE else View.GONE
            //Open Close
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListOpenClose.text =
                if (searchRestaurant.isOpen == 1) "open" else "close"
            holder.rowSearchRestaurantListBinding.tvSearchRestaurantListOpenClose.background =
                ContextCompat.getDrawable(
                    context,
                    if (searchRestaurant.isOpen == 1) R.drawable.bg_moutain_meadow_circle else R.drawable.bg_red_circle
                )
            //Holder View onClick
            holder.view.setOnClickListener {
                listener.onClickListener(holder.itemView, position, searchRestaurant)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return searchRestaurantList.size
    }

    fun addSearchRestaurantList(alRestaurantList: ArrayList<SearchRestaurantData>) {
        try {
            if (alRestaurantList.isNotEmpty()) {
                this.searchRestaurantList.clear()
                this.searchRestaurantListFilter.clear()
                this.searchRestaurantList = alRestaurantList
                this.searchRestaurantListFilter.addAll(alRestaurantList)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun clearSearchRestaurantList(){
        try {
            this.searchRestaurantList.clear()
            this.searchRestaurantListFilter.clear()
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filter(searchText: String) {
        try {
            searchRestaurantList.clear()
            if (searchText.isEmpty()) {
                searchRestaurantList.addAll(searchRestaurantListFilter)
            } else {
                for (i in searchRestaurantListFilter.indices) {
                    if (searchRestaurantListFilter[i].title!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        searchRestaurantListFilter[i].totalRating!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        searchRestaurantListFilter[i].rating!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        searchRestaurantListFilter[i].address!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        searchRestaurantListFilter[i].distance!!.lowercase(Locale.getDefault())
                            .contains(searchText)
                    ) {
                        searchRestaurantList.add(searchRestaurantListFilter[i])
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    inner class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val rowSearchRestaurantListBinding: RowSearchRestaurentListBinding =
            DataBindingUtil.bind(itemView)!!
    }
}