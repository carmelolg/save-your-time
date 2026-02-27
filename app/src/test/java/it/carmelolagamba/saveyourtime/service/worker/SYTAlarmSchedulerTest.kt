package it.carmelolagamba.saveyourtime.service.worker

import org.junit.Assert.*
import org.junit.Test

class SYTAlarmSchedulerTest {

    @Test
    fun `SYTAlarmScheduler interface can be implemented`() {
        val scheduled = mutableListOf<SYTAlarmItem>()
        val cancelled = mutableListOf<SYTAlarmItem>()

        val scheduler = object : SYTAlarmScheduler {
            override fun schedule(alarmItem: SYTAlarmItem) {
                scheduled.add(alarmItem)
            }
            override fun cancel(alarmItem: SYTAlarmItem) {
                cancelled.add(alarmItem)
            }
        }

        val item = SYTAlarmItem("test_message")
        scheduler.schedule(item)

        assertEquals(1, scheduled.size)
        assertEquals(item, scheduled[0])
        assertTrue(cancelled.isEmpty())
    }

    @Test
    fun `SYTAlarmScheduler cancel removes correct item`() {
        val cancelled = mutableListOf<SYTAlarmItem>()

        val scheduler = object : SYTAlarmScheduler {
            override fun schedule(alarmItem: SYTAlarmItem) {}
            override fun cancel(alarmItem: SYTAlarmItem) {
                cancelled.add(alarmItem)
            }
        }

        val item = SYTAlarmItem("cancel_me")
        scheduler.cancel(item)

        assertEquals(1, cancelled.size)
        assertEquals(item, cancelled[0])
    }

    @Test
    fun `SYTAlarmScheduler schedule and cancel can be called with same item`() {
        val scheduled = mutableListOf<SYTAlarmItem>()
        val cancelled = mutableListOf<SYTAlarmItem>()

        val scheduler = object : SYTAlarmScheduler {
            override fun schedule(alarmItem: SYTAlarmItem) {
                scheduled.add(alarmItem)
            }
            override fun cancel(alarmItem: SYTAlarmItem) {
                cancelled.add(alarmItem)
            }
        }

        val item = SYTAlarmItem("alarm")
        scheduler.schedule(item)
        scheduler.cancel(item)

        assertEquals(1, scheduled.size)
        assertEquals(1, cancelled.size)
        assertEquals(item, scheduled[0])
        assertEquals(item, cancelled[0])
    }

    @Test
    fun `SYTAlarmScheduler can handle multiple items`() {
        val scheduled = mutableListOf<SYTAlarmItem>()

        val scheduler = object : SYTAlarmScheduler {
            override fun schedule(alarmItem: SYTAlarmItem) {
                scheduled.add(alarmItem)
            }
            override fun cancel(alarmItem: SYTAlarmItem) {}
        }

        val item1 = SYTAlarmItem("alarm_1")
        val item2 = SYTAlarmItem("alarm_2")
        val item3 = SYTAlarmItem("alarm_3")

        scheduler.schedule(item1)
        scheduler.schedule(item2)
        scheduler.schedule(item3)

        assertEquals(3, scheduled.size)
        assertEquals(item1, scheduled[0])
        assertEquals(item2, scheduled[1])
        assertEquals(item3, scheduled[2])
    }
}
