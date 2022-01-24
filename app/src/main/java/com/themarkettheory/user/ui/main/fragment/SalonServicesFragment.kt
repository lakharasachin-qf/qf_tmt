package com.themarkettheory.user.ui.main.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.model.SaloonSpaList
import com.themarkettheory.user.ui.salon.SalonBookingInformationActivity
import com.themarkettheory.user.ui.main.adapter.AvailableServiceListAdapter
import com.themarkettheory.user.ui.main.adapter.ComboPackListAdapter
import com.themarkettheory.user.viewmodel.SaloonViewModel
import kotlinx.android.synthetic.main.fragment_salon_services.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SalonServicesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SalonServicesFragment : Fragment(), View.OnClickListener {
    private lateinit var availableServiceListAdapter: AvailableServiceListAdapter

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    lateinit var saloonViewModel: SaloonViewModel
    var serviceList = ArrayList<SaloonSpaList>()
    var list = ArrayList<SaloonSpaList>()
    var gender = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        saloonViewModel = ViewModelProvider(this).get(SaloonViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_salon_services, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saloonViewModel.saloon_spa_services(param1, "0")

        saloonViewModel.responseSaloonServices.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                if (it.data?.list.isNullOrEmpty()) {
                    tvAvailableServices.visibility = View.GONE
                } else {
                    list.clear()
                    list.addAll(it.data?.list!!)
                    list.map {
                        var minAmount = it.mrpPrice!!.toFloat()
                        if (!it.discountedPrice.isNullOrEmpty()) {
                            minAmount = it.discountedPrice!!.toFloat()
                        }
                        it.minAmount = minAmount

                    }
                    availableServiceListAdapter =
                        AvailableServiceListAdapter(requireActivity(), list) {
                            val data = availableServiceListAdapter.getServiceData()
                            var total = 0.0f
                            var minAmt = 0.0f
                            var currency: String? = ""
                            var isSelected = false
                            serviceList.clear()
                            data.mapIndexed { index, saloonSpaList ->
                                if (saloonSpaList.isSelected) {
                                    currency = saloonSpaList.currency
                                    total += saloonSpaList.discountedPrice!!.toFloat()
                                    isSelected = true
                                    serviceList.add(saloonSpaList)
                                    var minAmount = saloonSpaList.mrpPrice!!.toFloat()
                                    if (!saloonSpaList.discountedPrice.isNullOrEmpty()) {
                                        minAmount = saloonSpaList.discountedPrice!!.toFloat()
                                    }
                                    if (minAmt == 0.0f) {
                                        minAmt = saloonSpaList.minAmount!!
                                    }
                                    if (minAmount < saloonSpaList.minAmount!!) {
                                        minAmt = saloonSpaList.minAmount!!
                                    }
                                }
                            }
//                            tvTotal.setText(Utils.getDecimalFormat().format(total))
                            tvTotal.setText(currency + minAmt)
                            if (isSelected) {
                                rlBottom.visibility = View.VISIBLE
                            } else {
                                rlBottom.visibility = View.GONE
                            }
                        }
                    rvAvailableServices.adapter = availableServiceListAdapter
                    rvAvailableServices.layoutManager = GridLayoutManager(activity, 3)
                }

                if (it.data?.combo.isNullOrEmpty()) {
                    tvcomboPack.visibility = View.GONE
                } else {
                    val comboPackListAdapter =
                        ComboPackListAdapter(requireActivity(), it.data?.combo!!) {
                            serviceList.clear()
                            serviceList.add(it)
                            startActivity(
                                Intent(activity, SalonBookingInformationActivity::class.java)
                                    .putExtra("serviceList", serviceList)
                            )
                        }
                    rvComboPack.adapter = comboPackListAdapter
                    rvComboPack.layoutManager = LinearLayoutManager(activity)
                }

            }
        })

        llMen.setOnClickListener(this)
        llWomen.setOnClickListener(this)
        llKid.setOnClickListener(this)
        btnBookNow.setOnClickListener(this)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SalonServicesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            SalonServicesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onClick(v: View?) {
        when (v) {
            llMen -> {
                gender = "1"
                unselectViews()
                ivMen.setImageResource(R.drawable.ic_male_enabled)
                tvMen.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                saloonViewModel.saloon_spa_services(param1, "1")
            }

            llWomen -> {
                gender = "2"
                unselectViews()
                ivWomen.setImageResource(R.drawable.ic_female_enable)
                tvWomen.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                saloonViewModel.saloon_spa_services(param1, "2")
            }

            llKid -> {
                gender = "3"
                unselectViews()
                ivKid.setImageResource(R.drawable.ic_kid_selected)
                tvKid.setTextColor(ContextCompat.getColor(requireActivity(), R.color.white))
                saloonViewModel.saloon_spa_services(param1, "3")
            }

            btnBookNow -> {
                startActivity(
                    Intent(activity, SalonBookingInformationActivity::class.java)
                        .putExtra("serviceList", serviceList)
                        .putExtra("serviceId", param1)
                )
            }
        }
    }

    fun unselectViews() {
        ivMen.setImageResource(R.drawable.ic_male_disable)
        ivWomen.setImageResource(R.drawable.ic_female_disable)
        ivKid.setImageResource(R.drawable.ic_kid_unselected)
        tvMen.setTextColor(ContextCompat.getColor(requireActivity(), R.color.slate_gray))
        tvWomen.setTextColor(ContextCompat.getColor(requireActivity(), R.color.slate_gray))
        tvKid.setTextColor(ContextCompat.getColor(requireActivity(), R.color.slate_gray))
    }
}