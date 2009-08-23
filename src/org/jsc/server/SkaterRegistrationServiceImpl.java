package org.jsc.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.TreeMap;

import org.jsc.client.Person;
import org.jsc.client.RosterEntry;
import org.jsc.client.SessionSkatingClass;
import org.jsc.client.SkaterRegistrationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * An implementation of a registration service that handles the creation of 
 * new accounts as well as registration of people into classes.
 * @author Matt Jones
 */
public class SkaterRegistrationServiceImpl extends RemoteServiceServlet
        implements SkaterRegistrationService {

    private static final String JDBC_URL = "jdbc:postgresql://localhost/jscdb";
    private static final String JDBC_USER = "jscdb";
    private static final String JDBC_PASS = "1skate2";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    
    /**
     * Create a new person entry in the backing relational database, or update
     * fields on an existing entry.  If the 'pid' field of the person is empty
     * or 0, a new entry is created.  Otherwise, the entry with the pid is
     * updated.
     * @param person the Person to be created or updated in the database
     * @return the Person that was created or updated, or null on error
     */
    public Person createAccount(Person person) {
        long pid = 0;

        StringBuffer sql = new StringBuffer();
        if (person.getPid() == 0) {
            // Creating a new account, so no authentication needed
            // Create the SQL INSERT statement
            sql.append("insert into people");
            sql.append(" (surname, givenname, middlename, email, home_phone, birthdate, password) ");
            sql.append("values ('");
            sql.append(person.getLname()).append("','");
            sql.append(person.getFname()).append("','");
            sql.append(person.getMname()).append("','");
            sql.append(person.getEmail()).append("','");
            sql.append(person.getHomephone()).append("','");
            sql.append(person.getBday()).append("','");
            sql.append(person.getNewPassword()).append("'");
            sql.append(")");
        } else {
            // Verify that the user is valid before allowing an update
            boolean isAuthentic = checkCredentials(person);
            if (!isAuthentic) {
                return null;
            }
            sql.append("update people set ");
            sql.append("surname='").append(person.getLname()).append("',");
            sql.append("givenname='").append(person.getFname()).append("',");
            sql.append("middlename='").append(person.getMname()).append("',");
            if (person.getNewEmail() != null && person.getNewEmail().length() > 0 && 
                    !person.getEmail().equals(person.getNewEmail())) {
                sql.append("email='").append(person.getNewEmail()).append("',");
            } else {
                sql.append("email='").append(person.getEmail()).append("',");
            }
            sql.append("home_phone='").append(person.getHomephone()).append("',");
            sql.append("birthdate='").append(person.getBday()).append("'");
            if (person.getNewPassword() != null && person.getNewPassword().length() > 0) {
                sql.append(",password='").append(person.getNewPassword()).append("'");
            }
            sql.append(" where pid=").append(person.getPid());
        }

        System.out.println(sql.toString());

        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();
            if (person.getPid() == 0) {
                // This is an INSERT, so look up the new PID for the new record
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(
                        "SELECT max(pid) from people where email LIKE '" + 
                        person.getEmail() + "'");
                if (rs.next()) {
                    pid = rs.getInt(1);
                } else {
                    pid = 0;
                }
                stmt.close();
            } else {
                // This is an UPDATE, so use the passed in PID
                pid = person.getPid();
            }
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        Person newPerson = lookupPerson(pid);
        return newPerson;
    }
    
    /**
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the Person that was authenticated if valid, otherwise null
     */
    public Person authenticate(String username, String password) {
        Person person = null;
        if (username != null && password != null) {
            int pid = checkPassword(username, password);
            if (pid > 0) {
                person = lookupPerson(pid);
            }
        }
        return person;
    }
    
    /**
     * Look up the person in the database based on their identifier. If found,
     * return the Person object.
     * @param pid the identifier of the person to look up
     * @return the Person object associated with this pid
     */
    public Person getPerson(long pid) {
        // TODO: be sure to check for proper credentials here
        
        Person person = null;
        person = lookupPerson(pid);
        return person;
    }
    
    /**
     * Look up the list of current classes that are available in the database,
     * and return them as an ArrayList of SessionSkatingClass objects.
     * @param person the person used for authentication credentials
     * @return the ArrayList of SessionSkatingClass instances
     */
    public ArrayList<SessionSkatingClass> getSessionClassList(Person person) {
        ArrayList<SessionSkatingClass> classList = new ArrayList<SessionSkatingClass>();
        
        // Check authentication credentials
        boolean isAuthentic = checkCredentials(person);
        if (!isAuthentic) {
            return null;
        }
        
        // Query the database to get the list of classes
        StringBuffer sql = new StringBuffer();
        sql.append("select season, sessionname, classtype, day, timeslot, classid from sessionclasses");
        System.out.println(sql.toString());
        
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                SessionSkatingClass sc = new SessionSkatingClass();
                sc.setSeason(rs.getString(1));
                sc.setSessionNum(rs.getInt(2));
                sc.setClassType(rs.getString(3));
                sc.setDay(rs.getString(4));
                sc.setTimeslot(rs.getString(5));
                sc.setClassId(rs.getLong(6));
                classList.add(sc);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        // START HERE: com.google.gwt.user.client.rpc.SerializationException: java.lang.reflect.InvocationTargetException
        return classList;
    }
    
    /**
     * Register a person for a class by creating a new entry in the roster
     * table in the backing relational database.  This method takes on input a
     * skeleton RosterEntry that contains the classid and pid to be registered,
     * and then creates and returns a new RosterEntry from the database after 
     * the rosterid has been created.
     * @param person to be used to authenticate the connection
     * @param entry containing the details of the class and person to be registered
     * @return the completed RosterEntry from the database
     */
    public RosterEntry register(Person person, RosterEntry entry) {
        RosterEntry newEntry = null;
        
        if (person != null && entry != null) {
            // Verify that the user is valid before allowing an insert
            boolean isAuthentic = checkCredentials(person);
            if (!isAuthentic) {
                return null;
            }
        } else {
            return null;
        }
        
        // Create the SQL INSERT statement
        StringBuffer sql = new StringBuffer();
        sql.append("insert into roster (classid, pid, payment_amount) values ('");
        sql.append(entry.getClassid()).append("','");
        sql.append(entry.getPid()).append("',");
        sql.append(entry.getPayment_amount());
        sql.append(")");
        System.out.println(sql.toString());

        // Execute the INSERT to create the new roster table entry
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();
            
            stmt = con.createStatement();
            StringBuffer rsql = new StringBuffer();
            rsql.append("SELECT rosterid,classid,pid,levelpassed,payment_amount,");
            rsql.append("payment_date,paypal_tx_id,paypal_gross,paypal_fee,");
            rsql.append("paypal_status from roster where ");
            rsql.append("classid = '").append(entry.getClassid()).append("'");
            rsql.append(" AND ");
            rsql.append("pid = '").append(entry.getPid()).append("'");
            System.out.println(rsql.toString());
            
            ResultSet rs = stmt.executeQuery(rsql.toString());
            newEntry = createRosterEntry(rs);
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return newEntry;
    }

    /**
     * Helper method to create a RosterEntry object from the results fo a JDBC
     * query that contains the appropriate fields.
     * @param rs the result set containing the roster fields
     * @return an instance of RosterEntry
     * @throws SQLException if there is an error accessing the result set
     */
    private RosterEntry createRosterEntry(ResultSet rs) throws SQLException {
        RosterEntry entry = null;
        if (rs.next()) {
            entry = new RosterEntry();
            entry.setRosterid(rs.getLong(1));
            entry.setClassid(rs.getLong(2));
            entry.setPid(rs.getLong(3));
            entry.setLevelpassed(rs.getString(4));
            entry.setPayment_amount(rs.getDouble(5));
            entry.setPayment_date(rs.getDate(6));
            entry.setPaypal_tx_id(rs.getString(7));
            entry.setPaypal_gross(rs.getDouble(8));
            entry.setPaypal_fee(rs.getDouble(9));
            entry.setPaypal_status(rs.getString(10));
        }
        return entry;
    }
    
    /**
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the id of the person if valid, otherwise 0
     */
    private Person lookupPerson(long pid) {

        StringBuffer sql = new StringBuffer();
        sql.append("select pid, surname, givenname, middlename, email, home_phone, birthdate, password from people where ");
        sql.append("pid = '").append(pid).append("'");
        System.out.println(sql.toString());
        
        Person person = null;
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                person = new Person();
                person.setPid(rs.getInt(1));
                person.setLname(rs.getString(2));
                person.setFname(rs.getString(3));
                person.setMname(rs.getString(4));
                person.setEmail(rs.getString(5));
                person.setHomephone(rs.getString(6));
                person.setBday(rs.getString(7));
                person.setPassword(rs.getString(8));
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return person;
    }
    
    /**
     * Check the credentials for the person, determining if their email and
     * password match a similar row in the database.
     * @param person the person whose credentials are to be authenticated
     * @return true if valid credentials, false if otherwise
     */
    private boolean checkCredentials(Person person) {
        boolean isAuthenticated = false;
        if (person.getEmail() != null && person.getPassword() != null) {
            int validpid = checkPassword(person.getEmail(), person.getPassword());
            if (validpid > 0 || validpid == person.getPid()) {
                // The password matches, returns a person's id, which matches
                // the person originally requesting auth
                isAuthenticated = true;
            }
        }
        return isAuthenticated;
    }
    
    /**
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the id of the person if valid, otherwise 0
     */
    private int checkPassword(String username, String password) {
        int pid = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("select * from people where ");
        sql.append("email LIKE '").append(username).append("'");
        sql.append(" AND ");
        sql.append("password LIKE '").append(password).append("'");
        System.out.println(sql.toString());

        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                pid = rs.getInt(1);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return pid;
    }
    
    /**
     * Open a JDBC database connection.
     */
    private static Connection getConnection() {
        Connection con = null;
        
        try {
            Class.forName(JDBC_DRIVER);
        } catch(java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return con;
    }
}
