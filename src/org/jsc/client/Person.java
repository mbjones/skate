package org.jsc.client;

import java.io.Serializable;

/**
 * An encapsulation of a Person, with fields giving their basic demographic and
 * contact information.
 * @author Matthew Jones
 *
 */
public class Person implements Serializable {
    private long pid;
    private String username;
    private String fname;
    private String mname;
    private String lname;
    private String suffix;
    private String email;
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
    public String getParentFirstName() {
        return parentFirstName;
    }
    /**
     * @param parentFirstName the parentFirstName to set
     */
    public void setParentFirstName(String parentFirstName) {
        this.parentFirstName = parentFirstName;
    }
    /**
     * @return the parentSurname
     */
    public String getParentSurname() {
        return parentSurname;
    }
    /**
     * @param parentSurname the parentSurname to set
     */
    public void setParentSurname(String parentSurname) {
        this.parentSurname = parentSurname;
    }
    /**
     * @return the password1
     */
    public String getPassword1() {
        return password1;
    }
    /**
     * @param password1 the password1 to set
     */
    public void setPassword1(String password1) {
        this.password1 = password1;
    }
    /**
     * @return the password2
     */
    public String getPassword2() {
        return password2;
    }
    /**
     * @param password2 the password2 to set
     */
    public void setPassword2(String password2) {
        this.password2 = password2;
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
    public String getRole() {
        return role;
    }
    /**
     * @param role the role to set
     */
    public void setRole(String role) {
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
}
