package it.carmelolagamba.saveyourtime.service.streaming

/**
 * @author carmelolg
 * @since version 1.0
 */
class EventNotifier {

    private val listeners: MutableSet<EventListener> = HashSet()

    companion object {

        private var _instance: EventNotifier? = null

        /**
         * Get the singleton of EventNotifier
         * @return an instance of EventNotifier
         */
        fun getInstance(): EventNotifier {

            if (_instance == null) {
                _instance = EventNotifier()
            }

            return _instance!!
        }
    }

    /**
     * Add a new listener
     * @param eventListener The EventListener implementation
     */
    fun addListener(eventListener: EventListener) {
        listeners.add(eventListener)
    }

    /**
     * Remove a new listener
     * @param eventListener The EventListener implementation
     */
    fun removeListener(eventListener: EventListener) {
        listeners.remove(eventListener)
    }

    /**
     * Send a new message/event to all listener
     * @param channel The event name
     */
    fun notifyEvent(channel: String) {
        listeners.forEach {
            it.onEvent(channel)
        }
    }
}