package org.jsc.client;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

/**
 * A serializable class that describes the information needed to create one or
 * more memberships in the database.  The requests are indexed by the id of the
 * person, and multiple membership type requests can be made for each person.
 * @author Matt Jones
 */
public class MembershipRequests implements Serializable {
    private static final long serialVersionUID = 1L;
    private HashMap<Long, ArrayList<MembershipType>> membershipRequests;
    
    /** 
     * Construct a registration results object to be populated with accessors.
     */
    public MembershipRequests() {
        membershipRequests = new HashMap<Long, ArrayList<MembershipType>>();
    }

    /**
     * @param pid the identifier of the person for the membership
     * @param mt the type of membership to add
     */
    public void add(Long pid, MembershipType mt) {
        ArrayList<MembershipType> mtList = membershipRequests.get(pid);
        if (mtList == null) {
            mtList = new ArrayList<MembershipType>();
            membershipRequests.put(pid, mtList);
        }
        mtList.add(mt);
    }
    
    /**
     * @param pid the identifier of the person to be removed from the request
     * @param mt the MembershipType to be removed from the request
     */
    public void remove(Long pid, MembershipType mt) {
        ArrayList<MembershipType> mtList = membershipRequests.get(pid);
        if (mtList != null) {
            mtList.remove(mt);
            if (mtList.isEmpty()) {
                membershipRequests.remove(pid);
            }
        }
    }
    
    /**
     * @return the number of memberships requested in this object
     */
    public int size() {
        return membershipRequests.size();
    }
    
    /**
     * Return an iterable Set of each of the membership requests.  Each returned item
     * represents one person, and the value is an ArrayList containing a list of
     * MembershipType objects that have been requested for that person.
     * @return the EntrySet containing one entry for each person identifier
     */
    public Set<Entry<Long, ArrayList<MembershipType>>> entrySet() {
        return membershipRequests.entrySet();
    }
}
