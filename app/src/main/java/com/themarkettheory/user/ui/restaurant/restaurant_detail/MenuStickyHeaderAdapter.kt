package com.themarkettheory.user.ui.restaurant.restaurant_detail

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.sathvik.stickyheaderexample.StickyHeaderItemDecoration
import com.squareup.picasso.Picasso
import com.themarkettheory.user.R
import com.themarkettheory.user.databinding.RowMenuContainerBinding
import com.themarkettheory.user.databinding.RowMenuHeaderCategoryBinding
import com.themarkettheory.user.interfaces.ListClickListener
import java.util.*
import kotlin.collections.ArrayList

//Sticky Header
const val TYPE_HEADER = 0
const val TYPE_ITEM = 1

class MenuStickyHeaderAdapter(
    private val context: Context,
    private val listener: ListClickListener,
    private val rowListener: ListClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(),
    StickyHeaderItemDecoration.StickyHeaderInterface {
    //ArrayList for child menu
    var menuList = ArrayList<clsMenuList>()
    private val menuListFilter = ArrayList<clsMenuList>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_HEADER) {
            ViewHolderHeader(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_menu_header_category,
                    FrameLayout(parent.context),
                    false
                )
            )
        } else {
            ViewHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.row_menu_container,
                    FrameLayout(parent.context),
                    false
                )
            )
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        try {
            val menuX = menuList[position]
            if (holder is ViewHolder) {
                //Food Image
                if (!menuX.image.isNullOrEmpty()) {
                    holder.rowMenuContainerBinding.ivMenuListImage.visibility = View.VISIBLE
                    Picasso.get().load(menuX.image)
                        .error(R.drawable.ic_camera)
                        .into(holder.rowMenuContainerBinding.ivMenuListImage)
                } else {
                    holder.rowMenuContainerBinding.ivMenuListImage.visibility = View.GONE
                }
                //Veg or Non-veg or Egg
                holder.rowMenuContainerBinding.ivMenuListVegNonVeg.setImageDrawable(
                    when (menuX.foodType) {
                        1 -> ContextCompat.getDrawable(context, R.drawable.ic_veg_icon)
                        2 -> ContextCompat.getDrawable(context, R.drawable.ic_non_veg_icon)
                        else -> ContextCompat.getDrawable(context, R.drawable.food_type_egg)
                    }
                )
                //Spicy or Non-Spicy
                holder.rowMenuContainerBinding.ivMenuListSpicy.setImageDrawable(
                    if (menuX.isSpicy == 1) ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_spicy
                    )
                    else ContextCompat.getDrawable(context, R.drawable.ic_not_spicy)
                )
                //Points
                holder.rowMenuContainerBinding.tvMenuListPointLabel.visibility =
                    if (menuX.point > 0) View.VISIBLE else View.GONE
                holder.rowMenuContainerBinding.tvMenuListPoint.visibility =
                    if (menuX.point > 0) View.VISIBLE else View.GONE
                holder.rowMenuContainerBinding.tvMenuListPoint.apply {
                    text = menuX.point.toString().trim()
                    visibility =
                        if (menuX.point.toString().trim()
                                .isEmpty() || menuX.point == 0
                        ) View.GONE else View.VISIBLE
                }
                //Line Divider
                holder.rowMenuContainerBinding.viewMenuListDivider.visibility =
                    if (menuX.preparingTime.isEmpty() ||
                        menuX.preparingTime.trim() == "0 mins"
                    ) View.GONE else View.VISIBLE
                //Time
                holder.rowMenuContainerBinding.tvMenuListTime.apply {
                    text = menuX.preparingTime.trim()
                    visibility =
                        if (menuX.preparingTime.trim().isEmpty() ||
                            menuX.preparingTime.trim() == "0 mins"
                        ) View.GONE else View.VISIBLE
                }
                //Food Name
                if (!menuX.dishQty.isEmpty() && !menuX.unit.isEmpty()) {
                    holder.rowMenuContainerBinding.tvMenuListItemName.text =
                        "${menuX.title.trim()} (${menuX.dishQty.trim()} ${menuX.unit.trim()})"
                } else {
                    holder.rowMenuContainerBinding.tvMenuListItemName.text = menuX.title.trim()
                }
                //Food Category
                holder.rowMenuContainerBinding.tvMenuListCategory.text = menuX.categoryName.trim()
                //Food Final Price
                holder.rowMenuContainerBinding.tvMenuListFinalPrice.text =
                    "${menuX.currency.trim()}${menuX.finalPrice.toString().trim()}"
                //Food Actual Price
                if (menuX.finalPrice != menuX.actualPrice) {
                    holder.rowMenuContainerBinding.tvMenuListActualPrice.visibility = View.VISIBLE
                    holder.rowMenuContainerBinding.tvMenuListActualPrice.text =
                        "${menuX.currency.trim()}${menuX.actualPrice.toString().trim()}"
                    holder.rowMenuContainerBinding.tvMenuListActualPrice.paintFlags =
                        Paint.STRIKE_THRU_TEXT_FLAG
                } else {
                    holder.rowMenuContainerBinding.tvMenuListActualPrice.visibility = View.GONE
                }
                //Button Add Click Listener
                holder.rowMenuContainerBinding.tvMenuListAdd.setOnClickListener {
                    listener.onClickListener(holder.view, position, menuX)
                }

                //Button Background and text properties
                if (menuX.isAdded) {
                    //Background
                    holder.rowMenuContainerBinding.tvMenuListAdd.background =
                        ContextCompat.getDrawable(context, R.drawable.ic_button_bg)
                    //Text
                    holder.rowMenuContainerBinding.tvMenuListAdd.text = "added"
                    //Text Color
                    holder.rowMenuContainerBinding.tvMenuListAdd.setTextColor(
                        ContextCompat.getColor(context, R.color.white)
                    )
                } else {
                    //Background
                    holder.rowMenuContainerBinding.tvMenuListAdd.background =
                        ContextCompat.getDrawable(context, R.drawable.bg_button_grey_border)
                    //Text
                    holder.rowMenuContainerBinding.tvMenuListAdd.text = "+ add"
                    //Text Color
                    holder.rowMenuContainerBinding.tvMenuListAdd.setTextColor(
                        ContextCompat.getColor(context, R.color.myMountainMeadow)
                    )
                }

                //Row click listener to Hide Short Menu List
                holder.view.setOnClickListener {
                    rowListener.onClickListener(holder.view, position, menuX)
                }
            } else if (holder is ViewHolderHeader) {
                holder.rowMenuHeaderCategoryBinding.menuCategoryname.text = menuX.title.trim()

                //Row header click listener to Hide Short Menu List
                holder.headerView.setOnClickListener {
                    rowListener.onClickListener(holder.headerView, position, menuX)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getItemCount(): Int {
        return menuList.size
    }

    override fun getItemViewType(position: Int): Int {
        return if (menuList[position].isHeader) {
            TYPE_HEADER
        } else {
            TYPE_ITEM
        }
    }

    @JvmName("setMenuList1")
    fun setMenuList(alMenuList: ArrayList<clsMenuList>) {
        try {
            if (alMenuList.isNotEmpty()) {
                this.menuList.clear()
                this.menuListFilter.clear()
                this.menuList = alMenuList
                this.menuListFilter.addAll(alMenuList)
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
                menuList.addAll(menuListFilter)
            } else {
                for (s in menuListFilter.indices) {
                    if (
                        menuListFilter[s].title.lowercase(Locale.getDefault())
                            .contains(searchText) ||
                        menuListFilter[s].categoryName.lowercase(Locale.getDefault())
                            .contains(searchText)
                    ) {
                        menuList.add(menuListFilter[s])
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            notifyDataSetChanged()
        }
    }

    //region Header Menu
    override fun getHeaderPositionForItem(itemPosition: Int): Int {
        var headerPosition = 0
        var position = itemPosition
        do {
            if (this.isHeader(position)) {
                headerPosition = position
                break
            }
            position -= 1
        } while (position >= 0)
        return headerPosition
    }

    override fun getHeaderLayout(headerPosition: Int): Int {
        return R.layout.row_menu_header_category
    }

    override fun bindHeaderData(header: View, headerPosition: Int) {
        ((header as ConstraintLayout).getChildAt(0) as TextView).text =
            menuList[headerPosition].title.trim()
    }

    override fun isHeader(itemPosition: Int): Boolean {
        return menuList[itemPosition].isHeader
    }
    //endregion

    //region Menu List ViewHolder
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val view = itemView
        val rowMenuContainerBinding: RowMenuContainerBinding = DataBindingUtil.bind(itemView)!!
    }
    //endregion

    //region Menu Header ViewHolder
    inner class ViewHolderHeader(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val headerView = itemView
        val rowMenuHeaderCategoryBinding: RowMenuHeaderCategoryBinding =
            DataBindingUtil.bind(itemView)!!
    }
    //endregion

}