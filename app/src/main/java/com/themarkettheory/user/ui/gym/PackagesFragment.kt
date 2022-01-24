package com.themarkettheory.user.ui.gym

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.themarkettheory.user.R
import com.themarkettheory.user.ui.main.adapter.PackageListAdapter
import com.themarkettheory.user.viewmodel.GymViewModel
import kotlinx.android.synthetic.main.fragment_packages.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [PackagesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class PackagesFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var gymViewModel: GymViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        gymViewModel = ViewModelProvider(this).get(GymViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_packages, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        gymViewModel.gym_packages(param1)

        getResponse()
    }

    private fun getResponse() {
        gymViewModel.responseGymPackages.observe(viewLifecycleOwner, Observer {
            if (it.status!!) {
                val packageListAdapter = PackageListAdapter(requireActivity(), it.data?.list!!) {
                    startActivity(
                        Intent(requireActivity(), PackageDetailActivity::class.java)
                            .putExtra("data", it)
                    )
                }
                rvPackages.adapter = packageListAdapter
                rvPackages.layoutManager = LinearLayoutManager(activity)
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
         * @return A new instance of fragment PackagesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            PackagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}