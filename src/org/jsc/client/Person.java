package org.jsc.client;

import java.io.Serializable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * An encapsulation of a Person, with fields giving their basic demographic and
 * contact information.
 * @author Matthew Jones
 *
 */
public class Person implements Serializable {
    private long pid;
    private String fname;
    private String mname;
    private String lname;
    private String suffix;
    private String email;
    private boolean isEmailValid;
    private String bday;
    private int role;
    private String homephone;
    private String workphone;
    private String cellphone;
    private String street1;
    private String street2;
    private String city;
    private String state;
    private String zip;
    private String usfsaid;
    private String parentLastname;
    private String parentFirstname;
    private String parentEmail;
    private String username;
    private String newUsername;
    private String password;
    private String newPassword;
    private String newEmail;
    private boolean isMember;
    private long mid;
    private String membershipStatus;
    private long membershipPaymentId;
    private String maxLevel;
    
    // Predefined roles used in the application
    // Roles are cumulative (higher roles have all privileges of lower roles)
    public static int SKATER = 0;
    public static int COACH = 1;
    public static int BOARD = 2;
    public static int ADMIN = 3;
    
    /**
     * Construct a new person object with no fields set.
     */
    public Person() {
        
    }

    /**
     * Contruct a new person with the given name fields set.
     *
     * @param fname The first name of the person
     * @param mname The middle name of the person
     * @param lname The last name (surname) of the person
     */
    public Person(String fname, String mname, String lname) {
        this();
        this.setLname(lname);
        this.setMname(mname);
        this.setFname(fname);
    }
    
    /**
     * Output a readable String representation of the Person
     * @return String representing the person
     */
    public String toString() {
        /*
        private String username;
        private String suffix;
        private boolean isEmailValid;
        private String bday;
        private String role;
        private String homephone;
        private String workphone;
        private String cellphone;
        private String street1;
        private String street2;
        private String city;
        private String state;
        private String zip;
        private String usfsaid;
        private String parentSurname;
        private String parentFirstName;
        private String parentEmail;
        private String password1;
        private String password2;
        */
        StringBuffer sb = new StringBuffer();
        sb.append(pid);
        sb.append("\n");
        sb.append(fname).append(" ");
        sb.append(mname).append(" ");
        sb.append(lname).append("\n");
        sb.append(email).append("\n");
        
        return sb.toString();
    }
    
    /**
     * @return the bday
     */
    public String getBday() {
        return bday;
    }
    /**
     * @param bday the bday to set
     */
    public void setBday(String bday) {
        this.bday = bday;
    }
    /**
     * @return the cellphone
     */
    public String getCellphone() {
        return cellphone;
    }
    /**
     * @param cellphone the cellphone to set
     */
    public void setCellphone(String cellphone) {
        this.cellphone = cellphone;
    }
    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }
    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }
    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }
    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }
    /**
     * The newEmail represent an email address to be set in case of an update. It
     * is only used temporarily to transport a new email address to the server
     * so that the relational store can be updated.
     * @return the newEmail
     */
    public String getNewEmail() {
        return newEmail;
    }

    /**
     * @param newEmail the newEmail to set
     */
    public void setNewEmail(String newEmail) {
        this.newEmail = newEmail;
    }

    /**
     * @return the fname
     */
    public String getFname() {
        return fname;
    }
    /**
     * @param fname the fname to set
     */
    public void setFname(String fname) {
        this.fname = fname;
    }
    /**
     * @return the homephone
     */
    public String getHomephone() {
        return homephone;
    }
    /**
     * @param homephone the homephone to set
     */
    public void setHomephone(String homephone) {
        this.homephone = homephone;
    }
    /**
     * @return the isEmailValid
     */
    public boolean isEmailValid() {
        return isEmailValid;
    }
    /**
     * @param isEmailValid the isEmailValid to set
     */
    public void setEmailValid(boolean isEmailValid) {
        this.isEmailValid = isEmailValid;
    }
    /**
     * @return the lname
     */
    public String getLname() {
        return lname;
    }
    /**
     * @param lname the lname to set
     */
    public void setLname(String lname) {
        this.lname = lname;
    }
    /**
     * @return the mname
     */
    public String getMname() {
        return mname;
    }
    /**
     * @param mname the mname to set
     */
    public void setMname(String mname) {
        this.mname = mname;
    }
    /**
     * @return the parentEmail
     */
    public String getParentEmail() {
        return parentEmail;
    }
    /**
     * @param parentEmail the parentEmail to set
     */
    public void setParentEmail(String parentEmail) {
        this.parentEmail = parentEmail;
    }
    /**
     * @return the parentFirstName
     */
    public String getParentFirstname() {
        return parentFirstname;
    }
    /**
     * @param parentFirstName the parentFirstName to set
     */
    public void setParentFirstname(String parentFirstname) {
        this.parentFirstname = parentFirstname;
    }
    /**
     * @return the parentSurname
     */
    public String getParentLastname() {
        return parentLastname;
    }
    /**
     * @param parentSurname the parentSurname to set
     */
    public void setParentLastname(String parentLastname) {
        this.parentLastname = parentLastname;
    }
    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }
    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }
    /**
     * @return the new password to be set for this account
     */
    public String getNewPassword() {
        return newPassword;
    }
    /**
     * @param password the password to set on this account when settings are saved
     */
    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the newUsername
     */
    public String getNewUsername() {
        return newUsername;
    }

    /**
     * @param newUsername the newUsername to set
     */
    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    /**
     * @return the pid
     */
    public long getPid() {
        return pid;
    }
    /**
     * @param pid the pid to set
     */
    public void setPid(long pid) {
        this.pid = pid;
    }
    /**
     * @return the role
     */
    public int getRole() {
        return role;
    }
    /**
     * @param role the role to set
     */
    public void setRole(int role) {
        this.role = role;
    }
    /**
     * @return the state
     */
    public String getState() {
        return state;
    }
    /**
     * @param state the state to set
     */
    public void setState(String state) {
        this.state = state;
    }
    /**
     * @return the street1
     */
    public String getStreet1() {
        return street1;
    }
    /**
     * @param street1 the street1 to set
     */
    public void setStreet1(String street1) {
        this.street1 = street1;
    }
    /**
     * @return the street2
     */
    public String getStreet2() {
        return street2;
    }
    /**
     * @param street2 the street2 to set
     */
    public void setStreet2(String street2) {
        this.street2 = street2;
    }
    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }
    /**
     * @param suffix the suffix to set
     */
    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
    /**
     * @return the usfsaid
     */
    public String getUsfsaid() {
        return usfsaid;
    }
    /**
     * @param usfsaid the usfsaid to set
     */
    public void setUsfsaid(String usfsaid) {
        this.usfsaid = usfsaid;
    }
    /**
     * @return the workphone
     */
    public String getWorkphone() {
        return workphone;
    }
    /**
     * @param workphone the workphone to set
     */
    public void setWorkphone(String workphone) {
        this.workphone = workphone;
    }
    /**
     * @return the zip
     */
    public String getZip() {
        return zip;
    }
    /**
     * @param zip the zip to set
     */
    public void setZip(String zip) {
        this.zip = zip;
    }
    /**
     * @return true if this person has a paid membership this season
     */
    public boolean isMember() {
        return isMember;
    }
    /**
     * Set whether the person is a club member this season.
     * @param isMember the boolean value, true if a member
     */
    public void setMember(boolean isMember) {
        this.isMember = isMember;
    }

    /**
     * Get the membership id for this person if they are a member
     * @return the mid
     */
    public long getMembershipId() {
        return mid;
    }

    /**
     * Set the membership id for this person if they are a member
     * @param mid the mid to set
     */
    public void setMembershipId(long mid) {
        this.mid = mid;
    }

    /**
     * @return the membershipStatus
     */
    public String getMembershipStatus() {
        return membershipStatus;
    }

    /**
     * @param membershipStatus the membershipStatus to set
     */
    public void setMembershipStatus(String membershipStatus) {
        this.membershipStatus = membershipStatus;
    }

    /**
     * @return the membershipPaymentId
     */
    public long getMembershipPaymentId() {
        return membershipPaymentId;
    }

    /**
     * @param membershipPaymentId the membershipPaymentId to set
     */
    public void setMembershipPaymentId(long membershipPaymentId) {
        this.membershipPaymentId = membershipPaymentId;
    }

    /**
     * @param maxLevel the maxLevel to set
     */
    public void setMaxLevel(String maxLevel) {
        this.maxLevel = maxLevel;
    }

    /**
     * @return the maxLevel
     */
    public String getMaxLevel() {
        return maxLevel;
    }

    /**
     * Look up properties for this Person instance from the database and refresh those
     * details in this instance.
     */
    public void refreshPersonDetails() {
        
        // Set up the callback object.
        AsyncCallback<Person> callback = new AsyncCallback<Person>() {
            public void onFailure(Throwable caught) {
                // TODO: Do something with errors.
                GWT.log("Failed to refresh the person record.", caught);
            }

            public void onSuccess(Person newPerson) {
                if (newPerson == null) {
                    // Failure on the remote end.
                    GWT.log("Error refreshing the person's attributes.", null);
                    return;
                } else {
                    updateFields(newPerson);
                }
            }
        };

        // Initialize the service proxy and make the call to the registration service.
        SkaterRegistrationServiceAsync regService = GWT.create(SkaterRegistrationService.class);
        regService.getPerson(this.getPid(), callback);
    }
    
    /**
     * Replace the properties of the current person with properties from a new
     * person passed in.  This is used to update fields after a database update.
     * @param p the new person used to update the field data
     */
    private void updateFields(Person p) {
        setLname(p.getLname());
        setFname(p.getFname());
        setMname(p.getMname());
        setSuffix(p.getSuffix());
        setEmail(p.getEmail());
        setEmailValid(p.isEmailValid());
        setBday(p.getBday());
        setRole(p.getRole());
        setHomephone(p.getHomephone());
        setWorkphone(p.getWorkphone());
        setCellphone(p.getCellphone());
        setStreet1(p.getStreet1());
        setStreet2(p.getStreet2());
        setCity(p.getCity());
        setState(p.getState());
        setZip(p.getZip());
        setUsfsaid(p.getUsfsaid());
        setParentLastname(p.getParentLastname());
        setParentFirstname(p.getParentFirstname());
        setParentEmail(p.getParentEmail());
        setUsername(p.getUsername());
        setMember(p.isMember());
        setMembershipId(p.getMembershipId());
        setMembershipStatus(p.getMembershipStatus());
        setMembershipPaymentId(p.getMembershipPaymentId());  
        setMaxLevel(p.getMaxLevel());
    }
}
