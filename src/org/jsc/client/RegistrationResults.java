package org.jsc.client;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A serializable class that contains the result information from registering
 * for skating classes and creating memberships in the club.
 * @author Matt Jones
 */
public class RegistrationResults implements Serializable {
    private boolean membershipAttempted;
    private boolean membershipCreated;
    private String membershipErrorMessage;
    private long membershipId;
    private long paymentId;
    private ArrayList<RosterEntry> entriesCreated;
    private ArrayList<Long> entriesNotCreated;
    
    /** 
     * Construct a registration results object to be populated with accessors.
     */
    public RegistrationResults() {
    }

    /**
     * @return the membershipAttempted
     */
    public boolean isMembershipAttempted() {
        return membershipAttempted;
    }

    /**
     * @param membershipAttempted the membershipAttempted to set
     */
    public void setMembershipAttempted(boolean membershipAttempted) {
        this.membershipAttempted = membershipAttempted;
    }

    /**
     * @return the membershipCreated
     */
    public boolean isMembershipCreated() {
        return membershipCreated;
    }

    /**
     * @param membershipCreated the membershipCreated to set
     */
    public void setMembershipCreated(boolean membershipCreated) {
        this.membershipCreated = membershipCreated;
    }

    /**
     * @return the membershipErrorMessage
     */
    public String getMembershipErrorMessage() {
        return membershipErrorMessage;
    }

    /**
     * @param membershipErrorMessage the membershipErrorMessage to set
     */
    public void setMembershipErrorMessage(String membershipErrorMessage) {
        this.membershipErrorMessage = membershipErrorMessage;
    }

    /**
     * @return the membershipId
     */
    public long getMembershipId() {
        return membershipId;
    }

    /**
     * @param membershipId the membershipId to set
     */
    public void setMembershipId(long membershipId) {
        this.membershipId = membershipId;
    }

    /**
     * @param paymentId the paymentId to set
     */
    public void setPaymentId(long paymentId) {
        this.paymentId = paymentId;
    }

    /**
     * @return the paymentId
     */
    public long getPaymentId() {
        return paymentId;
    }

    /**
     * @return the entriesCreated
     */
    public ArrayList<RosterEntry> getEntriesCreated() {
        return entriesCreated;
    }

    /**
     * @param entriesCreated the entriesCreated to set
     */
    public void setEntriesCreated(ArrayList<RosterEntry> entriesCreated) {
        this.entriesCreated = entriesCreated;
    }

    /**
     * @return the entriesNotCreated
     */
    public ArrayList<Long> getEntriesNotCreated() {
        return entriesNotCreated;
    }

    /**
     * @param entriesNotCreated the entriesNotCreated to set
     */
    public void setEntriesNotCreated(ArrayList<Long> entriesNotCreated) {
        this.entriesNotCreated = entriesNotCreated;
    }
}
