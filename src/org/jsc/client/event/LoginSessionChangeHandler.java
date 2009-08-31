package org.jsc.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler interface that responds to LoginSessionChangeEvents.
 * @author Matt Jones
 *
 */
public interface LoginSessionChangeHandler extends EventHandler {
    /**
     * Called whenever the LoginSession changes.
     * @param event the change event, which is simply a marker
     */
    void onLoginSessionChange(LoginSessionChangeEvent event);
}
