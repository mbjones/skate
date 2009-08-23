package org.jsc.client.event;

import com.google.gwt.event.shared.EventHandler;

public interface SkatingClassChangeHandler extends EventHandler {
    void onClassChange(SkatingClassChangeEvent event);
}
