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
    private String instructorFullName;
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
     * @return the instructorFullName
     */
    public String getInstructorFullName() {
        return instructorFullName;
    }

    /**
     * @param instructorFullName the instructorFullName to set
     */
    public void setInstructorFullName(String instructorFullName) {
        this.instructorFullName = instructorFullName;
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
}
