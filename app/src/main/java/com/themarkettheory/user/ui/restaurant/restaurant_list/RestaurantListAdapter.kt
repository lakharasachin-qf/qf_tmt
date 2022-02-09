package com.themarkettheory.user.ui.restaurant.restaurant_list

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowRestaurantListBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.map.Restaurant
import java.util.*
import kotlin.collections.ArrayList

class RestaurantListAdapter(
    private val context: RestaurantListActivity,
    private val listener: ListClickListener
) : RecyclerView.Adapter<RestaurantListAdapter.Holder>() {
    //General
    private var restaurantList = ArrayList<Restaurant>()
    private var restaurantListFilter = ArrayList<Restaurant>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val itemView = LayoutInflater.from(parent.context).inflate(
            R.layout.row_restaurant_list,
            FrameLayout(parent.context),
            false
        )
        return Holder(itemView)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val restaurant = restaurantList[position]
            //Restaurant Image
            if (restaurant.image.isNullOrEmpty()) {
                holder.rowRestaurantListBinding!!.ivRestaurantListImage.setImageDrawable(
                    ContextCompat.getDrawable(context, R.drawable.ic_camera)
                )
            } else {
                Picasso.get().load(restaurant.image)
                    .error(R.drawable.ic_camera)
                    .into(
                        holder.rowRestaurantListBinding.ivRestaurantListImage
                    )
            }

            //Live Deal
            holder.rowRestaurantListBinding.tvRestaurantListLiveDealTitle.visibility =
                if (restaurant.isLiveDeal!! && (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromLiveDealSeeAll))
                    View.VISIBLE else View.GONE

            if (restaurant.isLiveDeal!! && (Config.isRestaurantListOpeningFrom == Config.restaurantListOpeningFromLiveDealSeeAll))
            {
                animateLiveDeal(holder)
            }
            //Title
            holder.rowRestaurantListBinding.tvRestaurantListName.text = restaurant.title!!.trim()
            //Star Rating
            holder.rowRestaurantListBinding.RestaurantListRating.rating =
                restaurant.rating!!.trim().toFloat()
            //Rating Count
            holder.rowRestaurantListBinding.tvRestaurantListRateText.text =
                "(${restaurant.totalRating!!.trim()})"
            //Address
            holder.rowRestaurantListBinding.tvRestaurantListAddress.text =
                restaurant.address!!.trim()
            //Near By
            holder.rowRestaurantListBinding.tvRestaurantListNearBy.text =
                restaurant.distance!!.trim()
            //Egg visibility
            holder.rowRestaurantListBinding.ivRestaurantListEggTag.visibility =
                if (restaurant.isEgg == 1) View.VISIBLE else View.GONE
            //NonVeg visibility
            holder.rowRestaurantListBinding.ivRestaurantListNonVegTag.visibility =
                if (restaurant.isNonVeg == 1) View.VISIBLE else View.GONE
            //Veg visibility
            holder.rowRestaurantListBinding.ivRestaurantListVegTag.visibility =
                if (restaurant.isVeg == 1) View.VISIBLE else View.GONE
            //Open Close
            holder.rowRestaurantListBinding.tvRestaurantListOpenClose.text =
                if (restaurant.isOpen == 1) "open" else "close"
            holder.rowRestaurantListBinding.tvRestaurantListOpenClose.background =
                ContextCompat.getDrawable(
                    context,
                    if (restaurant.isOpen == 1) R.drawable.bg_moutain_meadow_circle else R.drawable.bg_red_circle
                )
            //Holder View onClick
            holder.view.setOnClickListener {
                listener.onClickListener(holder.itemView, position, restaurant)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    private fun animateLiveDeal(holder: Holder) {
        try {
            //Animate Live Deal Background
            val anim = ObjectAnimator.ofInt(
                holder.rowRestaurantListBinding.tvRestaurantListLiveDealTitle, "backgroundColor",
                ContextCompat.getColor(context, R.color.myMountainMeadow),
                ColorUtils.setAlphaComponent(
                    ContextCompat.getColor(
                        context,
                        R.color.myMountainMeadow
                    ), 85
                )
            )
            anim.apply {
                duration = 500
                setEvaluator(ArgbEvaluator())
                repeatMode = ValueAnimator.REVERSE
                repeatCount = Animation.INFINITE
                start()
            }

            //Animate Live Deal Text
            val animText = ObjectAnimator.ofInt(
                holder.rowRestaurantListBinding.tvRestaurantListLiveDealTitle, "textColor",
                ContextCompat.getColor(context, R.color.myWhite),
                ColorUtils.setAlphaComponent(ContextCompat.getColor(context, R.color.myWhite), 85)
            )
            animText.apply {
                duration = 500
                setEvaluator(ArgbEvaluator())
                repeatMode = ValueAnimator.REVERSE
                repeatCount = Animation.INFINITE
                start()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setRestaurantList(alRestaurantList: ArrayList<Restaurant>) {
        try {
            if (alRestaurantList.isNotEmpty()) {
                this.restaurantList.clear()
                this.restaurantListFilter.clear()
                this.restaurantList = alRestaurantList
                this.restaurantListFilter.addAll(alRestaurantList)
            }
            notifyDataSetChanged()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filter(searchText: String) {
        try {
            restaurantList.clear()
            if (searchText.isEmpty()) {
                restaurantList.addAll(restaurantListFilter)
            } else {
                for (i in restaurantListFilter.indices) {
                    if (restaurantListFilter[i].title!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        restaurantListFilter[i].totalRating!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        restaurantListFilter[i].rating!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        restaurantListFilter[i].address!!.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        restaurantListFilter[i].distance!!.lowercase(Locale.getDefault())
                            .contains(searchText)
                    ) {
                        restaurantList.add(restaurantListFilter[i])
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
        val rowRestaurantListBinding: RowRestaurantListBinding =
            DataBindingUtil.bind(itemView)!!
    }
}