package org.jsc.client;

import java.util.Iterator;

import junit.framework.TestCase;

public class ClassListModelTest extends TestCase {

    ClassListModel cm;
    JSCSessionClass c;
    
    protected void setUp() throws Exception {
        super.setUp();
        cm = new ClassListModel();
        long sid = 1;
        long session = 3;
        long classid = 1;
        c = new JSCSessionClass(sid, session, "2008-2009", 
                classid, "BS-"+classid);
    }

    public void testClassListModel() {
        ClassListModel lcm = new ClassListModel();
        assertTrue(lcm != null);
        assertTrue(lcm.size() == 0);
    }

    public void testRefreshClassList() {
        cm.refreshClassList();
        assertTrue(cm.size() == 8);
    }

    public void testAddClass() {
        cm.addClass(c);
        assertTrue(cm.size() == 1);
    }

    public void testIterator() {
        cm.refreshClassList();
        Iterator it = cm.iterator();
        assertTrue(it != null);
        while (it.hasNext()) {
            Object o = it.next();
            assertTrue(o instanceof JSCSessionClass);
        }        
    }
}
