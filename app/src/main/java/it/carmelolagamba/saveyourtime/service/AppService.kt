package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.SaveYourTimeApplication
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import javax.inject.Inject

/**
 * @author carmelolg
 * @since version 1.0
 */
class AppService @Inject constructor() {

    @Inject
    lateinit var utilService: UtilService

    /**
     *
     * @return all applications (in List) checked by the user
     */
    fun findAllChecked(): List<App> {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao()
            .getAllActive()
    }

    /**
     *
     * @return all applications (in List) unchecked
     */
    fun findAllUnchecked(): List<App> {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao()
            .getAllUnchecked()
    }

    /**
     * Reset
     */
    fun resetOldData() {

        val apps: List<App> = findAllChecked()
        apps.forEach { app ->
            if (app.lastUpdate < utilService.todayMidnightMillis()) {
                app.todayUsage = 0
                upsert(app)
            }
        }

        val appsUnchecked: List<App> = findAllUnchecked()
        appsUnchecked.forEach { app ->
            app.todayUsage = 0
            upsert(app)
        }
    }

    /**
     * @param packageName the app package
     * @return the App object with the package = packageName
     */
    fun findByPackageName(packageName: String): App {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao()
            .getByPackageName(packageName)
    }

    /**
     * Insert a new app on DB
     * @param app the App to insert
     */
    fun insert(app: App) {
        DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().insertAll(app)
    }

    /**
     * @param app the application to update
     * @return the app updated
     */
    fun upsert(app: App): App {
        return run {
            app.lastUpdate = System.currentTimeMillis()
            DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().update(app)
            app
        }
    }

    /**
     * Reset all data on table "application"
     * This method doesn't remove all data from DB but it's only a logical deletion
     */
    fun resetAll() {
        val apps: List<App> =
            DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().getAll()
        apps.forEach { app ->
            app.notifyTime = 60
            app.todayUsage = 0
            app.selected = false
            upsert(app)
        }
    }

    /**
     * Reset all current app usage from the DB
     */
    fun resetAllUsages() {
        val apps: List<App> =
            DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().getAll()
        apps.forEach { app ->
            app.todayUsage = 0
            upsert(app)
        }
    }

    /**
     * @param packageName the package name
     * @return the application name
     */
    fun findNameByPackageName(packageName: String): String {
        return findByPackageName(packageName).name
    }

    /**
     * @return a List of applications that exceeded the time usage chose by the user
     */
    fun findExceededApplication(apps: List<App> = findAllChecked()): List<App> {
        return apps.filter { app -> app.todayUsage >= app.notifyTime }
    }

}