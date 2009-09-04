package org.jsc.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler interface that responds to NotificationEvents.
 * @author Matt Jones
 *
 */
public interface NotificationHandler extends EventHandler {
    /**
     * Called whenever the message to be displayed changes.
     * @param event the event containing the changed message
     */
    void onNotification(NotificationEvent event);
}
