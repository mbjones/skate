package org.jsc.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler interface that responds to RosterChangeEvents.
 * @author Matt Jones
 *
 */
public interface RosterChangeHandler extends EventHandler {
    /**
     * Called whenever the roster changes.
     * @param event the change event containing the list of changed RosterEntry objects
     */
    void onRosterChange(RosterChangeEvent event);
}
