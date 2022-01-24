package com.themarkettheory.user.ui.restaurant.restaurant_detail

import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMenuContainerBinding
import com.themarkettheory.user.interfaces.ListClickListener
import java.util.*
import kotlin.collections.ArrayList

class MenuSearchAdapter(
    private val context: Context,
    private val listener: ListClickListener,
    private val rowListener: ListClickListener
) : RecyclerView.Adapter<MenuSearchAdapter.Holder>() {
    var menuList = ArrayList<clsMenuList>()
    var menuFilterList = ArrayList<clsMenuList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.row_menu_container,
                FrameLayout(parent.context),
                false
            )
        )
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val menuX = menuList[position]
            //Food Image
            if (!menuX.image.isNullOrEmpty()) {
                holder.bindingMenuSearch.ivMenuListImage.visibility = View.VISIBLE
                Picasso.get().load(menuX.image)
                    .error(R.drawable.ic_camera)
                    .into(holder.bindingMenuSearch.ivMenuListImage)
            } else {
                holder.bindingMenuSearch.ivMenuListImage.visibility = View.GONE
            }
            //Veg or Non-veg or Egg
            holder.bindingMenuSearch.ivMenuListVegNonVeg.setImageDrawable(
                when (menuX.foodType) {
                    1 -> ContextCompat.getDrawable(context, R.drawable.ic_veg_icon)
                    2 -> ContextCompat.getDrawable(context, R.drawable.ic_non_veg_icon)
                    else -> ContextCompat.getDrawable(context, R.drawable.food_type_egg)
                }
            )
            //Spicy or Non-Spicy
            holder.bindingMenuSearch.ivMenuListSpicy.setImageDrawable(
                if (menuX.isSpicy == 1) ContextCompat.getDrawable(
                    context,
                    R.drawable.ic_spicy
                )
                else ContextCompat.getDrawable(context, R.drawable.ic_not_spicy)
            )
            //Points
            holder.bindingMenuSearch.tvMenuListPointLabel.visibility =
                if (menuX.point > 0) View.VISIBLE else View.GONE
            holder.bindingMenuSearch.tvMenuListPoint.visibility =
                if (menuX.point > 0) View.VISIBLE else View.GONE
            holder.bindingMenuSearch.tvMenuListPoint.apply {
                text = menuX.point.toString().trim()
                visibility =
                    if (menuX.point.toString().trim()
                            .isEmpty() || menuX.point == 0
                    ) View.GONE else View.VISIBLE
            }
            //Line Divider
            holder.bindingMenuSearch.viewMenuListDivider.visibility =
                if (menuX.preparingTime.isEmpty() ||
                    menuX.preparingTime.trim() == "0 mins"
                ) View.GONE else View.VISIBLE
            //Time
            holder.bindingMenuSearch.tvMenuListTime.apply {
                text = menuX.preparingTime.trim()
                visibility =
                    if (menuX.preparingTime.trim().isEmpty() ||
                        menuX.preparingTime.trim() == "0 mins"
                    ) View.GONE else View.VISIBLE
            }
            //Food Name
            if (!menuX.dishQty.isEmpty() && !menuX.unit.isEmpty()) {
                holder.bindingMenuSearch.tvMenuListItemName.text =
                    "${menuX.title.trim()} (${menuX.dishQty.trim()} ${menuX.unit.trim()})"
            } else {
                holder.bindingMenuSearch.tvMenuListItemName.text = menuX.title.trim()
            }
            //Food Category
            holder.bindingMenuSearch.tvMenuListCategory.text = menuX.categoryName.trim()
            //Food Final Price
            holder.bindingMenuSearch.tvMenuListFinalPrice.text =
                "${menuX.currency.trim()}${menuX.finalPrice.toString().trim()}"
            //Food Actual Price
            if (menuX.finalPrice != menuX.actualPrice) {
                holder.bindingMenuSearch.tvMenuListActualPrice.visibility = View.VISIBLE
                holder.bindingMenuSearch.tvMenuListActualPrice.text =
                    "${menuX.currency.trim()}${menuX.actualPrice.toString().trim()}"
                holder.bindingMenuSearch.tvMenuListActualPrice.paintFlags =
                    Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                holder.bindingMenuSearch.tvMenuListActualPrice.visibility = View.GONE
            }
            //Button Add Click Listener
            holder.bindingMenuSearch.tvMenuListAdd.setOnClickListener {
                listener.onClickListener(holder.view, position, menuX)
            }

            //Button Background and text properties
            if (menuX.isAdded) {
                //Background
                holder.bindingMenuSearch.tvMenuListAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                //Text
                holder.bindingMenuSearch.tvMenuListAdd.text = "added"
                //Text Color
                holder.bindingMenuSearch.tvMenuListAdd.setTextColor(
                    ContextCompat.getColor(context, R.color.white)
                )
            } else {
                //Background
                holder.bindingMenuSearch.tvMenuListAdd.background =
                    ContextCompat.getDrawable(context, R.drawable.bg_button_grey_border)
                //Text
                holder.bindingMenuSearch.tvMenuListAdd.text = "+ add"
                //Text Color
                holder.bindingMenuSearch.tvMenuListAdd.setTextColor(
                    ContextCompat.getColor(context, R.color.myMountainMeadow)
                )
            }

            //Row click listener to Hide Short Menu List
            holder.view.setOnClickListener {
                rowListener.onClickListener(holder.view, position, menuX)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    fun setMenuSearchList(alMenuList: ArrayList<clsMenuList>) {
        try {
            if (alMenuList.isNotEmpty()) {
                menuList.clear()
                menuFilterList.clear()
                menuList = alMenuList
                menuFilterList.addAll(alMenuList)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    fun setMenuAdded(position: Int, isAdded: Boolean) {
        try {
            if (menuList.isNotEmpty()) {
                menuList[position].isAdded = isAdded
                notifyDataSetChanged()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun filter(searchText: String) {
        try {
            menuList.clear()
            if (searchText.isEmpty()) {
                menuList.addAll(menuFilterList)
            } else {
                for (s in menuFilterList.indices) {
                    if (
                        menuFilterList[s].title.lowercase(Locale.getDefault())
                            .contains(searchText)
                    ) {
                        menuList.add(menuFilterList[s])
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
        val bindingMenuSearch = DataBindingUtil.bind<RowMenuContainerBinding>(itemView)!!
    }
}