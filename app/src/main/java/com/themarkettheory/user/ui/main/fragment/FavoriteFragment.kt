package com.themarkettheory.user.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.getcategories.Category
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.NotificationActivity
import com.themarkettheory.user.ui.main.adapter.FavoriteListAdapter
import com.themarkettheory.user.ui.main.adapter.HomeCategoriesAdapter
import com.themarkettheory.user.viewmodel.FavoriteViewModel
import kotlinx.android.synthetic.main.fragment_favorite.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoriteFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoriteFragment : Fragment(), View.OnClickListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var favoriteViewModel: FavoriteViewModel
    var relativeLayoutArray = arrayOf<RelativeLayout>()
    var imageViewArray = arrayOf<ImageView>()
    var textviewArray = arrayOf<TextView>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        favoriteViewModel = ViewModelProvider(this).get(FavoriteViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_favorite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        relativeLayoutArray = arrayOf(
            rlRestaurant, rlSalon, rlGym,
            rlSpa, rlRetail, rlBakery
        )
        imageViewArray = arrayOf(
            ivRestaurant, ivSalon, ivGym,
            ivSpa, ivRetail, ivBakery
        )
        textviewArray = arrayOf(
            tvRestaurant, tvSalon, tvGym,
            tvSpa, tvRetail, tvBakery
        )

        if (PubFun.isInternetConnection(requireActivity())) {
            favoriteViewModel.getCategories()
        } else {
            showMsgDialogAndProceed(Config.msgToastForInternet)
        }
        getResponses()

        //updateUI(rlRestaurant,tvRestaurant,ivRestaurant)

        llRestaurant.setOnClickListener(this)
        llSalon.setOnClickListener(this)
        llGym.setOnClickListener(this)
        llSpa.setOnClickListener(this)
        llRetail.setOnClickListener(this)
        llBakery.setOnClickListener(this)
        ivNotification.setOnClickListener(this)
    }

    private fun getResponses() {
        favoriteViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {
                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        favoriteViewModel.responseGetCategories.observe(viewLifecycleOwner, Observer { it ->
            val categoryData = ArrayList<Category>()
            for (i in it.data!!.categories!!.indices) {
                val category = Category()
                category.id = it.data!!.categories!![i].id!!
                category.name = it.data!!.categories!![i].name!!

                /*if (category.name!! == Config.subCategory0IndexRestaurantOldName) {
                    category.name = Config.subCategory0IndexRestaurantNewName
                }*/
                categoryData.add(category)
            }

//            categoryData?.removeAt(0)
            val homeCategoriesAdapter =
                HomeCategoriesAdapter(requireActivity(), categoryData) { category ->
                    if (PubFun.isInternetConnection(requireActivity())) {
                        favoriteViewModel.get_favourite_services(category.id.toString())
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }
            rvHomeCategory.adapter = homeCategoriesAdapter
            rvHomeCategory.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

            if (PubFun.isInternetConnection(requireActivity())) {
                favoriteViewModel.get_favourite_services(categoryData[0].id.toString())
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

        })

        favoriteViewModel.responseGetFavoriteServices.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                val favoriteListAdapter = FavoriteListAdapter(requireActivity(), it.data!!) {
                    favoriteViewModel.favourite_services(it.id.toString())
                }
                rvFavorites.adapter = favoriteListAdapter
                rvFavorites.layoutManager = LinearLayoutManager(activity)
            }
        })

        favoriteViewModel.responseFavoriteService.observe(viewLifecycleOwner, Observer {
//            Utils.showSnackbar(requireActivity(),it.message)
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment FavoriteFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoriteFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v) {

            llRestaurant -> {
                updateUI(rlRestaurant, tvRestaurant, ivRestaurant)
            }

            llSalon -> {
                updateUI(rlSalon, tvSalon, ivSalon)
            }

            llSpa -> {
                updateUI(rlSpa, tvSpa, ivSpa)
            }

            llRetail -> {
                updateUI(rlRetail, tvRetail, ivRetail)
            }

            llBakery -> {
                updateUI(rlBakery, tvBakery, ivBakery)
            }

            llGym -> {
                updateUI(rlGym, tvGym, ivGym)
            }

            ivNotification -> {
                startActivity(Intent(requireActivity(), NotificationActivity::class.java))
            }
        }
    }

    fun updateUI(rl: RelativeLayout, tv: TextView, iv: ImageView) {
        relativeLayoutArray.map {
            it.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_menu_bg)
        }

        textviewArray.map {
            it.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
        }

        imageViewArray.map {
            it.setColorFilter(
                ContextCompat.getColor(requireActivity(), R.color.gull_gray),
                android.graphics.PorterDuff.Mode.MULTIPLY
            );
        }
        rl.background = ContextCompat.getDrawable(requireActivity(), R.drawable.ic_menu_selected_bg)
        tv.setTextColor(ContextCompat.getColor(requireActivity(), R.color.robins_egg_blue))
        iv.setColorFilter(Color.argb(255, 255, 255, 255))
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String) {
        try {
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Favorite"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 500)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}