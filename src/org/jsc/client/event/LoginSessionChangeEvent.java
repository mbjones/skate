package org.jsc.client.event;

import java.util.List;

import org.jsc.client.RosterEntry;
import org.jsc.client.SessionSkatingClass;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that is created when the fields of the LoginSession class registered
 * with the main UI class change.  It is used to post events that allow the user 
 * interface to reflect new Login changes.
 * 
 * @author Matt Jones
 *
 */
public class LoginSessionChangeEvent extends GwtEvent<LoginSessionChangeHandler> {

    /**
     * Create a new event that indicates that the login session has changed.
     */
    public LoginSessionChangeEvent() {
        super();
    }

    public static final GwtEvent.Type<LoginSessionChangeHandler> TYPE = new GwtEvent.Type<LoginSessionChangeHandler>();
    
    @Override
    protected void dispatch(LoginSessionChangeHandler handler) {
        handler.onLoginSessionChange(this);
    }

    @Override
    public GwtEvent.Type<LoginSessionChangeHandler> getAssociatedType() {
        return TYPE;
    }
}
