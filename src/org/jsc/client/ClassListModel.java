package org.jsc.client;

import java.util.ArrayList;
import java.util.Iterator;

import org.jsc.client.event.SkatingClassChangeEvent;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * A Model that stores the list of classes for local access, and can synchronize
 * the list with the remote database as changes are made.
 * @author Matt Jones
 */
public class ClassListModel {
    private ArrayList<SessionSkatingClass> classList;
    private SkaterRegistrationServiceAsync regService;
    private LoginSession loginSession;
    private HandlerManager eventBus;
    
    /**
     * Construct the class list model by registering the event bus to be used and the 
     * login session to be used in contacting the database.
     * @param eventBus the bus to publish change events
     * @param loginSession that contains credentials for logging into the remote RPC service
     */
    public ClassListModel(HandlerManager eventBus, LoginSession loginSession) {
        this.loginSession = loginSession;
        this.eventBus = eventBus;
    }
    
    /**
     * Look up the current list of classes from the registration servlet and
     * store it for use in the UI later.
     */
    protected void refreshClassList() {
        // Initialize the service proxy.
        if (regService == null) {
            regService = GWT.create(SkaterRegistrationService.class);
        }

        // Set up the callback object.
        AsyncCallback<ArrayList<SessionSkatingClass>> callback = new AsyncCallback<ArrayList<SessionSkatingClass>>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to get list of classes.", caught);
            }

            public void onSuccess(ArrayList<SessionSkatingClass> list) {
                if (list == null) {
                    // Failure on the remote end.
                    GWT.log("Error finding the list of classes.", null);
                    return;
                } else {
                    // Assign the classList
                    classList = list;
                    SkatingClassChangeEvent event = new SkatingClassChangeEvent(classList);
                    eventBus.fireEvent(event);
                }
            }
        };

        // Make the call to the registration service.
        regService.getSessionClassList(loginSession.getPerson(), callback);
    }

    /**
     * Add a new JSC class to the list, and synchronize it with the database
     * @param jscClass
     */
    public void addClass(SessionSkatingClass jscClass) {
        classList.add(jscClass);
    }
    
    /**
     * Determine the number of classes in this model.
     * @return integer number of session classes
     */
    public int size() {
        return classList.size();
    }
    
    /**
     * Get an iterator for this list of classes to step over all elements.
     * @return iterator across all elements
     */
    public Iterator iterator() {
        return classList.iterator();
    }

    /**
     * @return the classList
     */
    public ArrayList<SessionSkatingClass> getClassList() {
        return classList;
    }

    /**
     * @param classList the classList to set
     */
    public void setClassList(ArrayList<SessionSkatingClass> classList) {
        this.classList = classList;
    }

}
