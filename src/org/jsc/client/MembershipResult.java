package org.jsc.client;

import java.io.Serializable;

/**
 * A serializable class that contains the result information from creating new
 * memberships via the createMembership() service.
 * @author Matt Jones
 */
public class MembershipResult implements Serializable {
    private static final long serialVersionUID = 1L;
    private boolean membershipAttempted;
    private boolean membershipCreated;
    private String membershipErrorMessage;
    private long membershipId;
    private long paymentId;
    private String membershipStatus;
    private String membershipType;
    
    /** 
     * Construct a registration results object to be populated with accessors.
     */
    public MembershipResult() {
    }
    
    /**
     * @return the membershipAttempted
     */
    public boolean isMembershipAttempted() {
        return membershipAttempted;
    }

    /**
     * @return the membershipCreated
     */
    public boolean isMembershipCreated() {
        return membershipCreated;
    }

    /**
     * @return the membershipErrorMessage
     */
    public String getMembershipErrorMessage() {
        return membershipErrorMessage;
    }

    /**
     * @return the membershipId
     */
    public long getMembershipId() {
        return membershipId;
    }

    /**
     * @return the paymentId
     */
    public long getPaymentId() {
        return paymentId;
    }

    /**
     * @return the membershipStatus
     */
    public String getMembershipStatus() {
        return membershipStatus;
    }

    /**
     * @return the membershipType
     */
    public String getMembershipType() {
        return membershipType;
    }

    /**
     * @param membershipAttempted the membershipAttempted to set
     */
    public void setMembershipAttempted(boolean membershipAttempted) {
        this.membershipAttempted = membershipAttempted;
    }

    /**
     * @param membershipCreated the membershipCreated to set
     */
    public void setMembershipCreated(boolean membershipCreated) {
        this.membershipCreated = membershipCreated;
    }

    /**
     * @param membershipErrorMessage the membershipErrorMessage to set
     */
    public void setMembershipErrorMessage(String membershipErrorMessage) {
        this.membershipErrorMessage = membershipErrorMessage;
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
     * @param membershipStatus the membershipStatus to set
     */
    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    /**
     * @param membershipType the membershipType to set
     */
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }
}
