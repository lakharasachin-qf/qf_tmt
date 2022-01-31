package com.themarkettheory.user.ui.main.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.themarkettheory.user.R
import com.themarkettheory.user.helper.Config
import com.themarkettheory.user.helper.PubFun
import com.themarkettheory.user.ui.dialog.dialogToast.DialogToast
import com.themarkettheory.user.viewmodel.GeneralViewModel
import kotlinx.android.synthetic.main.activity_feedback_new.*

class FeedbackActivity : BaseActivity(), View.OnClickListener {
    var serviceId = 0
    lateinit var generalViewModel: GeneralViewModel
    var priviousRating = 0.0f
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.exitTransition = null
        this.window
            .setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE or
                        WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
            )
        setContentView(R.layout.activity_feedback_new)
        generalViewModel = ViewModelProvider(this).get(GeneralViewModel::class.java)

        val btnRating1 = findViewById<AppCompatButton>(R.id.btnRating1)
        val btnRating2 = findViewById<AppCompatButton>(R.id.btnRating2)
        val btnRating3 = findViewById<AppCompatButton>(R.id.btnRating3)
        val btnRating4 = findViewById<AppCompatButton>(R.id.btnRating4)

        serviceId = intent.getIntExtra("serviceId", 0)

        btnRating1.setOnClickListener {

            if (rbCustomerService.rating == 2.0f) {
                rbCustomerService.rating = 1.0f
            } else if (rbCustomerService.rating == 1.0f) {
                rbCustomerService.rating = 0.0f
            } else if (rbCustomerService.rating == 0.0f) {
                rbCustomerService.rating = 1.0f
            }
        }
        btnRating2.setOnClickListener {

            if (rbRestaurantCleanliness.rating == 2.0f) {
                rbRestaurantCleanliness.rating = 1.0f
            } else if (rbRestaurantCleanliness.rating == 1.0f) {
                rbRestaurantCleanliness.rating = 0.0f
            } else if (rbRestaurantCleanliness.rating == 0.0f) {
                rbRestaurantCleanliness.rating = 1.0f
            }
        }
        btnRating3.setOnClickListener {

            if (rbFoodQuality.rating == 2.0f) {
                rbFoodQuality.rating = 1.0f
            } else if (rbFoodQuality.rating == 1.0f) {
                rbFoodQuality.rating = 0.0f
            } else if (rbFoodQuality.rating == 0.0f) {
                rbFoodQuality.rating = 1.0f
            }
        }

        btnRating4.setOnClickListener {

            if (rbRecommendFriend.rating == 2.0f) {
                rbRecommendFriend.rating = 1.0f
            } else if (rbRecommendFriend.rating == 1.0f) {
                rbRecommendFriend.rating = 0.0f
            } else if (rbRecommendFriend.rating == 0.0f) {
                rbRecommendFriend.rating = 1.0f
            }
        }
        btnSubmitReview.setOnClickListener(this)
        getResponse()
    }


    override fun onClick(v: View?) {
        when (v) {
            btnSubmitReview -> {
                //call api for add review
                var comment: String = edAddFeedback.text.toString()
                var rating0: Float = rbCustomerService.rating
                var rating1: Float = rbRestaurantCleanliness.rating
                var rating2: Float = rbFoodQuality.rating
                var rating3: Float = rbRecommendFriend.rating

                val rating = arrayOf(rating0, rating1, rating2, rating3)
                //calling api as of now sending service id 3 hardcoded
                if (PubFun.isInternetConnection(this@FeedbackActivity)) {
                    generalViewModel.add_review(serviceId, rating, comment)
                } else {
                    showMsgDialogAndProceed(Config.msgToastForInternet, true)
                }
            }
        }
    }

    private fun getResponse() {
        generalViewModel.responseAddReview.observe(this, Observer {
            when (it.status) {
                0 -> showMsgDialogAndProceed(it.message!!.trim(), true)
                1 -> {
                    showMsgDialogAndProceed(it.message!!.trim(), false)
                }
            }
        })
    }

    @SuppressLint("SetTextI18n")
    private fun showMsgDialogAndProceed(msg: String, isError: Boolean) {
        try {
            val myDialog = DialogToast(this@FeedbackActivity)
            myDialog.show()
            myDialog.holder?.let {
                it.tvTitle.text = "Feedback"
                it.tvMessage.text = msg
                it.btnDialogCancel.visibility = View.GONE
                it.btnDialogLogout.text = "OK"
                it.btnDialogLogout.visibility = View.GONE
                var i = Config.autoDialogDismissTimeInSec
                it.btnDialogLogout.post(object : Runnable {
                    override fun run() {
                        if (i == 0) {
                            myDialog.dismiss()
                            if (!isError) {
                                val intent = Intent(this@FeedbackActivity, MainActivity::class.java)
                                intent.flags =
                                    Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            }
                        } else {
                            i--
                            it.btnDialogLogout.postDelayed(this, 1000)
                        }
                    }
                })
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {

        //super.onBackPressed()
    }
}