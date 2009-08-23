package org.jsc.client.event;

import com.google.gwt.event.shared.EventHandler;

/**
 * A handler interface that manages SkatingClassChangeEvents.
 * @author Matt Jones
 *
 */
public interface SkatingClassChangeHandler extends EventHandler {
    /**
     * Called whenever the list of skating classes changes.
     * @param event the change event containing the list of changed classes
     */
    void onClassChange(SkatingClassChangeEvent event);
}
