package org.jsc.client;

import java.util.HashMap;

/**
 * A class that encapsulates all of the levels that can be passed via any class.
 * This list is rad from the database, and serialized back into the database.
 * 
 * @author Matthew Jones
 *
 */
public class Levels {
    private static Levels self = null;
    private HashMap<String, String> levelsList = null;
    
    /**
     * Construct the list of levels by reading them from the database. Levels
     * is a singleton, as we only need and want one instance of this model to be
     * read from the database. Use getInstance() to get the single instance of 
     * this class.
     *
     */
    private Levels() {
        levelsList = new HashMap<String, String>();
        loadLevels();
    }
    
    /**
     * Return the single instance of this Levels singleton, instantiating it 
     * if needed.
     * @return an instance of the Levels class
     */
    public static Levels getInstance() {
        if (self == null) {
            self = new Levels();
        }
        return self;
    }
    
    /**
     * Load the levels from the database.
     *
     */
    private void loadLevels() {
        addLevel("SS1", "Snowplow Sam 1");
        addLevel("BS1", "Basic Skills 1");
        addLevel("BS2", "Basic Skills 1");
    }
    
    /**
     * Add a single level to the local list.
     * @param code the code to be used for this level
     * @param name the name of this level
     */
    public void addLevel(String code, String name) {
        assert(code != null);
        assert(name != null);
        levelsList.put(code,name);
    }
    
    /**
     * Access the name of a level based on the code for that level.
     * @param code for which the level name is returned
     * @return name of the level for the given code
     */
    public String getLevel(String code) {
        return levelsList.get(code);
    }
    
}
