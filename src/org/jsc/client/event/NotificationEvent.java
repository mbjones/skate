package org.jsc.client.event;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that is created when a component needs to notify the user of an
 * important message due to activity in the application.
 * 
 * @author Matt Jones
 *
 */
public class NotificationEvent extends GwtEvent<NotificationHandler> {
    private final String message;
    private final boolean shouldClearMessage;

    /**
     * Create a new event that indicates that a message should be posted to the user
     * @param message the message to be posted to the user
     */
    public NotificationEvent(String message) {
        super();
        this.message = message;
        this.shouldClearMessage = false;
    }

    /**
     * Create a new event that indicates that a message should be cleared from the UI
     * @param shouldClearMessage set to true if the message UI should be cleared
     */
    public NotificationEvent(boolean shouldClearMessage) {
        super();
        this.message = "";
        this.shouldClearMessage = shouldClearMessage;
    }
    
    public static final GwtEvent.Type<NotificationHandler> TYPE = new GwtEvent.Type<NotificationHandler>();
    
    @Override
    protected void dispatch(NotificationHandler handler) {
        handler.onNotification(this);
    }

    @Override
    public GwtEvent.Type<NotificationHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Retrieve the message to be posted
     * @return String the message to be displayed in the UI
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return the shouldClearMessage
     */
    public boolean shouldClearMessage() {
        return shouldClearMessage;
    }

}
