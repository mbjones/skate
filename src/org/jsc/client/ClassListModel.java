package org.jsc.client;

//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.PreparedStatement;
//import java.sql.SQLException;
import java.util.Iterator;
import java.util.Vector;

public class ClassListModel {
    private Vector<JSCSessionClass> classList;
    
    /**
     * Construct the list of classes before the list is populated.
     */
    public ClassListModel() {
        classList = new Vector<JSCSessionClass>();
    }
    
    /**
     * Read the list of classes from the sessionclass view in the database.
     *
     */
    public void refreshClassList() {
        String dbDriver = "pgsql";
        String connString = "somehost";
        String user = "jones";
        String password = "";
        String sql = "SELECT * from classes;";
        /*
        try {
            Connection conn = openConnection(dbDriver, connString, user, password);
            PreparedStatement stmt = conn.prepareStatement(sql);
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        */
        
        for (long classid = 1; classid <= 8; classid++) {
            long sid = 1;
            long session = 3;
            JSCSessionClass c1 = new JSCSessionClass(sid, session, "2008-2009", 
                    classid, "BS-"+classid);
            c1.setInstructorFullName("L. Anderson");
            addClass(c1);
        }
    }
    
    /**
     * Add a new JSC class to the list, and synchronize it with the database
     * @param jscClass
     */
    public void addClass(JSCSessionClass jscClass) {
        classList.add(jscClass);
    }
    
    /**
     * Determine the number of classes in this model.
     * @return integer number of session classes
     */
    public int size() {
        return classList.size();
    }
    
    /**
     * Get an iterator for this list of classes to step over all elements.
     * @return iterator across all elements
     */
    public Iterator iterator() {
        return classList.iterator();
    }
    
    /** 
     * Method to establish a JDBC database connection 
     *
     * @param dbDriver the string representing the database driver
     * @param connection the string representing the database connection parameters
     * @param user name of the user to use for database connection
     * @param password password for the user to use for database connection
     */
    /*
    private static Connection openConnection(String dbDriver, String connection,
                  String user, String password)
                  throws SQLException
   {
       // Load the JDBC driver
       try {
         Class.forName(dbDriver);
       } catch (ClassNotFoundException e) {
         System.err.println("Error opening database connection: "+e.getMessage());
         return null;
       }
       
       // Connect to the database
       Connection connLocal = null;
       connLocal = DriverManager.getConnection( connection, user, password);
       return connLocal;
    }
    */

}
