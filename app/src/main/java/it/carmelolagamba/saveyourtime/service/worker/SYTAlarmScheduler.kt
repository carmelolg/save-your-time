package it.carmelolagamba.saveyourtime.service.worker

/**
 * @author carmelolg
 * @since version 1.2.3
 */
interface SYTAlarmScheduler {
        fun schedule(alarmItem: SYTAlarmItem)
        fun cancel(alarmItem: SYTAlarmItem)
}