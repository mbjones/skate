package org.jsc.client;

import java.util.ArrayList;

import org.jsc.client.event.LoginSessionChangeEvent;
import org.jsc.client.event.LoginSessionChangeHandler;
import org.jsc.client.event.RosterChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A Model that stores the list of students in a single class, or a list of 
 * classes for a single student.
 *  
 * @author Matt Jones
 */
public class RosterModel {
    private ArrayList<RosterEntry> roster;
    private SkaterRegistrationServiceAsync regService;
    private LoginSession loginSession;
    private HandlerManager eventBus;
        
    /**
     * Construct the class list model by registering the event bus to be used and the 
     * login session to be used in contacting the database.
     * @param eventBus the bus to publish change events
     * @param loginSession that contains credentials for logging into the remote RPC service
     */
    public RosterModel(HandlerManager eventBus, LoginSession loginSession) {
        this.loginSession = loginSession;
        this.eventBus = eventBus;
        
        // Register as a handler for LoginSession changes, and handle those changes
        // by updating the header appropriately
        eventBus.addHandler(LoginSessionChangeEvent.TYPE, new LoginSessionChangeHandler() {
            public void onLoginSessionChange(LoginSessionChangeEvent event) {
                refreshRoster();
            }
        });
    }
    
    /**
     * Look up the current list of classes from the registration servlet and
     * store it for use in the UI later.
     */
    protected void refreshRoster() {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<ArrayList<RosterEntry>> callback = new AsyncCallback<ArrayList<RosterEntry>>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to get roster.", caught);
            }

            public void onSuccess(ArrayList<RosterEntry> newRoster) {
                if (newRoster == null) {
                    // Failure on the remote end.
                    GWT.log("Error finding the roster.", null);
                    return;
                } else {
                    // Record the roster, and notify listeners that it has changed
                    roster = newRoster;
                    RosterChangeEvent event = new RosterChangeEvent(roster);
                    eventBus.fireEvent(event);
                }
            }
        };

        // Make the call to the registration service.
        regService.getStudentRoster(loginSession, loginSession.getPerson(), callback);
    }

    /**
     * @return the roster
     */
    public ArrayList<RosterEntry> getRoster() {
        return roster;
    }

    /**
     * @param roster the roster to set
     */
    public void setRoster(ArrayList<RosterEntry> roster) {
        this.roster = roster;
    }
    
    /**
     * Determine if this roster contains an entry for the given student and class
     * @param pid the student identifier to look up
     * @param classid the class identifier to look up
     * @return true if the student pid is registered in the class classid
     */
    public boolean contains(long pid, long classid) {
        boolean isRegistered = false;
        
        // This could be done more efficiently if we kept a hash of the roster
        // keyed on classid/pid, but for now this loop will do.
        if (roster != null) {
            for (RosterEntry entry : roster) {
                if (entry.getClassid() == classid && entry.getPid() == pid) {
                    isRegistered = true;
                    break;
                }
            }
        }
        return isRegistered;
    }
}
