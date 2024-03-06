package it.carmelolagamba.saveyourtime.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.Calendar
import java.util.Locale
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class UtilService @Inject constructor() {

    enum class Weekday {
        NO_DAY,
        SUNDAY,
        MONDAY,
        TUESDAY,
        WEDNESDAY,
        THURSDAY,
        FRIDAY,
        SATURDAY,
    }

    /**
     * @param weekday the current day expressed in Weekday object
     * @return the previous day from the weekday
     */
    fun prev(weekday: Weekday): Weekday {
        return if (weekday == Weekday.SUNDAY) {
            Weekday.SATURDAY
        } else {
            Weekday.entries[weekday.ordinal - 1]
        }
    }

    /**
     * @param weekday the current day expressed in Weekday object
     * @return the next day from the weekday
     */
    fun next(weekday: Weekday): Weekday {
        return if (weekday == Weekday.SATURDAY) {
            Weekday.SUNDAY
        } else {
            Weekday.entries[weekday.ordinal + 1]
        }
    }

    /**
     * @param weekday the current day expressed in Weekday object
     * @param offset the integer that represent how much days you want to go in back
     * @return the prev day from the weekday
     */
    fun prev(weekday: Weekday, offset: Int): Weekday {
        var currentElement = weekday
        var tmpElement: Weekday
        for (i in 1..offset) {
            tmpElement = prev(currentElement)
            currentElement = tmpElement
        }
        return currentElement
    }

    /**
     * @param weekday the current day expressed in Weekday object
     * @param offset the integer that represent how much days you want to go in ahead
     * @return the next day from the weekday
     */
    fun next(weekday: Weekday, offset: Int): Weekday {
        var currentElement = weekday
        var tmpElement: Weekday
        for (i in 1..offset) {
            tmpElement = next(currentElement)
            currentElement = tmpElement
        }
        return currentElement
    }

    /**
     * Builder for TimeParams object
     */
    class TimeParams {
        var week: Int = 0
        var tomorrow: Boolean = false
        var weekday: Weekday = Weekday.NO_DAY

        /**
         * @param week 0 if you want use this week, -1 the previous week, +1 the next week and so on
         */
        fun week(week: Int): TimeParams {
            this.week = week
            return this
        }

        /**
         * @param tomorrow true if you want to perform tomorrow midnight
         */
        fun tomorrow(tomorrow: Boolean): TimeParams {
            this.tomorrow = tomorrow
            return this
        }

        /**
         * @param weekday add Weekday param for calculate time of the chose weekday
         */
        fun weekday(weekday: Weekday): TimeParams {
            this.weekday = weekday
            return this
        }

        /**
         * Build the TimeParams object
         * @return TimeParams objects
         */
        fun build(): TimeParams {
            return this
        }
    }

    /**
     * @return the millis value of current midnight
     */
    fun todayMidnightMillis(): Long {
        val midnight: Calendar = Calendar.getInstance()
        midnight.set(Calendar.HOUR_OF_DAY, 0)
        midnight.set(Calendar.MINUTE, 0)
        midnight.set(Calendar.SECOND, 0)
        midnight.set(Calendar.MILLISECOND, 0)

        return midnight.timeInMillis
        //return getMidnight()
    }

    /**
     * @return the millis value of tomorrow midnight
     */
    fun tomorrowMidnightMillis(): Long {
        val tomorrow: Calendar = Calendar.getInstance()
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)

        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        return tomorrow.timeInMillis
        //return getMidnight(TimeParams().tomorrow(true).build())
    }

    /**
     * @return the millis value of yesterday midnight
     */
    fun yesterdayMidnightMillis(): Long {
        val tomorrow: Calendar = Calendar.getInstance()
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)

        tomorrow.add(Calendar.DAY_OF_YEAR, -1)

        return tomorrow.timeInMillis
    }

    /**
     * @return true if current time is between 0:00 and 0:20 (current time in local GMT)
     */
    fun isNearMidnight(): Boolean {

        val now: Long = System.currentTimeMillis()
        val midnight: Long = todayMidnightMillis()

        /** If the difference between now and current midnight its less than 20 (minutes) * 60 (seconds) * 1000 (millis) return true else false */
        return now - midnight <= 20 * 60 * 1000
    }

    /**
     * Calculate the midnight by TimeParams
     * @return the midnight in millis
     */
    fun getMidnight(timeParams: TimeParams? = null): Long {
        val midnight: Calendar = Calendar.getInstance()

        midnight.set(Calendar.HOUR_OF_DAY, 0)
        midnight.set(Calendar.MINUTE, 0)
        midnight.set(Calendar.SECOND, 0)
        midnight.set(Calendar.MILLISECOND, 0)

        if (timeParams != null) {
            if (timeParams.tomorrow) {
                midnight.add(Calendar.DAY_OF_YEAR, 1)
            }
            if (timeParams.weekday != Weekday.NO_DAY) {
                midnight[Calendar.DAY_OF_WEEK] = timeParams.weekday.ordinal
            }
            if (timeParams.week != 0) {
                midnight.add(Calendar.WEEK_OF_YEAR, timeParams.week)
            }
        }


        return midnight.timeInMillis
    }

    /**
     * @return the today Weekday value
     */
    fun getCurrentDay(): Weekday {
        return Weekday.valueOf(LocalDate.now().dayOfWeek.name)
    }


    /**
     * @param context the application context
     * @param packageName the package of the app that you want get the total daily usage
     * @param start millis of start time of the range @default today's midnight
     * @param end millis of end time of the range @default tomorrow's midnight
     * @return the total usage in minutes
     */
    fun getUsageInMinutesByPackage(
        context: Context,
        packageName: String,
        start: Long = todayMidnightMillis(),
        end: Long = tomorrowMidnightMillis()
    ): Int {

        val statsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager

        val usageEvents: UsageEvents = statsManager.queryEvents(
            start,
            end
        )

        var singleEventUsage = 0L
        var totalUsage = 0L

        while (usageEvents.hasNextEvent()) {
            val currentEvent: UsageEvents.Event = UsageEvents.Event()
            usageEvents.getNextEvent(currentEvent)

            if (packageName == currentEvent.packageName) {
                val time = currentEvent.timeStamp

                if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED
                    || currentEvent.eventType == UsageEvents.Event.ACTIVITY_PAUSED
                ) {
                    if (currentEvent.eventType == UsageEvents.Event.ACTIVITY_RESUMED) {
                        singleEventUsage = time
                    } else {
                        if (singleEventUsage > 0) {
                            totalUsage = totalUsage.plus(time - singleEventUsage)
                        }
                        singleEventUsage = 0L
                    }
                }
            }
        }
        if (singleEventUsage > 0) {
            totalUsage += if (end == tomorrowMidnightMillis()) {
                (System.currentTimeMillis() - singleEventUsage)
            } else {
                (end - singleEventUsage)
            }
        }

        return (totalUsage / 1000 / 60).toInt()
    }


    /**
     * Return a date with the pattern dd/M from today
     * @param daysAgo how much day you want go back?
     * @return the string date format
     */
    fun getDateFromToday(offset: Int): String {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, offset)

        val pattern = "dd/MM"
        return SimpleDateFormat(pattern, Locale.getDefault()).format(calendar.time)
    }

}