package org.jsc.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.TreeMap;

import org.jsc.client.Person;
import org.jsc.client.SkaterRegistrationService;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * An implementation of a registration service that handles the creation of 
 * new accounts as well as registration of people into classes.
 * @author Matthew Jones
 */
public class SkaterRegistrationServiceImpl extends RemoteServiceServlet
        implements SkaterRegistrationService {

    private static final String JDBC_URL = "jdbc:postgresql://localhost/jscdb";
    private static final String JDBC_USER = "jones";
    private static final String JDBC_PASS = "";
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
     * and return them as a Hash where the key is the classId and the value is
     * a descriptive label for the class.
     * @param person the person used for authentication credentials
     * @return the TreeMap of the classes, keyed on classId
     */
    public TreeMap<String,String> getClassList(Person person) {
        TreeMap<String,String> classList = new TreeMap<String,String>();
        
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
                String season = rs.getString(1);
                String sessionName = rs.getString(2);
                String classType = rs.getString(3);
                String day = rs.getString(4);
                String timeslot = rs.getString(5);
                String classId = rs.getString(6);
                StringBuffer classLabel = new StringBuffer(season);
                classLabel.append(" Session ").append(sessionName);
                classLabel.append(" ").append(classType);
                classLabel.append(" (").append(day);
                classLabel.append(" ").append(timeslot).append(")");
                classList.put(classLabel.toString(), classId);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        return classList;
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
        sql.append("pid LIKE '").append(pid).append("'");
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
     * Create a new person entry in the backing relational database, or update
     * fields on an existing entry.  If the 'pid' field of the person is empty
     * or 0, a new entry is created.  Otherwise, the entry with the pid is
     * updated.
     * @param person the Person to be created or updated in the database
     * @return the Person that was created or updated created
     */
    /*
    private Person insertOrUpdatePerson(Person person) {
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
            if (person.getEmail() != null && person.getPassword() != null) {
                int validpid = checkPassword(person.getEmail(), person.getPassword());
                if (validpid == 0 || validpid != person.getPid()) {
                    // Invalid credentials, so don't allow the change
                    return null;
                }
            } else {
                // No email and/or password provided, so no valid credentials
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
    */
    
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
