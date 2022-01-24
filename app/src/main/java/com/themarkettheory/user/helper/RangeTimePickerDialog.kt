import android.app.TimePickerDialog
import android.content.Context
import android.widget.TimePicker
import java.lang.reflect.Field
import java.text.DateFormat
import java.util.*


class RangeTimePickerDialog(
    context: Context?,
    themeStyle: Int,
    callBack: OnTimeSetListener?,
    hourOfDay: Int,
    minute: Int,
    is24HourView: Boolean,
    private val isPastTimeValidation: Boolean
) :
    TimePickerDialog(context, themeStyle, callBack, hourOfDay, minute, is24HourView) {
    private var minHour = -1
    private var minMinute = -1
    private var maxHour = 24
    private var maxMinute = 60
    private var currentHour = 0
    private var currentMinute = 0
    private val calendar: Calendar = Calendar.getInstance()
    private val dateFormat: DateFormat
    fun setMin(hour: Int, minute: Int) {
        minHour = hour
        minMinute = minute
    }

    fun setMax(hour: Int, minute: Int) {
        maxHour = hour
        maxMinute = minute
    }

    override fun onTimeChanged(view: TimePicker, hourOfDay: Int, minute: Int) {
        var validTime = true
        if (isPastTimeValidation) {
            if (hourOfDay < minHour || hourOfDay == minHour && minute < minMinute) {
                validTime = false
            }
            if (hourOfDay > maxHour || hourOfDay == maxHour && minute > maxMinute) {
                validTime = false
            }
        }
        if (validTime) {
            currentHour = hourOfDay
            currentMinute = minute
        }
        updateTime(currentHour, currentMinute)
        updateDialogTitle(view, currentHour, currentMinute)
    }

    private fun updateDialogTitle(timePicker: TimePicker, hourOfDay: Int, minute: Int) {
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
        calendar.set(Calendar.MINUTE, minute)
        val title: String = dateFormat.format(calendar.time)
        setTitle(title)
    }

    init {
        currentHour = hourOfDay
        currentMinute = minute
        dateFormat = DateFormat.getTimeInstance(DateFormat.SHORT)

        try {
            val superclass: Class<*> = javaClass.superclass
            val mTimePickerField: Field = superclass.getDeclaredField("mTimePicker")
            mTimePickerField.isAccessible = true
            val mTimePicker = mTimePickerField.get(this) as TimePicker
            mTimePicker.setOnTimeChangedListener(this)
        } catch (e: NoSuchFieldException) {
        } catch (e: IllegalArgumentException) {
        } catch (e: IllegalAccessException) {
        }
    }
}