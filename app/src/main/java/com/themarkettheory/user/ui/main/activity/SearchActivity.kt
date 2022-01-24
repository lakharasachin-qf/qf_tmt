package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.interfaces.ListClickListener
import com.themarkettheory.user.newmodels.searchrestaurant.SearchRestaurantData
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.adapter.SearchRestaurantListAdapter
import com.themarkettheory.user.ui.restaurant.VendorDetailActivity
import com.themarkettheory.user.viewmodel.SearchViewModel
import kotlinx.android.synthetic.main.activity_search.*
import android.view.inputmethod.EditorInfo

import android.widget.TextView

import android.widget.TextView.OnEditorActionListener





class SearchActivity : BaseActivity(), View.OnClickListener {
    //ViewModel
    lateinit var searchViewModel: SearchViewModel

    //General
    private var handlerSearch = Handler(Looper.getMainLooper())
    private lateinit var runnableSearch: Runnable
    private val delay = 2000L //2 seconds
    private var lastEditText = 0L
    private var mySearchText = ""

    //RecyclerView Adapter
    private lateinit var adapterSearchRestaurant: SearchRestaurantListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        try {
            //View Model Initialization
            searchViewModel = ViewModelProvider(this).get(SearchViewModel::class.java)

            //region Set On Click Listener
            ivSearchRestaurantListBack.setOnClickListener(this)
            ivSearchRestaurantCloseIcon.setOnClickListener(this)
            //endregion

            //region Search Restaurant List RecyclerView, Listener and Adapter
            val listener = object : ListClickListener {
                override fun onClickListener(view: View, pos: Int, objects: Any) {
                    val searchRestaurantData = objects as SearchRestaurantData
                    Config.isMenuFragmentComingFrom = ""
                    PubFun.hideKeyboard(this@SearchActivity)
                    if (PubFun.isInternetConnection(this@SearchActivity)) {
                        startActivity(
                            Intent(
                                this@SearchActivity,
                                VendorDetailActivity::class.java
                            )
                                .putExtra("category", searchRestaurantData.categoryId.toString())
                                .putExtra("serviceId", searchRestaurantData.id.toString())
                                .putExtra("vendorTitle", searchRestaurantData.title)
                        )
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            }
            adapterSearchRestaurant = SearchRestaurantListAdapter(
                this@SearchActivity,
                listener
            )
            rvSearchRestaurantList.apply {
                layoutManager = LinearLayoutManager(this@SearchActivity)
                adapter = adapterSearchRestaurant
            }
            //endregion

            //region Loading Response
            searchViewModel.isLoadingSearch.observe(this, {
                try {
                    if (it!!) {
                        Utils.showProgress(this@SearchActivity)
                    } else {
                        Utils.hideProgress(this@SearchActivity)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            })
            //endregion

            //region All Search API Response
            searchViewModel.responseSearch.observe(this, {
                searchViewModel.isLoadingSearch.value = false
                if (!it.data.isNullOrEmpty()) {
                    adapterSearchRestaurant.addSearchRestaurantList(it.data!! as ArrayList<SearchRestaurantData>)
                } else {
                   // showMsgDialogAndProceed(it.message!!.toString().trim())
                }
            })
            //endregion

            //region Search Edittext
            etRestaurantListSearch.requestFocus()
            PubFun.showKeyboard(this@SearchActivity)
            etRestaurantListSearch.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    try {
                        lastEditText = System.currentTimeMillis()
                        handlerSearch.postDelayed(runnableSearch, delay)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
                false
            })
            etRestaurantListSearch.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {

                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    handlerSearch.removeCallbacks(runnableSearch)
                    mySearchText = s.toString().lowercase().trim()
                    if (mySearchText.isEmpty()) adapterSearchRestaurant.clearSearchRestaurantList()
                }

                override fun afterTextChanged(s: Editable?) {
                    try {
                        lastEditText = System.currentTimeMillis()
                        handlerSearch.postDelayed(runnableSearch, delay)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            })
            //endregion

            //region Runnable Search
            runnableSearch = Runnable {
                if (System.currentTimeMillis() > (lastEditText + delay - 500)) {
                    callAllSearchAPI()
                }
            }
            //endregion

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        try {
            //Remove Handler Call Back
            handlerSearch.removeCallbacks(runnableSearch)
            handlerSearch.removeCallbacksAndMessages(null)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onClick(v: View?) {
        try {
            when (v) {
                ivSearchRestaurantListBack -> onBackPressed()
                ivSearchRestaurantCloseIcon -> {
                    searchViewModel.isLoadingSearch.value = false
                    etRestaurantListSearch.text!!.clear()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun callAllSearchAPI() {
        try {
            if (mySearchText.isNotEmpty()) {
                if (PubFun.isInternetConnection(this@SearchActivity)) {
                    searchViewModel.isLoadingSearch.value = true
                    searchViewModel.allSearch(mySearchText)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet)
                }
            }
        } catch (e: Exception) {
            searchViewModel.isLoadingSearch.value = false
            e.printStackTrace()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(this@SearchActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Search Restaurant List"
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
}