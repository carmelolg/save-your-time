package it.carmelolagamba.saveyourtime.service

import java.util.Calendar
import javax.inject.Inject

class UtilService @Inject constructor(){


    @Inject
    lateinit var appService: AppService

    fun todayMidnightMillis(): Long {

        val midnight: Calendar = Calendar.getInstance()
        midnight.set(Calendar.HOUR_OF_DAY, 0)
        midnight.set(Calendar.MINUTE, 0)
        midnight.set(Calendar.SECOND, 0)
        midnight.set(Calendar.MILLISECOND, 0)

        return midnight.timeInMillis
    }

    fun tomorrowMidnightMillis(): Long {
        val tomorrow: Calendar = Calendar.getInstance()
        tomorrow.set(Calendar.HOUR_OF_DAY, 0)
        tomorrow.set(Calendar.MINUTE, 0)
        tomorrow.set(Calendar.SECOND, 0)
        tomorrow.set(Calendar.MILLISECOND, 0)

        tomorrow.add(Calendar.DAY_OF_YEAR, 1)

        return tomorrow.timeInMillis
    }

}