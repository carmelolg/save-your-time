package it.carmelolagamba.saveyourtime.service.streaming

/**
 * @author carmelolg
 * @since version 1.0
 */
interface EventListener {

    /**
     * The event is triggered when message is sent
     * This method it's overrided by all listener implementation
     * @param channel the event name
     */
    fun onEvent(channel: String)
}