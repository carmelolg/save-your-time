package it.carmelolagamba.saveyourtime.service

import android.util.Log
import it.carmelolagamba.saveyourtime.SaveYourTimeApplication
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import it.carmelolagamba.saveyourtime.persistence.Event
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class EventService @Inject constructor() {

    @Inject
    lateinit var utilService: UtilService

    /**
     * @return an event List with the events currently active
     */
    fun findAllActive(): List<Event> {
        val lastMidnight: Long = utilService.todayMidnightMillis()
        return DBFactory.getDatabase(SaveYourTimeApplication.context).eventDao()
            .getAllActive(lastMidnight)
    }

    /**
     * @param appId the application id, so the package name
     * @return the last event linked to the app
     */
    fun findEventByPackageName(appId: String, events: List<Event> = findAllActive()): Event? {
        return try {
            events.last { event: Event -> event.appId == appId }
        } catch (ex: NoSuchElementException) {
            Log.d("SYT", "No event generated for $appId")
            null
        }
    }

    /**
     * @param event the event to add
     */
    fun insert(event: Event) {
        DBFactory.getDatabase(SaveYourTimeApplication.context).eventDao().insertAll(event)
    }

    /**
     * @param event the event to update
     * @return the event updated
     */
    fun upsert(event: Event, usageAtEvent: Int = event.usageAtEvent): Event {
        event.insertDate = System.currentTimeMillis()
        event.usageAtEvent = usageAtEvent
        return run {
            DBFactory.getDatabase(SaveYourTimeApplication.context).eventDao().update(event)
            event
        }
    }

    /**
     * Delete all data from the table "event"
     */
    fun resetAll() {
        DBFactory.getDatabase(SaveYourTimeApplication.context).eventDao().deleteAll()
    }

    /**
     * Delete the oldest data from the DB
     * A row is old when the insertDate it's before the current midnight
     */
    fun cleanDB() {
        val lastMidnight: Long = utilService.todayMidnightMillis()
        DBFactory.getDatabase(SaveYourTimeApplication.context).eventDao().cleanDB(lastMidnight)
    }

}