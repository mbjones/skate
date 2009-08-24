package org.jsc.client;

import java.io.Serializable;

/**
 * A model of a JSC Session and Class combination, with properties from both
 * the class and session merged in a single object.
 * .
 * @author Matt Jones
 */
public class SessionSkatingClass implements Serializable {
    private long sid;
    private long sessionNum;
    private String season;
    private String startDate;
    private String endDate;
    private long classId;
    private String classType;
    private String day;
    private String timeslot;
    private long instructorId;
    private String instructorSurName;
    private String instructorGivenName;
    private double cost;
    private String otherinstructors;
    
    /**
     * Construct a new session, using accessors to set all fields after construction.
     */
    public SessionSkatingClass() {
        
    }
    
    /**
     * Construct a new session class with only the required fields.
     * 
     * @param sid
     * @param sessionNum
     * @param season
     * @param classId
     * @param className
     */
    public SessionSkatingClass(long sid, long sessionNum, String season, 
            long classId, String classType) {
        this.sid = sid;
        this.sessionNum = sessionNum;
        this.season = season;
        this.classId = classId;
        this.classType = classType;
    }

    /**
     * @return the sid
     */
    public long getSid() {
        return sid;
    }

    /**
     * @param sid the sid to set
     */
    public void setSid(long sid) {
        this.sid = sid;
    }

    /**
     * @return the sessionNum
     */
    public long getSessionNum() {
        return sessionNum;
    }

    /**
     * @param sessionNum the sessionNum to set
     */
    public void setSessionNum(long sessionNum) {
        this.sessionNum = sessionNum;
    }

    /**
     * @return the season
     */
    public String getSeason() {
        return season;
    }

    /**
     * @param season the season to set
     */
    public void setSeason(String season) {
        this.season = season;
    }

    /**
     * @return the startDate
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * @param startDate the startDate to set
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * @return the endDate
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * @param endDate the endDate to set
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * @return the classId
     */
    public long getClassId() {
        return classId;
    }

    /**
     * @param classId the classId to set
     */
    public void setClassId(long classId) {
        this.classId = classId;
    }

    /**
     * @return the classType
     */
    public String getClassType() {
        return classType;
    }

    /**
     * @param classType the classType to set
     */
    public void setClassType(String classType) {
        this.classType = classType;
    }

    /**
     * @return the day
     */
    public String getDay() {
        return day;
    }

    /**
     * @param day the day to set
     */
    public void setDay(String day) {
        this.day = day;
    }

    /**
     * @return the timeslot
     */
    public String getTimeslot() {
        return timeslot;
    }

    /**
     * @param timeslot the timeslot to set
     */
    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    /**
     * @return the instructorId
     */
    public long getInstructorId() {
        return instructorId;
    }

    /**
     * @param instructorId the instructorId to set
     */
    public void setInstructorId(long instructorId) {
        this.instructorId = instructorId;
    }

    /**
     * @return the instructorSurName
     */
    public String getInstructorSurName() {
        return instructorSurName;
    }

    /**
     * @param instructorSurName the instructorSurName to set
     */
    public void setInstructorSurName(String instructorSurName) {
        this.instructorSurName = instructorSurName;
    }

    /**
     * @return the instructorGivenName
     */
    public String getInstructorGivenName() {
        return instructorGivenName;
    }

    /**
     * @param instructorGivenName the instructorGivenName to set
     */
    public void setInstructorGivenName(String instructorGivenName) {
        this.instructorGivenName = instructorGivenName;
    }

    /**
     * @return the cost
     */
    public double getCost() {
        return cost;
    }

    /**
     * @param cost the cost to set
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * @return the otherinstructors
     */
    public String getOtherinstructors() {
        return otherinstructors;
    }

    /**
     * @param otherinstructors the otherinstructors to set
     */
    public void setOtherinstructors(String otherinstructors) {
        this.otherinstructors = otherinstructors;
    }

    /**
     * Create a string that can be used to represent this class, including session
     * information and schedule information.
     * @return the String label representing the class
     */
    public String formatClassLabel() {
        StringBuffer classLabel = new StringBuffer(getSeason());
        classLabel.append(" Session ").append(getSessionNum());
        classLabel.append(" ").append(getClassType());
        classLabel.append(" (").append(getDay());
        classLabel.append(" ").append(getTimeslot()).append(")");
        return classLabel.toString();
    }
}
