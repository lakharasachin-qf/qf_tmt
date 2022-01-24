package com.themarkettheory.user.ui.restaurant

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.model.SubCategoryDatum
import com.themarkettheory.user.newmodels.map.Restaurant
import com.themarkettheory.user.ui.main.adapter.VendorListAdapter
import com.themarkettheory.user.ui.main.fragment.BaseFragment
import com.themarkettheory.user.viewmodel.HomeViewModel
import kotlinx.android.synthetic.main.fragment_restaurant.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RestaurantFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RestaurantFragment : BaseFragment(), View.OnClickListener {
    private lateinit var vendorListAdapter: VendorListAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var homeViewModel: HomeViewModel
    var sortList = ArrayList<String>()
    var ratingList = ArrayList<String>()
    var sortPosition = -1
    var ratingPosition = -1
    var subcategoryPosition = -1
    var list = ArrayList<Restaurant>()
    var subCategoryList = ArrayList<SubCategoryDatum>()
    val re = Regex("[^\\d.]")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        homeViewModel = ViewModelProvider(requireActivity()).get(HomeViewModel::class.java)
        homeViewModel.service_list(param1, "", "", "", "", "", "")

//        if(param1 == "3"){
        homeViewModel.get_sub_categories(param1)
//        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_restaurant, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getServicesResponse()

        setSpinner()

        ivRatingSort.setOnClickListener(this)
        ivSort.setOnClickListener(this)
        ivSubcategorySort.setOnClickListener(this)
        llSubCategory.setOnClickListener(this)
        llSort.setOnClickListener(this)
    }

    private fun setSpinner() {
        sortList.add("Select")
        sortList.add("Recommended By TMT")
        sortList.add("Rating : High To Low")
        sortList.add("Most Favorite")
        sortList.add("Near By")
        sortList.add("Ascending")
        sortList.add("None")

        val dataAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(requireActivity(), R.layout.row_sort_spinner_item, sortList)
        spSort.adapter = dataAdapter

        ratingList.add("Rating")
        ratingList.add("5.0⭐")
        ratingList.add("4.5⭐")
        ratingList.add("4.0⭐")
        ratingList.add("3.5⭐")
        ratingList.add("3.0⭐")
        ratingList.add("2.5⭐")
        ratingList.add("2.0⭐")
        ratingList.add("1.5⭐")
        ratingList.add("1.0⭐")
        ratingList.add("0.5⭐")

        val dataAdapter1: ArrayAdapter<String> =
            ArrayAdapter<String>(requireActivity(), R.layout.row_sort_spinner_item, ratingList)
        spRating.adapter = dataAdapter1

        spSort.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                sortPosition = position
                sortAndFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        })

        spRating.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                ratingPosition = position
                sortAndFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        })

        spSubcategory.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                subcategoryPosition = position
                sortAndFilter()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }

        })
    }

    fun sortAndFilter() {
        var tempData = ArrayList<Restaurant>()
        tempData.addAll(list)
        if (sortPosition != -1 && sortPosition != 0) {
            if (sortPosition == 1) {
                tempData.sortByDescending { it.isRecommanded == 1 }
            } else if (sortPosition == 2) {
                tempData.sortByDescending { it.rating!!.toFloat() }
            } else if (sortPosition == 3) {
                tempData.sortByDescending { it.isFavourite }
            } else if (sortPosition == 4) {
                tempData.map {
                    var distance = it.distance
                    distance = re.replace(distance!!, "")
                    it.distance = distance
                }
                tempData.sortBy { it.distance }
            } else if (sortPosition == 5) {
                tempData.sortBy { it.title }
            }
        }

        if (ratingPosition != -1 && ratingPosition != 0) {
            var ratingData = ratingList[ratingPosition]
            ratingData = re.replace(ratingData, "")
            tempData =
                tempData.filter { it -> it.rating!!.toFloat() >= ratingData.toFloat() } as ArrayList<Restaurant>
        }


        if (subcategoryPosition != -1 && subcategoryPosition != 0 && subCategoryList[subcategoryPosition].id != 0) {
            if (param1 == "3") {
                tempData =
                    tempData.filter { it -> it.subCategoryId == subCategoryList[subcategoryPosition].id } as ArrayList<Restaurant>
            } else {
                tempData = tempData.filter { it -> isExist(it) } as ArrayList<Restaurant>
            }
        }
        if (::vendorListAdapter.isInitialized)
            vendorListAdapter.updataData(tempData)
    }

    fun isExist(it: Restaurant): Boolean {
        /*it.cuisine_types?.mapIndexed { index, cuisineTypes ->
            if (cuisineTypes.id == subCategoryList[subcategoryPosition].id){
                return true
            }
        }*/
        return false
    }

    private fun getServicesResponse() {
        homeViewModel.responseServices.observe(viewLifecycleOwner, Observer {
            when (it.status!!) {
                0 -> Toast.makeText(requireActivity(), it.message, Toast.LENGTH_LONG).show()
                1 -> {
                    list.clear()
                    list.addAll(it.data?.restaurant!!)
                    vendorListAdapter = VendorListAdapter(requireActivity(), list)
                    rvVendor.adapter = vendorListAdapter
                    rvVendor.layoutManager = LinearLayoutManager(requireActivity())
                }
            }
        })

        homeViewModel.responseSubCategories.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                llSubCategory.visibility = View.VISIBLE
                subCategoryList.clear()
                val model = SubCategoryDatum()
                model.name = "Cuisines"
                model.id = 0
                val modelForNone = SubCategoryDatum()
                modelForNone.name = "None"
                modelForNone.id = 0
                subCategoryList.add(model)
                subCategoryList.addAll(it.data!!)
                subCategoryList.add(modelForNone)
                val dataAdapter1: ArrayAdapter<SubCategoryDatum> =
                    ArrayAdapter<SubCategoryDatum>(
                        requireActivity(),
                        R.layout.row_sort_spinner_item,
                        subCategoryList
                    )
                spSubcategory.adapter = dataAdapter1
            }
        })
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RestaurantFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RestaurantFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v) {
            ivRatingSort -> spRating.performClick()

            ivSort -> spSort.performClick()

            ivSubcategorySort -> spSubcategory.performClick()

            llSubCategory -> spSubcategory.performClick()

            llSort -> spSort.performClick()
        }
    }
}