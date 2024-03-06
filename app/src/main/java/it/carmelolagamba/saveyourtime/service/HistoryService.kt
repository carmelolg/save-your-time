package it.carmelolagamba.saveyourtime.service

import android.content.Context
import javax.inject.Inject


/**
 * @author carmelolg
 * @since version 1.0
 */
class HistoryService @Inject constructor() {

    @Inject
    lateinit var utilService: UtilService

    /**
     * @param context the application context
     * @param packageName the package name of the app
     * @param week the week you want analyze. 0 current, -1 last week, +1 next week and so on
     * @return a map with all weekly usage (key = day name, value = the usage in minutes)
     */
    fun getWeeklyDetails(context: Context, packageName: String, week: Int = 0): Map<String, Int> {
        val monday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.MONDAY).week(week).build()
        )
        val tuesday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.TUESDAY).week(week).build()
        )
        val wednesday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.WEDNESDAY).week(week).build()
        )
        val thursday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.THURSDAY).week(week).build()
        )
        val friday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.FRIDAY).week(week).build()
        )
        val saturday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.SATURDAY).week(week).build()
        )
        val sunday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.SUNDAY).week(week).build()
        )
        val nextMonday = utilService.getMidnight(
            UtilService.TimeParams().weekday(UtilService.Weekday.MONDAY).week(week + 1).build()
        )

        val weeklyMap = mutableMapOf<String, Int>()

        weeklyMap[UtilService.Weekday.MONDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, monday, tuesday)
        weeklyMap[UtilService.Weekday.TUESDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, tuesday, wednesday)
        weeklyMap[UtilService.Weekday.WEDNESDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, wednesday, thursday)
        weeklyMap[UtilService.Weekday.THURSDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, thursday, friday)
        weeklyMap[UtilService.Weekday.FRIDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, friday, saturday)
        weeklyMap[UtilService.Weekday.SATURDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, saturday, sunday)
        weeklyMap[UtilService.Weekday.SUNDAY.name] =
            utilService.getUsageInMinutesByPackage(context, packageName, sunday, nextMonday)

        return weeklyMap

    }

    /**
     * @param context the application context
     * @param packageName the package name of the app
     * @param currentWeekMap the data about this week
     * @param lastWeekMap the data about last week
     * @return a map with the last seven days usage (key = the index, value = Pair(first = the day label, second = the usage in minutes))
     */
    fun getLast7DaysUsage(
        context: Context,
        packageName: String,
        currentWeekMap: Map<String, Int> = getWeeklyDetails(context, packageName),
        lastWeekMap: Map<String, Int> = getWeeklyDetails(context, packageName, -1)
    ): Map<Int, Pair<String, Int>> {

        val last7DaysMap = mutableMapOf<Int, Pair<String, Int>>()
        val currentDay = utilService.getCurrentDay()
        val currentDayIndex = currentDay.ordinal

        last7DaysMap[7] = Pair(
            currentDay.name,
            currentWeekMap[currentDay.name]!!
        )

        for (i in 1..6) {

            val prev = utilService.prev(currentDay, i)

            if (prev.ordinal == 1) {
                // Manage sunday
                last7DaysMap[7 - i] = Pair(prev.name, lastWeekMap[prev.name]!!)
            } else if (prev.ordinal <= currentDayIndex) {
                last7DaysMap[7 - i] = Pair(prev.name, currentWeekMap[prev.name]!!)
            } else {
                last7DaysMap[7 - i] = Pair(prev.name, lastWeekMap[prev.name]!!)
            }
        }

        return last7DaysMap
    }

    /**
     * @param context the application context
     * @param packageName the package name of the app
     * @return the yesterday usage of a specified app by package name
     */
    fun getYesterdayUsage(context: Context, packageName: String): Int {
        val yesterdayMidnight = utilService.yesterdayMidnightMillis()
        val todayMidnight = utilService.todayMidnightMillis()

        return utilService.getUsageInMinutesByPackage(
            context,
            packageName,
            yesterdayMidnight,
            todayMidnight
        )
    }

}