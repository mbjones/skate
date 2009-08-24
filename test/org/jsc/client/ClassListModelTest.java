package org.jsc.client;

import java.util.Iterator;

import com.google.gwt.event.shared.HandlerManager;

import junit.framework.TestCase;

public class ClassListModelTest extends TestCase {

    ClassListModel cm;
    SessionSkatingClass c;
    
    protected void setUp() throws Exception {
        super.setUp();
        LoginSession loginSession = null;
        HandlerManager eventBus = null;
        
        cm = new ClassListModel(eventBus, loginSession);
        long sid = 1;
        long session = 3;
        long classid = 1;
        c = new SessionSkatingClass(sid, session, "2008-2009", 
                classid, "BS-"+classid);
    }

    public void testClassListModel() {
        LoginSession loginSession = null;
        HandlerManager eventBus = null;
        ClassListModel lcm = new ClassListModel(eventBus, loginSession);
        assertTrue(lcm != null);
        assertTrue(lcm.size() == 0);
    }

    public void testRefreshClassList() {
        cm.refreshClassList();
        assertTrue(cm.size() == 8);
    }

    public void testAddClass() {
        cm.addSkatingClass(c);
        assertTrue(cm.size() == 1);
    }

    public void testIterator() {
        cm.refreshClassList();
        Iterator it = cm.iterator();
        assertTrue(it != null);
        while (it.hasNext()) {
            Object o = it.next();
            assertTrue(o instanceof SessionSkatingClass);
        }        
    }
}
