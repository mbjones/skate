package org.jsc.client;

import junit.framework.TestCase;

public class JSCSessionClassTest extends TestCase {
    
    SessionSkatingClass c;
    long sid;
    long session;
    long classid;
    
    protected void setUp() throws Exception {
        super.setUp();
        sid = 1;
        session = 3;
        classid = 1;
        c = new SessionSkatingClass(sid, session, "2008-2009", 
                classid, "BS-"+classid);
    }

    public void testJSCSessionClass() {
        SessionSkatingClass c1 = new SessionSkatingClass(sid, session, "2008-2009", 
                classid, "BS-"+classid);
        assertTrue("JSCSessionClass constructor failed.", c1 != null);
    }

    public void testGetClassId() {
        assertTrue(c.getClassId() == classid);
    }

    public void testSetClassId() {
        long newId = 2;
        c.setClassId(newId);
        assertTrue(c.getClassId() == newId);
    }
    /*
    public void testGetClassName() {
        fail("Not yet implemented");
    }

    public void testSetClassName() {
        fail("Not yet implemented");
    }

    public void testGetEndDate() {
        fail("Not yet implemented");
    }

    public void testSetEndDate() {
        fail("Not yet implemented");
    }

    public void testGetInstructorFullName() {
        fail("Not yet implemented");
    }

    public void testSetInstructorFullName() {
        fail("Not yet implemented");
    }

    public void testGetInstructorId() {
        fail("Not yet implemented");
    }

    public void testSetInstructorId() {
        fail("Not yet implemented");
    }

    public void testGetSeason() {
        fail("Not yet implemented");
    }

    public void testSetSeason() {
        fail("Not yet implemented");
    }

    public void testGetSessionNum() {
        fail("Not yet implemented");
    }

    public void testSetSessionNum() {
        fail("Not yet implemented");
    }

    public void testGetSid() {
        fail("Not yet implemented");
    }

    public void testSetSid() {
        fail("Not yet implemented");
    }

    public void testGetStartDate() {
        fail("Not yet implemented");
    }

    public void testSetStartDate() {
        fail("Not yet implemented");
    }
    */
}
