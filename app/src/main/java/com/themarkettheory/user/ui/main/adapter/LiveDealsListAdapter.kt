package com.themarkettheory.user.ui.main.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.core.content.ContextCompat
import androidx.core.graphics.ColorUtils
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowLiveDealBinding
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.interfaces.FavoriteListener
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.home.Restaurant
import com.themarkettheory.user.ui.restaurant.LiveDealsActivity
import java.util.*
import kotlin.collections.ArrayList

class LiveDealsListAdapter(
    val context: Context,
    private val liveDealListener: ListClickListener,
    val listener: (Restaurant) -> Unit
) : RecyclerView.Adapter<LiveDealsListAdapter.ViewHolder>() {

    private val data = ArrayList<Restaurant>()
    private lateinit var favoriteListener: FavoriteListener

    private var currentPosition: Int = 0
    var flags = arrayOfNulls<Boolean>(4)

    init {
        Arrays.fill(flags, false)
    }

    fun addLiveDeals(liveDealsList: ArrayList<Restaurant>) {
        this.data.clear()
        this.data.addAll(liveDealsList)
        notifyDataSetChanged()
    }

    fun addFavoriteListener(listener: FavoriteListener) {
        this.favoriteListener = listener
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        val binding: RowLiveDealBinding =
            DataBindingUtil.inflate(layoutInflater, R.layout.row_live_deal, p0, false)

        return ViewHolder(binding.root)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (data[position].isLiveDeal!!) {
            holder.itemView.visibility = View.VISIBLE
            holder.binding?.objLiveDeal = data[position]

            holder.binding!!.rating.rating = data[position].rating!!.toFloat()
            if (data[getItemViewType(position)].isFavourite == 0) {
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
            } else {
                holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
            }

            holder.binding!!.ivFavorite.setOnClickListener {
                if (PubFun.isInternetConnection(context)) {
                    if (data[getItemViewType(position)].isFavourite == 1) {
                        holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_unselected)
                    } else {
                        holder.binding!!.ivFavorite.setImageResource(R.drawable.ic_like_selected)
                    }

                    favoriteListener.onMarkFavorite(
                        data[position].title!!,
                        data[position].isFavourite!!.xor(1)
                    )

                    data[getItemViewType(position)].isFavourite =
                        data[getItemViewType(position)].isFavourite!!.xor(1)
                    notifyDataSetChanged()
                }
                listener(data[getItemViewType(position)])
            }

            Glide.with(context).load(data[getItemViewType(position)].image)
                .error(R.drawable.ic_camera)
                .into(holder.binding!!.iv)

            //Animate Live Deal Tag
            val anim = ObjectAnimator.ofInt(
                holder.binding!!.tvRowLiveDealTitle, "backgroundColor",
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

            val animText = ObjectAnimator.ofInt(
                holder.binding!!.tvRowLiveDealTitle, "textColor",
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

            holder.binding!!.ll.setOnClickListener {
                /*Commented by Milan on 20-Oct-2021 as requested by Amar bhai*/
                /*Config.isMenuFragmentComingFrom = ""
                Config.isBookingDetailOpeningFrom = Config.isBookingDetailOpeningFromLiveDealListAdapter
                context.startActivity(
                    Intent(context, VendorDetailActivity::class.java)
                        .putExtra("category", data[getItemViewType(position)].categoryId.toString())
                        .putExtra("serviceId", data[getItemViewType(position)].id.toString())
                        .putExtra("vendorTitle", data[getItemViewType(position)].title)
                )*/

                /*Added by Milan on 20-Oct-2021 as requested by Amar bhai */
                /*Config.vendorDetailServiceId = data[getItemViewType(position)].id!!.toString().trim()
                context.startActivity(Intent(context, LiveDealsActivity::class.java))*/
                liveDealListener.onClickListener(holder.itemView, position, data[position])
            }

            holder.binding!!.tvRowLiveDealTitle.setOnClickListener {
                liveDealListener.onClickListener(holder.itemView, position, data[position])
            }
        } else {
            holder.itemView.visibility = View.GONE
        }
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        internal var binding: RowLiveDealBinding? = null

        init {
            binding = DataBindingUtil.bind<RowLiveDealBinding>(itemView)

        }
    }

}