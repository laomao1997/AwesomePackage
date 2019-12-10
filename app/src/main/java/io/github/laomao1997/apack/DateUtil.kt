package io.github.laomao1997.apack

import android.content.Context
import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.*

object DateUtil {

    private val simpleDateFormatYyyyMMddHHhMmSs: SimpleDateFormat =
        SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA)
    private val simpleDateFormatShiFen: SimpleDateFormat =
        SimpleDateFormat("HH:mm", Locale.CHINA)

    fun getChinaDateTime(context: Context, arabDateStr: String): String {
        val date: Date? = simpleDateFormatYyyyMMddHHhMmSs.parse(arabDateStr)
        date?.let {
            if (DateUtils.isToday(date.time)) {
                return "今天 ${simpleDateFormatShiFen.format(date)}"
            }
            return DateUtils.formatDateTime(
                context, date.time, DateUtils.FORMAT_SHOW_YEAR or
                        DateUtils.FORMAT_SHOW_DATE or
                        DateUtils.FORMAT_SHOW_WEEKDAY or
                        DateUtils.FORMAT_SHOW_TIME
            )
        }
        return arabDateStr
    }

}