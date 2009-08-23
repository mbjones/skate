package org.jsc.client.event;

import java.util.List;

import org.jsc.client.RosterEntry;
import org.jsc.client.SessionSkatingClass;

import com.google.gwt.event.shared.GwtEvent;

/**
 * An event that is created when a Roster has been updated.  It
 * is used to post events that allow the user interface to reflect new roster changes.
 * 
 * @author Matt Jones
 *
 */
public class RosterChangeEvent extends GwtEvent<RosterChangeHandler> {
    private final List<RosterEntry> roster;

    /**
     * Create a new event that indicates that the list of skating classes has changed.
     * @param sessionClassList the list of classes that has been changed
     */
    public RosterChangeEvent(List<RosterEntry> roster) {
        super();
        this.roster = roster;
    }

    public static final GwtEvent.Type<RosterChangeHandler> TYPE = new GwtEvent.Type<RosterChangeHandler>();
    
    @Override
    protected void dispatch(RosterChangeHandler handler) {
        handler.onRosterChange(this);
    }

    @Override
    public GwtEvent.Type<RosterChangeHandler> getAssociatedType() {
        return TYPE;
    }

    /**
     * Retrieve the roster that was changed in this change event
     * @return List of RosterEntry objects that was changed
     */
    public List<RosterEntry> getRoster(){
        return roster;
    }

}
