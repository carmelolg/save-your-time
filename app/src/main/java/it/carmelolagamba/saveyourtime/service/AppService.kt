package it.carmelolagamba.saveyourtime.service

import it.carmelolagamba.saveyourtime.SaveYourTimeApplication
import it.carmelolagamba.saveyourtime.persistence.App
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import javax.inject.Inject

class AppService @Inject constructor() {

    fun findAllChecked(): List<App> {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().getAllActive()
    }

    fun findByPackageName(packageName: String): App {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().getByPackageName(packageName)
    }

    fun findIdByPackageName(packageName: String): Float {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().getByPackageName(packageName).id
    }

    fun insert(app: App) {
        DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().insertAll(app)
    }

    fun upsert(app: App): App {
        val entity : App = this.findByPackageName(app.packageName!!)
        return if(entity != null) {
            DBFactory.getDatabase(SaveYourTimeApplication.context).applicationDao().update(app)
            app
        }else {
            this.insert(app)
            app
        }
    }

    fun findNameByPackageName(packageName: String): String{
        return findByPackageName(packageName).name
    }
}