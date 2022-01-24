package com.themarkettheory.user.ui.main.adapter

/*import com.themarkettheory.user.model.UpcomingEvent*/
import android.content.Context
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.themarkettheory.user.R

class UpcomingListAdapter(
    val context: Context,
    /*val data: List<UpcomingEvent>,
    val listener: (UpcomingEvent) -> Unit*/
) : PagerAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = LayoutInflater.from(context)
    }

    var flags = booleanArrayOf(false, false, false)

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun getCount(): Int {
        /*return data.size*/
        return 0
    }

    override fun instantiateItem(view: ViewGroup, position: Int): Any {
        val eventLayout = inflater.inflate(R.layout.row_upcoming_events, view, false)!!
        /*val ll = eventLayout.findViewById<LinearLayout>(R.id.ll)
        val ivFavorite = eventLayout.findViewById<ImageView>(R.id.ivFavorite)
        val tvDesc = eventLayout.findViewById<TextView>(R.id.tvDesc)
        val tvTitle = eventLayout.findViewById<TextView>(R.id.tvTitle)
        val iv = eventLayout.findViewById<RoundedImageView>(R.id.iv)
//       imageView.setImageResource(data[position])

        Glide.with(context).load(data[position].image).into(iv)
        tvDesc.setText(
            Utils.formatDate(
                data[position].start!!,
                "yyyy-MM-dd HH:mm:ss",
                "dd MMM, yyyy '-' hh:mm a") +" | "+ data[position].event_type
        )
        tvTitle.setText(data[position].title)
        ll.setOnClickListener {
            context.startActivity(
                Intent(context, EventDetailActivity::class.java)
                    .putExtra("eventId", data[position].id.toString())
            )
        }

        if(data[position].is_favourite == 1){
            ivFavorite.setImageResource(R.drawable.ic_like_selected)
        }else{
            ivFavorite.setImageResource(R.drawable.ic_like_unselected)
        }

        ivFavorite.setOnClickListener {
            if(data[position].is_favourite == 1){
                ivFavorite.setImageResource(R.drawable.ic_like_unselected)
            }else{
                ivFavorite.setImageResource(R.drawable.ic_like_selected)
            }
            data[position].is_favourite = data[position].is_favourite!!.xor(1)
            listener(data[position])
            notifyDataSetChanged()
        }
        view.addView(eventLayout, 0)*/

        return eventLayout
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {}

    override fun saveState(): Parcelable? {
        return null
    }


}