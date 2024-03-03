package it.carmelolagamba.saveyourtime.service

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.LocalDate
import java.util.Calendar
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

    class TimeParams {
        var week: Int = 0
        var tomorrow: Boolean = false
        var weekday: Weekday = Weekday.NO_DAY

        fun week(week: Int): TimeParams {
            this.week = week
            return this
        }

        fun tomorrow(tomorrow: Boolean): TimeParams {
            this.tomorrow = tomorrow
            return this
        }

        fun weekday(weekday: Weekday): TimeParams {
            this.weekday = weekday
            return this
        }

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
     * @return true if current time is between 0:00 and 0:20 (current time in local GMT)
     */
    fun isNearMidnight(): Boolean {

        val now: Long = System.currentTimeMillis()
        val midnight: Long = todayMidnightMillis()

        /** If the difference between now and current midnight its less than 20 (minutes) * 60 (seconds) * 1000 (millis) return true else false */
        return now - midnight <= 20 * 60 * 1000
    }

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

}