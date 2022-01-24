package com.themarkettheory.user.ui.main.fragment

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.gson.Gson
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config.Companion.gson
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.ui.main.adapter.NotificationAlertsAdapter
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.fragment_notiification_alert.*
import javax.inject.Inject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AlertFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotificationAlertFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var generalViewModel: GeneralViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_notiification_alert, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val gson = Gson()
        generalViewModel.get_notifications()

        getResponse()

    }

    @SuppressLint("LongLogTag")
    private fun getResponse() {
        generalViewModel.isLoading.observe(viewLifecycleOwner, Observer {
            if (it!!) {

                Utils.showProgress(requireActivity())
            } else {
                Utils.hideProgress(requireActivity())
            }
        })

        generalViewModel.responseNotifications.observe(viewLifecycleOwner, Observer {

            if (it.status == 1) {
                val notificationAlertsAdapter =
                    it.data?.let { it1 -> NotificationAlertsAdapter(requireActivity(), it1) }
                Log.e("NotificationsAlertData:", gson.toJson(it.data))
                rvAlert.adapter = notificationAlertsAdapter
                rvAlert.layoutManager = LinearLayoutManager(activity)
            } else {
                Utils.showSnackbar(requireActivity(), it.message)
            }
        })
    }

//    override fun onAttach(context: Context) {
//        super.onAttach(context)
//        generalViewModel.get_notifications()
//
//    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AlertFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            NotificationAlertFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}