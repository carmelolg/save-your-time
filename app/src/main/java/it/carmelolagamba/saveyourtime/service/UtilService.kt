package it.carmelolagamba.saveyourtime.service

import java.util.Calendar
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class UtilService @Inject constructor() {

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

}