package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.PopupWindow
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.newmodels.home.NewHomeRes
import com.themarkettheory.user.ui.dialog.dialogPopupList.AdapterDialogPopupList
import com.themarkettheory.user.ui.dialog.dialogPopupList.clsPopupDialogList
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.AllRecommendedListAdapter
import com.themarkettheory.user.ui.main.adapter.HomeCategoriesAdapter
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.activity_recommended.*
import kotlinx.android.synthetic.main.toolbar.*


class RecommendedActivity : BaseActivity(), View.OnClickListener {
    lateinit var homeViewModel: HomeViewModel
    var selectedCategoryId = ""
    private lateinit var newHomeRes: NewHomeRes

    private lateinit var homeCategoriesAdapter: HomeCategoriesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recommended)

        newHomeRes = gson.fromJson(
            myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbNewHomeRes),
            NewHomeRes::class.java
        )

        tvTitle.text = getString(R.string.recommended)
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        getRecommendedResponse()

        ivBack.setOnClickListener(this)
        cvFoodType.setOnClickListener {
            displayPopupList(true)
        }
        cvFoodSort.setOnClickListener {
            displayPopupList(false)
        }
        getList()
        /*val responseHome = Gson().fromJson(
            PubFun.readAssetsFile(Config.mainActivity!!, "HomeRes.json"),
            NewHomeRes::class.java
        )
        val recommendedListAdapter =
            AllRecommendedListAdapter(this, responseHome.newHomeData?.restaurants!!) {
                homeViewModel.favourite_service(it.id.toString())
            }
        rvRecommended.adapter = recommendedListAdapter
        rvRecommended.layoutManager = LinearLayoutManager(this)*/

    }

    private fun displayPopupList(isFoodType: Boolean) {
        lylFoodType.visibility = if (isFoodType) View.VISIBLE else View.GONE
        lylFoodSort.visibility = if (isFoodType) View.GONE else View.VISIBLE

        val arrayListItems = ArrayList<clsPopupDialogList>()
        if (isFoodType) {
            arrayListItems.add(clsPopupDialogList("Veg", ""))
            arrayListItems.add(clsPopupDialogList("Non Veg", ""))
            arrayListItems.add(clsPopupDialogList("Egg", ""))
        } else {
            arrayListItems.add(clsPopupDialogList("All", ""))
            arrayListItems.add(clsPopupDialogList("Recommended by TMT", ""))
            arrayListItems.add(clsPopupDialogList("Rating: High to Low", ""))
            arrayListItems.add(clsPopupDialogList("Most Favorite", ""))
            arrayListItems.add(clsPopupDialogList("Near By", ""))
            arrayListItems.add(clsPopupDialogList("Ascending", ""))
        }
        val listener = object : ListClickListener {
            override fun onClickListener(view: View, pos: Int, objects: Any) {
                val popupDialogList = objects as clsPopupDialogList
                if (isFoodType) {
                    tvFoodType.text = popupDialogList.strTitle
                    lylFoodType.visibility = View.GONE
                } else {
                    tvSort.text = popupDialogList.strTitle
                    lylFoodSort.visibility = View.GONE
                }
            }
        }
        val adapterPopupList = AdapterDialogPopupList(listener)
        adapterPopupList.addPopupDataItems(arrayListItems)
        if (isFoodType) {
            rvFoodType.apply {
                adapter = adapterPopupList
                layoutManager = LinearLayoutManager(this@RecommendedActivity)
            }
        } else {
            rvFoodSort.apply {
                adapter = adapterPopupList
                layoutManager = LinearLayoutManager(this@RecommendedActivity)
            }
        }
    }

    private fun displayPopupWindow(anchorView: View) {
        val popup = PopupWindow(this)
        val layout: View = layoutInflater.inflate(R.layout.popup_content_for_food_type, null)
        val tvVeg: TextView = layout.findViewById<TextView>(R.id.tvVeg)
        val tvNonVeg: TextView = layout.findViewById<TextView>(R.id.tvNonVeg)
        tvVeg.setOnClickListener {
            tvFoodType.setText("Veg")
            popup.dismiss()
        }
        tvNonVeg.setOnClickListener {
            tvFoodType.setText("Non Veg")
            popup.dismiss()
        }
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0)

        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Show anchored to button
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
    }

    private fun displayPopupWindowForShort(anchorView: View) {
        val popup = PopupWindow(this)
        val layout: View = layoutInflater.inflate(R.layout.popup_content_for_short, null)
        val tvAll: TextView = layout.findViewById<TextView>(R.id.tvAll)
        val tvRecommend: TextView = layout.findViewById<TextView>(R.id.tvRecommend)
        val tvRattingHToL: TextView = layout.findViewById<TextView>(R.id.tvRattingHToL)
        val tvMostFavorite: TextView = layout.findViewById<TextView>(R.id.tvMostFavorite)
        val tvNearBy: TextView = layout.findViewById<TextView>(R.id.tvNearBy)
        val tvAscending: TextView = layout.findViewById<TextView>(R.id.tvAscending)
        tvAll.setOnClickListener {
            tvSort.setText("All")
            popup.dismiss()
        }
        tvRecommend.setOnClickListener {
            tvSort.setText("Recommend")
            popup.dismiss()
        }
        tvRattingHToL.setOnClickListener {
            tvSort.setText("Rating")
            popup.dismiss()
        }
        tvMostFavorite.setOnClickListener {
            tvSort.setText("Favorite")
            popup.dismiss()
        }
        tvNearBy.setOnClickListener {
            tvSort.setText("By")
            popup.dismiss()
        }
        tvAscending.setOnClickListener {
            tvSort.setText("Ascending")
            popup.dismiss()
        }
        popup.showAtLocation(anchorView, Gravity.CENTER, 0, 0);

        popup.contentView = layout
        // Set content width and height
        popup.height = WindowManager.LayoutParams.WRAP_CONTENT
        popup.width = WindowManager.LayoutParams.WRAP_CONTENT
        // Closes the popup window when touch outside of it - when looses focus
        popup.isOutsideTouchable = true
        popup.isFocusable = true
        // Show anchored to button
        popup.setBackgroundDrawable(BitmapDrawable())
        popup.showAsDropDown(anchorView)
    }

    private fun getList() {
        val subCategories = ArrayList<Category>()
        for (i in newHomeRes.data!!.categories!![0].subcategory!!.indices) {
            val category = Category()
            category.id = newHomeRes.data!!.categories!![0].subcategory!![i].id
            category.name = newHomeRes.data!!.categories!![0].subcategory!![i].name

            /*if (category.name!! == Config.subCategory0IndexRestaurantOldName) {
                category.name = Config.subCategory0IndexRestaurantNewName
            }*/
            subCategories.add(category)
        }

        homeCategoriesAdapter = HomeCategoriesAdapter(this, subCategories) {
//                    setFragment(RestaurantFragment.newInstance("", ""))
            if (selectedCategoryId != it.id.toString()) {
                selectedCategoryId = it.id.toString()
            }
        }

        rvRecommendCategory.setLayoutManager(
            LinearLayoutManager(
                this,
                LinearLayoutManager.HORIZONTAL,
                false
            )
        )
        rvRecommendCategory.adapter = homeCategoriesAdapter

    }

    private fun getRecommendedResponse() {
        homeViewModel.isLoading.observe(this, Observer {
            if (it!!) {
                Utils.showProgress(this)
            } else {
                Utils.hideProgress(this)
            }
        })

        homeViewModel.view_all_recommanded()

        homeViewModel.responseAllRecommended.observe(this, Observer {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it.message!!.trim())
                1 -> {
                    val recommendedListAdapter = AllRecommendedListAdapter(this, it.data?.list!!) {
                        homeViewModel.favourite_service(it.id.toString())
                    }
                    rvRecommended.adapter = recommendedListAdapter
                    rvRecommended.layoutManager = LinearLayoutManager(this)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@RecommendedActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.recommended)
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.apply {
                    visibility = View.GONE
                    post(object : Runnable {
                        override fun run() {
                            if (i == 0) {
                                myDialog.dismiss()
                            } else {
                                i--
                                postDelayed(this, 500)
                            }
                        }
                    })
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivBack -> onBackPressed()
        }
    }

    fun getRes(): String {
        return "{\n" +
                "\t\"data\": [{\n" +
                "\t\t\"id\": 1,\n" +
                "\t\t\"name\": \"All\",\n" +
                "\t\t\"status\": 1,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 2,\n" +
                "\t\t\"name\": \"Gujarati\",\n" +
                "\t\t\"status\": 2,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 3,\n" +
                "\t\t\"name\": \"Panjabi\",\n" +
                "\t\t\"status\": 3,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 4,\n" +
                "\t\t\"name\": \"Chinees\",\n" +
                "\t\t\"status\": 4,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 5,\n" +
                "\t\t\"name\": \"South\",\n" +
                "\t\t\"status\": 1,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 6,\n" +
                "\t\t\"name\": \"North\",\n" +
                "\t\t\"status\": 2,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 7,\n" +
                "\t\t\"name\": \"Rajeshtani\",\n" +
                "\t\t\"status\": 3,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}, {\n" +
                "\t\t\"id\": 8,\n" +
                "\t\t\"name\": \"Vadapau\",\n" +
                "\t\t\"status\": 4,\n" +
                "\t\t\"icon\": \"\",\n" +
                "\t\t\"createdAt\": \"\",\n" +
                "\t\t\"updatedAt\": \"\"\n" +
                "\t}]\n" +
                "}"
    }
}