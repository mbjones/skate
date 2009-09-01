package org.jsc.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jsc.client.LoginSession;
import org.jsc.client.Person;
import org.jsc.client.RegistrationResults;
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
    private static final String ROSTER_QUERY = "SELECT rosterid, classid, pid, levelPassed, paymentid, payment_amount, paypal_status, date_updated, surname, givenname FROM rosterpeople";
    private static final int MAX_SESSION_INTERVAL = 60 * 30;
    
    /**
     * Create a new person entry in the backing relational database, or update
     * fields on an existing entry.  If the 'pid' field of the person is empty
     * or 0, a new entry is created.  Otherwise, the entry with the pid is
     * updated.
     * @param person the Person to be created or updated in the database
     * @return the Person that was created or updated, or null on error
     */
    public Person createAccount(LoginSession loginSession, Person person) {
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
            // Verify that the session is valid before allowing an update
            boolean isAuthentic = isSessionValid(loginSession);
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
     * @return the LoginSession containing the person that was authenticated if valid, otherwise null
     */
    public LoginSession authenticate(String username, String password) {
        Person person = null;
        LoginSession loginSession = null;
        
        if (username != null && password != null) {
            HttpServletRequest request = this.getThreadLocalRequest();
            HttpSession session = request.getSession();
            session.setMaxInactiveInterval(MAX_SESSION_INTERVAL);
            System.out.println("Servlet got session with id: " + session.getId());
            String sessionId = session.getId();
            
            int pid = checkPassword(username, password);
            if (pid > 0) {
                person = lookupPerson(pid);
                loginSession = new LoginSession();
                loginSession.setPerson(person);
                loginSession.setSessionId(sessionId);
                loginSession.setAuthenticated(true);
            }
        }
        return loginSession;
    }
    
    /**
     * Invalidate the session, forcing a new authentication call from the client.
     */
    public boolean logout() {
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        session.invalidate();
        return true;
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
    public ArrayList<SessionSkatingClass> getSessionClassList(LoginSession loginSession, Person person) {
        ArrayList<SessionSkatingClass> classList = new ArrayList<SessionSkatingClass>();
        
        // Check authentication credentials
        boolean isAuthentic = isSessionValid(loginSession);
        if (!isAuthentic) {
            return null;
        }
        
        // Query the database to get the list of classes
        StringBuffer sql = new StringBuffer();
        sql.append("select sid, sessionname, season, startdate, enddate, ");
        sql.append("classid, classtype, day, timeslot, instructorid, ");
        sql.append("otherinstructors, surname, givenname from sessionclasses");
        System.out.println(sql.toString());
        
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            while (rs.next()) {
                SessionSkatingClass sc = new SessionSkatingClass();
                sc.setSid(rs.getLong(1));
                sc.setSessionNum(rs.getInt(2));
                sc.setSeason(rs.getString(3));
                sc.setStartDate(rs.getString(4));
                sc.setEndDate(rs.getString(5));
                sc.setClassId(rs.getLong(6));
                sc.setClassType(rs.getString(7));
                sc.setDay(rs.getString(8));
                sc.setTimeslot(rs.getString(9));
                sc.setInstructorId(rs.getLong(10));
                sc.setOtherinstructors(rs.getString(11));
                sc.setInstructorSurName(rs.getString(12));
                sc.setInstructorGivenName(rs.getString(13));
                classList.add(sc);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }

        return classList;
    }
    
    /**
     * Register a person for one or more classes by creating a new entry in the roster
     * table in the backing relational database for each RosterEntry that is passed in
     * the newEntryList array.  This method takes on input an array of
     * skeleton RosterEntry instances that each contains the classid and pid to be registered,
     * and then creates and returns a new RosterEntry from the database after 
     * the rosterid has been created for each one.
     * @param person to be used to authenticate the connection
     * @param newEntryList list of RosterEntry objects containing the details of the class and person to be registered
     * @param createMembership boolean, set to true if the membership for the user should be created for this season
     * @return an array of the completed RosterEntry instances from the database
     */
    public RegistrationResults register(LoginSession loginSession, Person person, ArrayList<RosterEntry> newEntryList, boolean createMembership) {
        
        RegistrationResults results = new RegistrationResults();
        
        ArrayList<RosterEntry> entriesCreated = new ArrayList<RosterEntry>();
        ArrayList<Long> entriesFailed = new ArrayList<Long>();
                
        // Check credentials
        if (person != null && newEntryList != null) {
            // Verify that the user is valid before allowing an insert
            boolean isAuthentic = isSessionValid(loginSession);
            if (!isAuthentic) {
                return null;
            }
        } else {
            return null;
        }
        
        // Create an entry in the payments table to represent the transaction,
        // setting the paypal_status field to incomplete. This field is later
        // updated using the paypal IPN service when the transaction completes        
        long paymentId = 0;
        try {
            Connection con = getConnection();
            
            // Look up the paymentId to be used for this insert
            StringBuffer idsql = new StringBuffer();
            idsql.append("SELECT NEXTVAL(\'\"payment_id_seq\"\')");
            System.out.println(idsql.toString());
            Statement stmt = con.createStatement();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(idsql.toString());
            if (rs.next()) {
                // This is the next id in the payment id sequence
                paymentId = rs.getLong(1);
            }
            stmt.close();
            
            // Execute the INSERT to create the new payment table entry
            StringBuffer psql = new StringBuffer();
            psql.append("insert into payment (paymentId, paypal_status) VALUES (" 
                    + paymentId + ", 'Pending')");
            System.out.println(psql.toString());
   
            stmt = con.createStatement();
            stmt.executeUpdate(psql.toString());
            stmt.close();
            con.close();
            results.setPaymentId(paymentId);
            
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
            results.setMembershipCreated(false);
            results.setMembershipErrorMessage("SQLException: " + ex.getMessage());
        }
        
        // Check if a membership entry should be created, and do so
        if (createMembership) {
            results.setMembershipAttempted(true);
            // Create the SQL INSERT statement
            StringBuffer sql = new StringBuffer();
            sql.append("insert into membership (pid, paymentid, season) VALUES ('");
            sql.append(person.getPid()).append("','");
            sql.append(paymentId).append("','");
            sql.append(SessionSkatingClass.calculateSeason());
            sql.append("')");
            System.out.println(sql.toString());
    
            // Execute the INSERT to create the new membership table entry
            try {
                Connection con = getConnection();
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql.toString());
                stmt.close();
                
                // Now look up the membershipId that was generated
                String season = SessionSkatingClass.calculateSeason();
                StringBuffer msql = new StringBuffer();
                msql.append("select mid, pid, paymentid, season from membership where ");
                msql.append("pid = '").append(person.getPid()).append("'");
                msql.append(" AND ");
                msql.append("season LIKE '").append(season).append("'");
                System.out.println(msql.toString());
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(msql.toString());
                if (rs.next()) {
                    // if we found a matching record, the record was created
                    results.setMembershipId(rs.getLong(1));
                    results.setMembershipCreated(true);
                    // TODO: set the paymentId in the results object
                }
                stmt.close();
                con.close();

            } catch(SQLException ex) {
                System.err.println("SQLException: " + ex.getMessage());
                results.setMembershipCreated(false);
                results.setMembershipErrorMessage("SQLException: " + ex.getMessage());
            }
        }
        
        // Loop through each of the entries we've been passed, and for each one
        // insert it in the database, look up its rosterid to create a new
        // RosterEntry for it, add this to the list of created roster entries
        for (RosterEntry entry : newEntryList) {
            
            RosterEntry newEntry = null;
            
            // Create the SQL INSERT statement
            StringBuffer sql = new StringBuffer();
            sql.append("insert into roster (classid, pid, paymentId) values ('");
            sql.append(entry.getClassid()).append("','");
            sql.append(entry.getPid()).append("',");
            sql.append(paymentId);
            sql.append(")");
            System.out.println(sql.toString());
    
            // Execute the INSERT to create the new roster table entry
            try {
                Connection con = getConnection();
                Statement stmt = con.createStatement();
                stmt.executeUpdate(sql.toString());
                stmt.close();
                
                // query the roster table to find the rosterid that was created
                stmt = con.createStatement();
                StringBuffer rsql = new StringBuffer();
                rsql.append(ROSTER_QUERY);
                rsql.append(" WHERE classid = '").append(entry.getClassid()).append("'");
                rsql.append(" AND ");
                rsql.append("pid = '").append(entry.getPid()).append("'");
                System.out.println(rsql.toString());
                
                ResultSet rs = stmt.executeQuery(rsql.toString());
                if (rs.next()) {
                    newEntry = createRosterEntry(rs);
                    entriesCreated.add(newEntry);
                } else {
                    entriesFailed.add(new Long(entry.getClassid()));
                }
                stmt.close();
                con.close();
    
            } catch(SQLException ex) {
                System.err.println("SQLException: " + ex.getMessage());
                entriesFailed.add(new Long(entry.getClassid()));
            }
        }
        
        results.setEntriesCreated(entriesCreated);
        results.setEntriesNotCreated(entriesFailed);
        
        return results;
    }
    
    /**
     * Look up the roster of classes for which this student has registered and
     * return it as an ArrayList.
     * @param person the person for whom the roster is compiled
     * @return an ArrayList of RosterEntry objects 
     */
    public ArrayList<RosterEntry> getStudentRoster(LoginSession loginSession, Person person) {
        // Create the query string to find the roster membership
        StringBuffer sql = new StringBuffer();
        sql.append(ROSTER_QUERY);
        sql.append(" WHERE pid = ").append(person.getPid());
        System.out.println(sql.toString());
        
        return getRoster(loginSession, person, sql.toString());
    }
    
    /**
     * Look up the roster of classes for which this student has registered and
     * return it as an ArrayList.
     * @param person the person for whom the roster is compiled
     * @return an ArrayList of RosterEntry objects 
     */
    public ArrayList<RosterEntry> getClassRoster(LoginSession loginSession, Person person, long classId) {
        // Create the query string to find the roster membership
        StringBuffer sql = new StringBuffer();
        sql.append(ROSTER_QUERY);
        sql.append(" WHERE classid = ").append(classId);
        System.out.println(sql.toString());
        
        return getRoster(loginSession, person, sql.toString());
    }
    
    /**
     * Look up a roster of classes.  The exact roster looked up depends on the sql
     * that is passed into the class, sometimes for a particular student, sometimes
     * for a particular class.
     * @param person the person used to authenticate the connection
     * @param rosterQuery the SQL query used to find the roster
     * @return and ArrayList of RosterEntry objects matching the query
     */
    private ArrayList<RosterEntry> getRoster(LoginSession loginSession, Person person, String rosterQuery) {

        ArrayList<RosterEntry> roster = new ArrayList<RosterEntry>();
        
        // Check authentication credentials
        boolean isAuthentic = isSessionValid(loginSession);
        if (!isAuthentic) {
            return null;
        }
        
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(rosterQuery.toString());
            while (rs.next()) {
                RosterEntry newEntry = createRosterEntry(rs);
                roster.add(newEntry);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }

        return roster;
    }
    
    /**
     * Helper method to create a RosterEntry object from the results of a JDBC
     * query that contains the appropriate fields.
     * @param rs the result set containing the roster fields
     * @return an instance of RosterEntry
     * @throws SQLException if there is an error accessing the result set
     */
    private RosterEntry createRosterEntry(ResultSet rs) throws SQLException {
        RosterEntry entry = null;
        entry = new RosterEntry();
        entry.setRosterid(rs.getLong(1));
        entry.setClassid(rs.getLong(2));
        entry.setPid(rs.getLong(3));
        entry.setLevelpassed(rs.getString(4));
        entry.setPaymentid(rs.getLong(5));
        entry.setPayment_amount(rs.getDouble(6));
        entry.setPaypal_status(rs.getString(7));
        entry.setDate_updated(rs.getDate(8));
        entry.setSurname(rs.getString(9));
        entry.setGivenname(rs.getString(10));
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
                //person.setPassword(rs.getString(8));
                person.setPassword(null);
                person.setMember(false);
            }
            stmt.close();
            
            // Now look up if the person already paid their membership this season
            // If so, set their membership flag
            String season = SessionSkatingClass.calculateSeason();
            StringBuffer msql = new StringBuffer();
            msql.append("select mid, pid, paymentid, season from membership where ");
            msql.append("pid = '").append(pid).append("'");
            msql.append(" AND ");
            msql.append("season LIKE '").append(season).append("'");
            System.out.println(msql.toString());
            stmt = con.createStatement();
            rs = stmt.executeQuery(msql.toString());
            if (rs.next()) {
                // if we found a matching record, they have paid their membership
                person.setMember(true);
                person.setMembershipId(rs.getLong(1));
            }
            stmt.close();
            
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return person;
    }

    /**
     * Check the LoginSession to see if it is a valid session that corresponds
     * to the correct user and that it has not expired.
     * @param LoginSession containing the the sessionId to be checked
     * @return true if valid session is valid, false if otherwise
     */
    private boolean isSessionValid(LoginSession loginSession) {
        boolean isAuthenticated = false;
        HttpServletRequest request = this.getThreadLocalRequest();
        HttpSession session = request.getSession();
        if (loginSession != null && session.getId().equals(loginSession.getSessionId())) {
            // TODO: Also check if session corresponds to a PID (which requires having written the sessionId to the database with a PID
            isAuthenticated = true;
        } else {
            session.invalidate();
        }
        return isAuthenticated;
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
