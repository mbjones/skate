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
    private double payment_amount;
    private Date payment_date;
    private String paypal_tx_id;
    private double paypal_gross;
    private double paypal_fee;
    private String paypal_status;
    
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
     * @return the payment_date
     */
    public Date getPayment_date() {
        return payment_date;
    }
    /**
     * @param paymentDate the payment_date to set
     */
    public void setPayment_date(Date paymentDate) {
        payment_date = paymentDate;
    }
    /**
     * @return the paypal_tx_id
     */
    public String getPaypal_tx_id() {
        return paypal_tx_id;
    }
    /**
     * @param paypalTxId the paypal_tx_id to set
     */
    public void setPaypal_tx_id(String paypalTxId) {
        paypal_tx_id = paypalTxId;
    }
    /**
     * @return the paypal_gross
     */
    public double getPaypal_gross() {
        return paypal_gross;
    }
    /**
     * @param paypalGross the paypal_gross to set
     */
    public void setPaypal_gross(double paypalGross) {
        paypal_gross = paypalGross;
    }
    /**
     * @return the paypal_fee
     */
    public double getPaypal_fee() {
        return paypal_fee;
    }
    /**
     * @param paypalFee the paypal_fee to set
     */
    public void setPaypal_fee(double paypalFee) {
        paypal_fee = paypalFee;
    }
    /**
     * @return the paypal_status
     */
    public String getPaypal_status() {
        return paypal_status;
    }
    /**
     * @param paypalStatus the paypal_status to set
     */
    public void setPaypal_status(String paypalStatus) {
        paypal_status = paypalStatus;
    }
}
