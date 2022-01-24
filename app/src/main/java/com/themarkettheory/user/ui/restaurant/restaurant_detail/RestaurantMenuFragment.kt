package com.themarkettheory.user.ui.restaurant.restaurant_detail

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import com.sathvik.stickyheaderexample.StickyHeaderItemDecoration
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.menulist.NewMenuListRes
import com.themarkettheory.user.ui.dialog.dialogPopupList.AdapterDialogPopupList
import com.themarkettheory.user.ui.dialog.dialogPopupList.clsPopupDialogList
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.restaurant.MyBucketActivity
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.fragment_restaurant_menu.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.LinkedHashSet


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [MenuFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantMenuFragment : Fragment(), View.OnClickListener {
    private var param1: String? = null
    private var param2: String? = null

    /*ViewModel*/
    private lateinit var vendorDetailViewModel: VendorDetailViewModel

    //Adapters
    private lateinit var adapterMenu: MenuStickyHeaderAdapter
    private lateinit var adapterMenuSearch: MenuSearchAdapter
    private lateinit var llmMenuListShort: LinearLayoutManager
    private var addedItemList = ArrayList<clsMenuList>()
    private var totalPoints = 0
    private val menuXList = ArrayList<clsMenuList>()
    private val menuXSearchList = ArrayList<clsMenuList>()

    private lateinit var myRoomDatabase: MyRoomDatabase

    private lateinit var menuX: clsMenuList
    private var menuPosition = 0
    private var bookingId = "0" //By Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_restaurant_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            //Initialize Menu
            menuInit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(requireActivity())
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Menu List"
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
                                postDelayed(this, 1000)
                            }
                        }
                    })
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callMenuApi() {
        try {
            if (PubFun.isInternetConnection(requireActivity())) {
                vendorDetailViewModel.menus(Config.vendorDetailServiceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAddMenuCart(menuId: String, qty: String) {
        try {
            if (PubFun.isInternetConnection(requireActivity())) {
                vendorDetailViewModel.menu_add_cart(
                    Config.vendorDetailServiceId,
                    menuId,
                    "0",
                    qty,
                    bookingId,
                    0,
                    0
                )
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun menuInit() {
        try {
            myRoomDatabase = MyRoomDatabase.getDB(requireActivity())!!
            if (Config.isMenuFragmentComingFrom == Config.isMenuFragmentComingFromBookingTable) {
                bookingId =
                    myRoomDatabase.daoConfig().selectConfigTableByField(Config.dbTableBookingId)!!
            } else {
                bookingId = "0"
            }

            //Hiding Short Pop-up menu
            lylMenuListShort.visibility = View.GONE

            //view model Initialization
            vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)

            //Call menu api
            callMenuApi()

            //Menu List Response
            vendorDetailViewModel.responseGetMenus.observe(viewLifecycleOwner, Observer {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> populateMenuList(it)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            //region TextInputEditText Menu Search
            etMenuSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    try {
                        if (s.toString().trim().isNotEmpty()) {
                            rvMenuSearchList.visibility = View.VISIBLE
                            rvMenuList.visibility = View.GONE
                            adapterMenuSearch.filter(
                                s.toString().trim().lowercase(Locale.getDefault())
                            )
                        } else {
                            PubFun.hideKeyboard(requireActivity())
                            rvMenuSearchList.visibility = View.GONE
                            rvMenuList.visibility = View.VISIBLE
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }

            })
            //endregion

            //Menu Cart Response
            vendorDetailViewModel.responseMenuAddCart.observe(viewLifecycleOwner, Observer {
                try {
                    when (it.status!!) {
                        0 -> showMsgDialogAndProceed(it.message!!.trim())
                        1 -> {
                            var isItemAdded = false
                            if (!menuX.isAdded) {
                                isItemAdded = true
                                addedItemList.add(menuX)
                            } else {
                                menu@ for (i in addedItemList.indices) {
                                    if (addedItemList[i].id == menuX.id) {
                                        addedItemList.removeAt(i)
                                        break@menu
                                    }
                                }
                            }

                            //region For selection duplicate item in List
                            for (s in menuXList.indices) {
                                if (menuX.title == menuXList[s].title) {
                                    adapterMenu.setMenuAdded(s, isItemAdded)
                                }
                            }
                            //endregion

                            //region for Search list
                            for (s in menuXSearchList.indices) {
                                if (menuX.title == menuXSearchList[s].title) {
                                    adapterMenuSearch.setMenuAdded(s, isItemAdded)
                                }
                            }
                            //endregion

                            calculateFooterSection()
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })

            //Click Listeners
            tvNext.setOnClickListener(this)
            ivFoodMenuArrow.setOnClickListener(this)
            menuTextLabel.setOnClickListener(this)
            ivMenu.setOnClickListener(this)
            menuMainContainer.setOnClickListener(this)
            lylMenuSearch.setOnClickListener(this)
            rlFooter.setOnClickListener(this)

            //Footer Layout
            rlFooter.visibility = View.GONE

            //region Menu List
            val menuListener = object : ListClickListener {
                @SuppressLint("SetTextI18n")
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {

                        menuX = objects as clsMenuList
                        menuPosition = pos
                        // making the shor menu invisible when it is open
                        if (lylMenuListShort.visibility == View.VISIBLE) {
                            lylMenuListShort.visibility = View.GONE
                            ivFoodMenuArrow.rotation = 0f
                        }

                        if (Config.menuServiceId.isEmpty() || Config.menuServiceId == Config.vendorDetailServiceId) {
                            callAddMenuCart(
                                menuX.id.toString().trim(),
                                if (!menuX.isAdded) "1" else "0"
                            )
                        } else if (Config.menuServiceId != Config.vendorDetailServiceId) {
                            val dialogMenu = DialogToast(requireActivity())
                            dialogMenu.show()
                            dialogMenu.holder?.let {
                                it.tvTitle.text = "Menu List"
                                it.tvMessage.text =
                                    "Are you sure you want to delete current cart dish for ${Config.menuServiceName}"
                                it.btnDialogCancel.text = "No"
                                it.btnDialogLogout.text = "Yes"
                                it.btnDialogLogout.setOnClickListener {
                                    dialogMenu.dismiss()
                                    Config.menuServiceId = Config.vendorDetailServiceId
                                    Config.menuServiceName = Config.vendorDetailServiceName

                                    callAddMenuCart(
                                        menuX.id.toString().trim(),
                                        "1"
                                    )
                                }
                                it.btnDialogCancel.setOnClickListener {
                                    dialogMenu.dismiss()
                                }
                            }
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            val rowListener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    try {
                        // ivFoodMenuArrow.performClick()
                        if (lylMenuListShort.visibility == View.VISIBLE) {
                            lylMenuListShort.visibility = View.GONE
                            ivFoodMenuArrow.rotation = 0f
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
            adapterMenu = MenuStickyHeaderAdapter(requireContext(), menuListener, rowListener)
            adapterMenuSearch = MenuSearchAdapter(requireContext(), menuListener, rowListener)
            //endregion

            //RecyclerView Menu List Properties
            rvMenuList.apply {
                setHasFixedSize(true)
                adapter = adapterMenu
                layoutManager = LinearLayoutManager(requireActivity())
            }
            rvMenuList.addItemDecoration(StickyHeaderItemDecoration(rvMenuList, adapterMenu))
            //RecyclerView Menu Search List Properties
            rvMenuSearchList.apply {
                setHasFixedSize(true)
                adapter = adapterMenuSearch
                layoutManager = LinearLayoutManager(requireActivity())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun calculateFooterSection() {
        try {
            //Footer Part for calculating total points & quanity
            if (addedItemList.isNotEmpty()) {
                rlFooter.visibility = View.VISIBLE
                totalPoints = 0
                for (a in addedItemList.indices) {
                    totalPoints += addedItemList[a].point
                }
                tvNoOfItems.text = addedItemList.size.toString()

                //region Settings points and bucketcount for showing in review screen
                Config.inBucketPoints = totalPoints.toString()
                Config.inBucketCount = addedItemList.size.toString()
                //endregion

                tvTotalPoints.apply {
                    text = "Total Points : $totalPoints"
                    visibility = if (totalPoints == 0) View.GONE else View.VISIBLE
                }
            } else {
                totalPoints = 0
                rlFooter.visibility = View.GONE
                Config.inBucketPoints = "0"
                Config.inBucketCount = "0"
            }
            //endregion
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun populateShortMenuList(menuXList: ArrayList<clsMenuList>) {
        try {
            lylMenuListShort.visibility = View.GONE
            val arrayListItems = ArrayList<clsPopupDialogList>()
            var menuTitle = ""
            var menuChildCount = 0
            var isChildFounded = false

            for (s in menuXList.indices) {
                if (menuXList[s].isHeader) {
                    if (isChildFounded) {
                        isChildFounded = false
                        val categoryName = menuTitle.split(",")[0]
                        val categoryPos = menuTitle.split(",")[1]
                        arrayListItems.add(
                            clsPopupDialogList(
                                "$categoryName ($menuChildCount)",
                                categoryPos
                            )
                        )
                        menuChildCount = 0
                    }
                    menuTitle = "${menuXList[s].title.trim()},$s"
                } else {
                    isChildFounded = true
                    menuChildCount += 1
                }
            }
            val categoryName = menuTitle.split(",")[0]
            val categoryPos = menuTitle.split(",")[1]
            arrayListItems.add(
                clsPopupDialogList(
                    "$categoryName ($menuChildCount)",
                    categoryPos
                )
            )

            if (arrayListItems.isNotEmpty()) {
                val listener = object : ListClickListener {
                    override fun onClickListener(view: View, pos: Int, objects: Any) {
                        try {
                            val popupDialogList = objects as clsPopupDialogList
                            lylMenuListShort.visibility = View.GONE
                            ivFoodMenuArrow.rotation = 0f
                            rvMenuList.smoothSnapToPosition(popupDialogList.strPos.toInt())
                            /*rvMenuList.smoothScrollToPosition(popupDialogList.strPos.toInt())*/
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
                val adapterMenuListShort = AdapterDialogPopupList(listener)
                adapterMenuListShort.addPopupDataItems(arrayListItems)

                llmMenuListShort = LinearLayoutManager(requireActivity())
                rvMenuListShort.apply {
                    layoutManager = llmMenuListShort
                    adapter = adapterMenuListShort
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun RecyclerView.smoothSnapToPosition(
        position: Int,
        snapMode: Int = LinearSmoothScroller.SNAP_TO_START
    ) {
        val smoothScroller = object : LinearSmoothScroller(this.context) {
            override fun getVerticalSnapPreference(): Int = snapMode
            override fun getHorizontalSnapPreference(): Int = snapMode
        }
        smoothScroller.targetPosition = position
        layoutManager?.startSmoothScroll(smoothScroller)
    }

    private fun populateMenuList(resMenu: NewMenuListRes) {
        try {
            if (resMenu.data!!.serviceDetails!!.id == null &&
                resMenu.data!!.serviceDetails!!.title == null
            ) {
                Config.menuServiceId = ""
                Config.menuServiceName = ""
            } else {
                Config.menuServiceId =
                    resMenu.data!!.serviceDetails!!.id!!.toString().trim()
                Config.menuServiceName =
                    resMenu.data!!.serviceDetails!!.title!!.toString().trim()
            }

            if (resMenu.data!!.menu!!.isNotEmpty()) {//Parent Menu is not empty
                for (m in resMenu.data!!.menu!!.indices) {//Parent Menu Loop
                    val clsMenuList = clsMenuList(
                        0,
                        "",
                        0,
                        0,
                        0,
                        "",
                        resMenu.data!!.menu!![m].name!!.trim(),
                        "",
                        "",
                        "",
                        "",
                        0.0,
                        0.0,
                        isHeader = true,
                        isAdded = false
                    )
                    menuXList.add(clsMenuList)
                    if (resMenu.data!!.menu!![m].menu!!.isNotEmpty()) { // Child Menu is not empty
                        for (b in resMenu.data!!.menu!![m].menu!!.indices) { //Child Menu Loop
                            val clsMenuChildList = clsMenuList(
                                resMenu.data!!.menu!![m].menu!![b].id!!,
                                resMenu.data!!.menu!![m].menu!![b].image!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].foodType!!,
                                resMenu.data!!.menu!![m].menu!![b].isSpicy!!,
                                resMenu.data!!.menu!![m].menu!![b].point!!,
                                resMenu.data!!.menu!![m].menu!![b].preparingTime!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].title!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].dishQty!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].unit!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].categoryName!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].currency!!.trim(),
                                resMenu.data!!.menu!![m].menu!![b].finalPrice!!,
                                resMenu.data!!.menu!![m].menu!![b].actualPrice!!,
                                false,
                                resMenu.data!!.menu!![m].menu!![b].inCart == 1
                            )
                            if (resMenu.data!!.menu!![m].menu!![b].inCart == 1) {
                                addedItemList.add(clsMenuChildList)
                            }
                            //region Remove Duplicates from addedItemList arraylist
                            val hashSet = LinkedHashSet<clsMenuList>(addedItemList)
                            addedItemList.apply {
                                clear()
                                addAll(hashSet)
                            }
                            //endregion
                            menuXList.add(clsMenuChildList)
                            menuXSearchList.add(clsMenuChildList)
                        }
                    }
                }
                if (menuXList.isNotEmpty()) {
                    adapterMenu.setMenuList(menuXList)
                    //region Search List
                    val hashSetSearchList = LinkedHashSet<clsMenuList>(menuXSearchList)
                    menuXSearchList.apply {
                        clear()
                        addAll(hashSetSearchList)
                    }
                    adapterMenuSearch.setMenuSearchList(menuXSearchList)
                    //endregion

                    calculateFooterSection()
                    populateShortMenuList(menuXList)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MenuFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            RestaurantMenuFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onClick(v: View?) {
        when (v) {
            tvNext -> {
                Config.isMyPointClickedFromHome = true
                if (PubFun.isInternetConnection(requireActivity())) {
                    if (Config.isBucketAddMoreClicked) {
                        Config.isBucketAddMoreClicked = false
                    }
                    startActivity(Intent(activity, MyBucketActivity::class.java))
                    requireActivity().finish()
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
            ivFoodMenuArrow -> {
                if (lylMenuListShort.visibility == View.GONE) {
                    lylMenuListShort.visibility = View.VISIBLE
                    ivFoodMenuArrow.rotation = 180f
                } else {
                    lylMenuListShort.visibility = View.GONE
                    ivFoodMenuArrow.rotation = 0f
                }
            }
            ivMenu, menuTextLabel -> {
                ivFoodMenuArrow.performClick()
            }

            menuMainContainer, lylMenuSearch, rlFooter -> {
                lylMenuListShort.visibility = View.GONE
                ivFoodMenuArrow.rotation = 0f
            }

        }
    }
}