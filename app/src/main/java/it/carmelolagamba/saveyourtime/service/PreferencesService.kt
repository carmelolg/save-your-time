package it.carmelolagamba.saveyourtime.service

import android.util.Log
import it.carmelolagamba.saveyourtime.SaveYourTimeApplication
import it.carmelolagamba.saveyourtime.persistence.DBFactory
import it.carmelolagamba.saveyourtime.persistence.Preferences
import javax.inject.Inject

class PreferencesService @Inject constructor() {

    fun findAll(): List<Preferences> {
        return DBFactory.getDatabase(SaveYourTimeApplication.context).preferencesDao().getAll()
    }

    fun isAppBlockEnabled(): Boolean {
        val preferences = findPreferenceByKey(BLOCK_APP)
        return preferences?.value?.toBoolean() ?: false
    }

    fun isAppReminderEnabled(): Boolean {
        val preferences = findPreferenceByKey(REMINDER_EXPIRED_CHECK)
        return preferences?.value?.toBoolean() ?: false
    }

    fun findAppReminderTimePreference(): Int {
        val preferences = findPreferenceByKey(REMINDER_EXPIRED_TIME)
        return preferences?.value?.toInt() ?: 1
    }

    fun updateAppBlockPreference(isBlock: Boolean) {
        updateByValue(BLOCK_APP, isBlock.toString())
    }

    fun updateAppReminderPreference(isActive: Boolean) {
        updateByValue(REMINDER_EXPIRED_CHECK, isActive.toString())
    }

    fun updateAppReminderTimePreference(time: String) {
        updateByValue(REMINDER_EXPIRED_TIME, time)
    }

    fun resetAll() {
        DBFactory.getDatabase(SaveYourTimeApplication.context).preferencesDao().deleteAll()
    }

    private fun findPreferenceByKey(key: String): Preferences? {
        return try {
            DBFactory.getDatabase(SaveYourTimeApplication.context).preferencesDao().getByKey(key)
        } catch (ex: NoSuchElementException) {
            Log.d("SYT", "No preference setted for $key")
            null
        }
    }

    /**
     * @param preferences the preferences to update
     * @return the preferences updated
     */
    private fun upsert(preferences: Preferences): Preferences {
        return run {
            DBFactory.getDatabase(SaveYourTimeApplication.context).preferencesDao()
                .update(preferences)
            preferences
        }
    }

    private fun updateByValue(key: String, value: String) {
        val preference: Preferences? = findPreferenceByKey(key)

        if (preference != null) {
            preference.value = value
            upsert(preference)
        } else {
            insert(Preferences(null, key, value))
        }
    }

    private fun insert(preferences: Preferences) {
        DBFactory.getDatabase(SaveYourTimeApplication.context).preferencesDao()
            .insertAll(preferences)
    }

    companion object {

        @JvmStatic
        val BLOCK_APP: String = "app_blocked"

        @JvmStatic
        val REMINDER_EXPIRED_CHECK: String = "app_expired_reminder_check"

        @JvmStatic
        val REMINDER_EXPIRED_TIME: String = "app_expired_reminder_time"
    }

}