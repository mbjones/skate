package org.jsc.client;

import java.io.Serializable;
import java.util.Date;

/**
 * A data structure representing a single entry in a roster.  One roster entry
 * is created for each person that is registered for a skating class.  
 * The RosterEntry class is used to transport information to and from the 
 * registration service.
 * @author Matt Jones
 */
public class RosterEntry implements Serializable {
    private long rosterid;
    private long classid;
    private long pid;
    private String levelpassed;
    private long paymentid;
    private double payment_amount;
    private String paypal_status;
    private Date date_updated;
    private String surname;
    private String givenname;
    private String section;
    
    /**
     * Default constructor takes no parameters.
     */
    public RosterEntry() {
        
    }
    
    /**
     * @return the rosterid
     */
    public long getRosterid() {
        return rosterid;
    }
    /**
     * @param rosterid the rosterid to set
     */
    public void setRosterid(long rosterid) {
        this.rosterid = rosterid;
    }
    /**
     * @return the classid
     */
    public long getClassid() {
        return classid;
    }
    /**
     * @param classid the classid to set
     */
    public void setClassid(long classid) {
        this.classid = classid;
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
     * @return the paymentid
     */
    public long getPaymentid() {
        return paymentid;
    }

    /**
     * @param paymentid the paymentid to set
     */
    public void setPaymentid(long paymentid) {
        this.paymentid = paymentid;
    }

    /**
     * @return the levelpassed
     */
    public String getLevelpassed() {
        return levelpassed;
    }
    /**
     * @param levelpassed the levelpassed to set
     */
    public void setLevelpassed(String levelpassed) {
        this.levelpassed = levelpassed;
    }
    /**
     * @return the payment_amount
     */
    public double getPayment_amount() {
        return payment_amount;
    }
    /**
     * @param paymentAmount the payment_amount to set
     */
    public void setPayment_amount(double paymentAmount) {
        payment_amount = paymentAmount;
    }

    /**
     * @param paypal_status the paypal_status to set
     */
    public void setPaypal_status(String paypal_status) {
        this.paypal_status = paypal_status;
    }

    /**
     * @return the paypal_status
     */
    public String getPaypal_status() {
        return paypal_status;
    }

    /**
     * @return the date_updated
     */
    public Date getDate_updated() {
        return date_updated;
    }

    /**
     * @param dateUpdated the date_updated to set
     */
    public void setDate_updated(Date dateUpdated) {
        date_updated = dateUpdated;
    }

    /**
     * @return the surname
     */
    public String getSurname() {
        return surname;
    }

    /**
     * @param surname the surname to set
     */
    public void setSurname(String surname) {
        this.surname = surname;
    }

    /**
     * @return the givenname
     */
    public String getGivenname() {
        return givenname;
    }

    /**
     * @param givenName the givenName to set
     */
    public void setGivenname(String givenname) {
        this.givenname = givenname;
    }

    /**
     * @param section the section to set
     */
    public void setSection(String section) {
        this.section = section;
    }

    /**
     * @return the section
     */
    public String getSection() {
        return section;
    }
    
}
