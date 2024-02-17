package it.carmelolagamba.saveyourtime.service

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
     * @return true if the app is already notified to the final user, false otherwise
     */
    fun isAppNotified(appId: String): Boolean {
        return findAllActive().any { event: Event -> event.appId == appId && event.notified }
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
    fun upsert(event: Event): Event {
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