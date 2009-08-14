package org.jsc.client;

import junit.framework.TestCase;

public class LevelsTest extends TestCase {

    private String code;
    private String name;
    
    protected void setUp() throws Exception {
        super.setUp();
        code = "test";
        name = "Test Name";
    }

    public void testGetInstance() {
        Levels levels = Levels.getInstance();
        assert(levels != null);
    }

    public void testAddLevel() {
        Levels levels = Levels.getInstance();
        levels.addLevel(code, name);
        assert(levels.getLevel(code).equals(name));
    }

    public void testGetLevel() {
        Levels levels = Levels.getInstance();
        assert(levels.getLevel(code).equals(name));
    }

}
