package org.jsc.client;

import java.io.Serializable;

/**
 * A model of the Membership types.  A MembershipType represents a single
 * class of membership in the club, and is associated with a particular cost
 * for registration, as well as metadata for displaying the type.
 * .
 * @author Matt Jones
 */
public class MembershipType implements Serializable {
    private String typeName;
    private String membershipType;
    private String description;
    private double cost;
    
    /**
     * Construct a new type, using accessors to set all fields after construction.
     */
    public MembershipType() {
        
    }

    /**
     * Construct a MembershipType instance using provided data.
     * @param typeName The unique name of this membership type 
     * @param membershipType The class of this membership (e.g., JSC_SINGLE)
     * @param description A descriptive label for this type 
     * @param cost The cost of joining under this membership type
     */
    public MembershipType(String typeName, String membershipType, String description, double cost) {
        super();
        this.typeName = typeName;
        this.membershipType = membershipType;
        this.description = description;
        this.cost = cost;
    }

    /**
     * @return the typeName
     */
    public String getTypeName() {
        return typeName;
    }

    /**
     * @return the membershipType
     */
    public String getMembershipType() {
        return membershipType;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param typeName the typeName to set
     */
    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    /**
     * @param membershipType the membershipType to set
     */
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }
}
