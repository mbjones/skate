package org.jsc.client;

/**
 * A model of a JSC Session and Class combination, with properties from both
 * the class and session merged in a single object.
 * .
 * @author Matthew Jones
 *
 */
public class JSCSessionClass {
    private long sid;
    private long sessionNum;
    private String season;
    private String startDate;
    private String endDate;
    private long classId;
    private String className;
    private long instructorId;
    private double cost;
    private String instructorFullName;
    
    /**
     * Construct a new session class with only the required fields.
     * 
     * @param sid
     * @param sessionName
     * @param season
     * @param classId
     * @param className
     */
    public JSCSessionClass(long sid, long sessionNum, String season, 
            long classId, String className) {
        this.sid = sid;
        this.sessionNum = sessionNum;
        this.season = season;
        this.classId = classId;
        this.className = className;
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
     * @return the className
     */
    public String getClassName() {
        return className;
    }
    /**
     * @param className the className to set
     */
    public void setClassName(String className) {
        this.className = className;
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
     * @return the sessionName
     */
    public long getSessionNum() {
        return sessionNum;
    }
    /**
     * @param sessionName the sessionName to set
     */
    public void setSessionNum(long sessionNum) {
        this.sessionNum = sessionNum;
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
    
}
