package org.jsc.client;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * A serializable class that describes the information needed to create a
 * membership in the database.
 * @author Matt Jones
 */
public class MembershipInfo implements Serializable {
    private String membershipType;
    private ArrayList<Long> memberIDList;
    
    /** 
     * Construct a registration results object to be populated with accessors.
     */
    public MembershipInfo() {
        memberIDList = new ArrayList<Long>();
    }

    /**
     * @return the membershipType
     */
    public String getMembershipType() {
        return membershipType;
    }

    /**
     * @param membershipType the membershipType to set
     */
    public void setMembershipType(String membershipType) {
        this.membershipType = membershipType;
    }

    /**
     * @return the memberIDList
     */
    public ArrayList<Long> getMemberIDList() {
        return memberIDList;
    }

    /**
     * @param memberIDList the memberIDList to set
     */
    public void addMemberID(Long memberID) {
        this.memberIDList.add(memberID);
    }
}
