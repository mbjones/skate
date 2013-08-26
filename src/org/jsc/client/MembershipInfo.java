package org.jsc.client;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A serializable class that describes the information needed to create one or
 * more memberships in the database.  The requests are indexed by the id of the
 * person, so only one request can be made for a given person.
 * @author Matt Jones
 */
public class MembershipInfo implements Serializable {
    private HashMap<Long, MembershipType> membershipRequests;
    
    /** 
     * Construct a registration results object to be populated with accessors.
     */
    public MembershipInfo() {
        membershipRequests = new HashMap<Long, MembershipType>();
    }

    /**
     * @return the membershipRequests
     */
    public HashMap<Long, MembershipType> getMembershipRequests() {
        return membershipRequests;
    }

    /**
     * @param pid the identifier of the person for the membership
     * @param mt the type of membership to add
     */
    public void add(Long pid, MembershipType mt) {
        this.membershipRequests.put(pid, mt);
    }
    
    /**
     * @param pid the identifier of the person for the membership
     * @param mt the type of membership to add
     */
    public void remove(Long pid) {
        this.membershipRequests.remove(pid);
    }
    
    /**
     * @return the number of memberships requested in this object
     */
    public int size() {
        return membershipRequests.size();
    }
    
    public Set<Entry<Long, MembershipType>> entrySet() {
        return membershipRequests.entrySet();
    }
}
