package org.jsc.client;

/**
 * A model of a JSC skating class, with fields that instantiate properties of
 * each skating class.
 * 
 * @author Matthew Jones
 *
 */
public class JSCClass {
    private long classId;
    private String className;
    private String instructor;
    private long instructorId;
    
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
     * @return the instructor
     */
    public String getInstructor() {
        return instructor;
    }
    /**
     * @param instructor the instructor to set
     */
    public void setInstructor(String instructor) {
        this.instructor = instructor;
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
    
}
