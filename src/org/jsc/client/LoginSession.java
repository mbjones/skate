package org.jsc.client;

import java.io.Serializable;


/**
 * A class representing the login session.  Most application operations should
 * only be performed if a valid session exists.  The session records whether
 * the user is logged in, and provides details about themselves as a Person.
 * @author Matt Jones
 */
public class LoginSession implements Serializable {

    private boolean isAuthenticated;
    private Person person;
    private String sessionId;
    
    /**
     * Construct a LoginSession to represent the user who is logged in. Initially
     * the session is invalid when constructed.
     */
    public LoginSession() {
        isAuthenticated = false;
        person = null;
        sessionId = "invalid";
    }
    
    /**
     * @return the true if the session has been authenticated
     */
    public boolean isAuthenticated() {
            return isAuthenticated;
    }

    /**
     * @param isAuthenticated the isAuthenticated to set
     */
    public void setAuthenticated(boolean isAuthenticated) {
        this.isAuthenticated = isAuthenticated;
        if (!isAuthenticated) {
            person = null;
            setSessionId("invalid");
        }
    }
    
    /**
     * @return the person
     */
    public Person getPerson() {
        return person;
    }
    
    /**
     * @param person the person to set
     */
    public void setPerson(Person person) {
        this.person = person;
    }

    /**
     * @return the sessionId
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * @param sessionId the sessionId to set
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}
