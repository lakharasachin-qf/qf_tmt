package com.themarkettheory.customer.ui.restaurant.restaurant_detail

import RangeTimePickerDialog
import android.annotation.SuppressLint
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.*
import android.widget.ArrayAdapter
import android.widget.TimePicker
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.michalsvec.singlerowcalendar.calendar.CalendarChangesObserver
import com.michalsvec.singlerowcalendar.calendar.CalendarViewManager
import com.michalsvec.singlerowcalendar.calendar.SingleRowCalendarAdapter
import com.michalsvec.singlerowcalendar.selection.CalendarSelectionManager
import com.michalsvec.singlerowcalendar.utils.DateUtils
import com.themarkettheory.user.R
import com.themarkettheory.user.database.MyRoomDatabase
import com.themarkettheory.user.database.dbtables.TableConfig
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.helper.Utils
import com.themarkettheory.user.newmodels.overview.Occasion
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.ui.main.activity.MyTableBookingDetailActivity
import com.themarkettheory.user.ui.main.adapter.GuestListAdapter
import com.themarkettheory.user.ui.main.adapter.GuestListAdapterChild
import com.themarkettheory.user.viewmodel.VendorDetailViewModel
import kotlinx.android.synthetic.main.activity_booking_detail.*
import kotlinx.android.synthetic.main.calendar_item.view.*
import kotlinx.android.synthetic.main.fragment_booking.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [BookingFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class BookingFragment : Fragment(), View.OnClickListener, TimePickerDialog.OnTimeSetListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    var child = 0
    var adults = 0
    private var bookDate: String? = ""
    private var bookTime: String? = ""
    lateinit var adultGuestListAdapter: GuestListAdapter
    lateinit var childGuestListAdapter: GuestListAdapterChild
    lateinit var vendorDetailViewModel: VendorDetailViewModel
    val data = ArrayList<Occasion>()
    var date = Date()
    var format: DateFormat = SimpleDateFormat("DD MMM yyyy", Locale.ENGLISH)
    var isPastTimeValidation: Boolean = true


    private val calendar = Calendar.getInstance()
    private var currentMonth = 0


    //Database
    private lateinit var myRoomDatabase: MyRoomDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            arguments?.let {
                param1 = it.getString(ARG_PARAM1)
                param2 = it.getString(ARG_PARAM2)
            }
            vendorDetailViewModel = ViewModelProvider(this).get(VendorDetailViewModel::class.java)
            requireActivity().window
                .setSoftInputMode(
                    WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                            WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                )
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        try {
            return inflater.inflate(
                com.themarkettheory.user.R.layout.fragment_booking,
                container,
                false
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        try {
            //Initialize RoomDatabase
            myRoomDatabase = MyRoomDatabase.getDB(requireActivity())!!

            adultGuestListAdapter = GuestListAdapter(requireActivity(), 100, true) {
            }
            rvAdults.adapter = adultGuestListAdapter
            rvAdults.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

            childGuestListAdapter = GuestListAdapterChild(requireActivity(), 100, false) {
            }
            rvChild.adapter = childGuestListAdapter
            rvChild.layoutManager =
                LinearLayoutManager(requireActivity(), LinearLayoutManager.HORIZONTAL, false)

            populateSingleRowCalendarView()


            bookingViewContainer.visibility = View.GONE

            /*edSpecialInstruction.setOnTouchListener(OnTouchListener { v, event ->
                edSpecialInstruction.requestLayout()
                requireActivity().window
                    .setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_UNSPECIFIED)
                false
            })*/

            getResponse()
            /*   vendorDetailViewModel.occations()*/
            ivAdult1.setOnClickListener(this)
            ivChild.setOnClickListener(this)
            btnBookNow.setOnClickListener(this)
            edSelectTime.setOnClickListener(this)
            imgCollapse.setOnClickListener(this)

            add30MinutesToCurrentTime()

            edSpecialInstruction.setOnTouchListener(object : View.OnTouchListener {
                override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                    if (v!!.id === com.themarkettheory.user.R.id.edSpecialInstruction) {
                        v!!.parent.requestDisallowInterceptTouchEvent(true)
                        when (event!!.action and MotionEvent.ACTION_MASK) {
                            MotionEvent.ACTION_UP -> v!!.parent.requestDisallowInterceptTouchEvent(
                                false
                            )
                        }
                    }
                    return false
                }
            })

            if (PubFun.isInternetConnection(requireActivity())) {
                vendorDetailViewModel.service_details(Config.vendorDetailServiceId)
            } else {
                showMsgDialogAndProceed(Config.msgToastForInternet)
            }

            vendorDetailViewModel.responseServiceDetail.observe(viewLifecycleOwner, {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message.toString())
                    1 -> {
                        // 25-11-2021 changes for table booking given by amar
                        if (it.data!!.isTableBooking == 0 && !Config.isPreOrder) {
                            /*showMsgDialogAndProceed("Table booking is not available in this restaurant")*/
                            btnBookNow.visibility = View.GONE
                        } else {
                            btnBookNow.visibility = View.VISIBLE
                            data.clear()
                            val model = Occasion()
                            model.name = "Select Special Occasion"
                            //model.id = 0
                            data.add(model)
                            data.addAll(it.data!!.occasion!!)
                            val dataAdapter =
                                ArrayAdapter(
                                    requireActivity(),
                                    R.layout.row_spinner,
                                    data
                                )
                            spOccations.adapter = dataAdapter
                        }
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun add30MinutesToCurrentTime() {
        val cal = Calendar.getInstance()
        cal.add(Calendar.MINUTE, 30)
        edSelectTime.text = PubFun.parseDate(
            cal.time.toString(),
            "EEE MMM dd HH:mm:ss",
            Config.defaultTimeFormat
        )
    }

    private fun getResponse() {
        try {
            vendorDetailViewModel.isLoadingBookTable.observe(viewLifecycleOwner, Observer {
                if (it!!) {
                    Utils.showProgress(requireActivity()!!)
                } else {
                    Utils.hideProgress(requireActivity()!!)
                }
            })

            vendorDetailViewModel.responseBookTable.observe(viewLifecycleOwner, Observer {
                when (it.status!!) {
                    0 -> showMsgDialogAndProceed(it.message.toString())
                    1 -> {
                        showMsgDialogAndProceed(it.message.toString())
                        myRoomDatabase.daoConfig().deleteConfigTableByField(Config.dbTableBookingId)
                        myRoomDatabase.daoConfig().insertConfigTable(
                            TableConfig(
                                Config.dbTableBookingId,
                                it.data!!.id!!.toString().trim()
                            )
                        )
                         Handler(Looper.getMainLooper()).postDelayed({
                            Config.isBookingDetailOpeningFrom = ""
                            startActivity(
                                Intent(
                                    requireActivity(),
                                    MyTableBookingDetailActivity::class.java
                                ).putExtra("bookFragment", "yes")
                            )
                            requireActivity().finish()
                        }, 4000)
                    }
                }
            })
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
         * @return A new instance of fragment BookingFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String?, param2: String?) =
            BookingFragment()
                .apply {
                    arguments = Bundle().apply {
                        putString(ARG_PARAM1, param1)
                        putString(ARG_PARAM2, param2)
                    }
                }
    }

    override fun onClick(v: View?) {

        try {
            when (v) {
                btnBookNow -> {
                    if (PubFun.isInternetConnection(requireActivity())) {
                        bookTime = edSelectTime.text.toString().trim()

                        var totalPerson = 0
                        adultGuestListAdapter.getFlags().mapIndexed { index, b ->
                            if (b!!)
                                adults = index + 1
                        }

                        childGuestListAdapter.getFlags().mapIndexed { index, b ->
                            if (b!!)
                                child = index + 1
                        }
                        totalPerson = adults + child

                        if (bookDate.isNullOrEmpty()) {
                            showMsgDialogAndProceed("Please select date")
                        } else if (bookTime.isNullOrEmpty()) {
                            showMsgDialogAndProceed("Please select time")
                        } else if (totalPerson == 0) {
                            showMsgDialogAndProceed("Please select Number of guests")
                        } /*else if (bookingDateCal.timeInMillis - Calendar.getInstance().timeInMillis < 0) {
                        Utils.showSnackbar(
                            requireActivity()!!,
                            "Please select valid booking date time"
                        )
                    }*/ else {

                            vendorDetailViewModel.book_table(
                                param1,
                                bookDate,
                                bookTime,
                                totalPerson.toString(),
                                adults.toString(),
                                child.toString(),
                                data[spOccations.selectedItemPosition].id.toString(),
                                edSpecialInstruction.text.toString()
                            )

                        }
                    } else {
                        showMsgDialogAndProceed(Config.msgToastForInternet)
                    }
                }

                edSelectTime -> {
                    openRangeTimePickerDialog()
                }

                ivAdult1 -> {
                    rvAdults.post(Runnable { rvAdults.smoothScrollToPosition(lastPosition(rvAdults) + 5) })
                }

                ivChild -> {
                    rvChild.post(Runnable { rvChild.smoothScrollToPosition(lastPosition(rvChild) + 5) })
                }

                imgCollapse -> {
                    if (bookingViewContainer.isVisible) {
                        bookingViewContainer.visibility = View.GONE
                        imgCollapse.setImageResource(com.themarkettheory.user.R.drawable.ic_plus_round)
                    } else {
                        bookingViewContainer.visibility = View.VISIBLE
                        imgCollapse.setImageResource(com.themarkettheory.user.R.drawable.ic_new_minus)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openRangeTimePickerDialog() {
        try {
            val mCurrentTime = Calendar.getInstance()
            val hour = mCurrentTime[Calendar.HOUR_OF_DAY]
            val minute = mCurrentTime[Calendar.MINUTE]
            val mTimePicker = RangeTimePickerDialog(
                requireActivity(),
                2,
                { timePicker, selectedHour, selectedMinute ->
                    val mySelectedHrs: String = selectedHour.toString()
                    val mySelectedMin: String =
                        if (selectedMinute.toString().length == 1) "0$selectedMinute" else selectedMinute.toString()
                    val mySelectedTime = "$mySelectedHrs:$mySelectedMin"
                    edSelectTime.text =
                        PubFun.parseDate(mySelectedTime, "HH:mm", Config.defaultTimeFormat)
                }, hour, minute, false, isPastTimeValidation
            ) //true = 24 hour time

            mTimePicker.setTitle("Select Time")
            mTimePicker.setMin(hour, minute)
            mTimePicker.show()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun lastPosition(recyclerView: RecyclerView): Int {
        try {
            val layoutManager = recyclerView.getLayoutManager() as LinearLayoutManager
            val pos = layoutManager.findLastCompletelyVisibleItemPosition()
            return pos.toInt();
        } catch (e: Exception) {
            e.printStackTrace()
            return 0
        }
    }

    @SuppressLint("SetTextI18n")

    private fun showMsgDialogAndProceed(msg: String) {
        try {
            Utils.hideProgress(requireActivity())
            val myDialog = DialogToast(requireActivity())
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = getString(R.string.my_bucket)
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
                    }
                    )
                }
                /*it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.setOnClickListener {
                    myDialog.dismiss()
                    //edSelectTime.performClick()
                }*/
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // callender view intialising
    fun populateSingleRowCalendarView() {
        calendar.time = Date()
        currentMonth = calendar[Calendar.MONTH]

        // enable white status bar with black icons
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requireActivity().window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            requireActivity().window.statusBarColor = Color.WHITE
        }*/
        bookingDateText.text = "${DateUtils.getMonthName(date)}, ${DateUtils.getYear(date)}"
        val myCalendarViewManager = object :
            CalendarViewManager {
            override fun setCalendarViewResourceId(
                position: Int,
                date: Date,
                isSelected: Boolean
            ): Int {
                // set date to calendar according to position where we are
                val cal = Calendar.getInstance()
                cal.time = date
                return if (isSelected)
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        else -> R.layout.selected_calendar_item
                    }
                else
                // here we return items which are not selected
                    when (cal[Calendar.DAY_OF_WEEK]) {
                        else -> R.layout.calendar_item
                    }

                // NOTE: if we don't want to do it this way, we can simply change color of background
                // in bindDataToCalendarView method
            }

            override fun bindDataToCalendarView(
                holder: SingleRowCalendarAdapter.CalendarViewHolder,
                date: Date,
                position: Int,
                isSelected: Boolean
            ) {
                // using this method we can bind data to calendar view
                // good practice is if all views in layout have same IDs in all item views
                var DateNumber: String = DateUtils.getDayNumber(date)
                if (DateNumber.get(0).toString().equals("0")) {
                    DateNumber = DateNumber.get(1).toString()
                }
                holder.itemView.tv_date_calendar_item.text = DateNumber
                holder.itemView.tv_day_calendar_item.text =
                    DateUtils.getDay3LettersName(date).toString().uppercase()
            }

        }
        // using calendar changes observer we can track changes in calendar
        val myCalendarChangesObserver = object :
            CalendarChangesObserver {
            // you can override more methods, in this example we need only this one
            override fun whenSelectionChanged(isSelected: Boolean, position: Int, date: Date) {

                // here we get the date selection and other stuff
                bookingDateText.text =
                    "${DateUtils.getMonthName(date)}, ${DateUtils.getYear(date)}"
//                tvDay.text = DateUtils.getDayName(date)
                isPastTimeValidation = position == 0
                if (position == 0) add30MinutesToCurrentTime()
                bookDate = "${DateUtils.getDayNumber(date)}-${DateUtils.getMonthName(date)}-${
                    DateUtils.getYear(date)
                }"
                bookDate = PubFun.parseDate(bookDate, "dd-MMM-yyyy", Config.requestDateFormat)
                super.whenSelectionChanged(isSelected, position, date)
            }
        }
        val mySelectionManager = object : CalendarSelectionManager {
            override fun canBeItemSelected(position: Int, date: Date): Boolean {
                // set date to calendar according to position
                val cal = Calendar.getInstance()
                cal.time = date
                return true
            }
        }
        weekCalendar.apply {
            calendarViewManager = myCalendarViewManager
            calendarChangesObserver = myCalendarChangesObserver
            calendarSelectionManager = mySelectionManager
            futureDaysCount = 90
            init()
            select(0)
        }
    }

    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        val datetime = Calendar.getInstance()
        val c = Calendar.getInstance()
        datetime[Calendar.HOUR_OF_DAY] = hourOfDay
        datetime[Calendar.MINUTE] = minute
        if (datetime.timeInMillis >= c.timeInMillis) {
            //it's after current
            val hour = hourOfDay % 12
            edSelectTime.setText(
                String.format(
                    "%02d:%02d %s", if (hour == 0) 12 else hour,
                    minute, if (hourOfDay < 12) "am" else "pm"
                )
            )
        } else {
            //it's before current'
            showMsgDialogAndProceed("Invalid Time")
        }
//        timePickerFragment.show(getSupportFragmentManager(), "TIME")
    }
}