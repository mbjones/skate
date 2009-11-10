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
import org.jsc.client.SQLRecordException;
import org.jsc.client.SessionSkatingClass;
import org.jsc.client.SkaterRegistrationService;
import org.mindrot.BCrypt;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * An implementation of a registration service that handles the creation of 
 * new accounts as well as registration of people into classes.
 * @author Matt Jones
 */
public class SkaterRegistrationServiceImpl extends RemoteServiceServlet
        implements SkaterRegistrationService {

    private static final String JDBC_URL = ServerConstants.getString("JDBC_URL"); //$NON-NLS-1$
    private static final String JDBC_USER = ServerConstants.getString("JDBC_USER"); //$NON-NLS-1$
    private static final String JDBC_PASS = ServerConstants.getString("JDBC_PASS"); //$NON-NLS-1$
    private static final String JDBC_DRIVER = ServerConstants.getString("JDBC_DRIVER"); //$NON-NLS-1$
    private static final String ROSTER_QUERY = "SELECT rosterid, classid, pid, levelPassed, paymentid, payment_amount, paypal_status, date_updated, surname, givenname, section FROM rosterpeople"; //$NON-NLS-1$
    private static final int MAX_SESSION_INTERVAL = 60 * 30;
    
    /**
     * Create a new person entry in the backing relational database, or update
     * fields on an existing entry.  If the 'pid' field of the person is empty
     * or 0, a new entry is created.  Otherwise, the entry with the pid is
     * updated.
     * @param person the Person to be created or updated in the database
     * @return the Person that was created or updated, or null on error
     * @throws DuplicateRecordException if the username already exists
     */
    public Person createAccount(LoginSession loginSession, Person person) throws SQLRecordException {
        long pid = 0;

        StringBuffer sql = new StringBuffer();
        if (person.getPid() == 0) {
            // Creating a new account, so no authentication needed
            
            // Look up the pid to be used for this insert
            long newPid = 0;
            StringBuffer idsql = new StringBuffer();
            idsql.append("SELECT NEXTVAL(\'\"person_id_seq\"\')"); //$NON-NLS-1$
            System.out.println(idsql.toString());
            Statement stmt;
            try {
                Connection con = getConnection();
                stmt = con.createStatement();
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(idsql.toString());
                if (rs.next()) {
                    // This is the next id in the payment id sequence
                    newPid = rs.getLong(1);
                }
                stmt.close();
            } catch (SQLException e) {
                System.err.println("SQLException: " + e.getMessage()); //$NON-NLS-1$
                return null;
            }
            
            // Create the SQL INSERT statement
            sql.append("insert into people"); //$NON-NLS-1$
            sql.append(" (pid, surname, givenname, middlename, email, birthdate, home_phone, cell_phone, work_phone," + //$NON-NLS-1$
            		"street1, street2, city, state, zipcode, parentfirstname, parentsurname, parentemail, username, password) "); //$NON-NLS-1$
            sql.append("values ("); //$NON-NLS-1$
            sql.append(newPid).append(",'"); //$NON-NLS-1$
            sql.append(person.getLname()).append("','"); //$NON-NLS-1$
            sql.append(person.getFname()).append("','"); //$NON-NLS-1$
            sql.append(person.getMname()).append("','"); //$NON-NLS-1$
            sql.append(person.getEmail()).append("','"); //$NON-NLS-1$
            sql.append(person.getBday()).append("','"); //$NON-NLS-1$
            sql.append(person.getHomephone()).append("','"); //$NON-NLS-1$
            sql.append(person.getCellphone()).append("','"); //$NON-NLS-1$
            sql.append(person.getWorkphone()).append("','"); //$NON-NLS-1$
            sql.append(person.getStreet1()).append("','"); //$NON-NLS-1$
            sql.append(person.getStreet2()).append("','"); //$NON-NLS-1$
            sql.append(person.getCity()).append("','"); //$NON-NLS-1$
            sql.append(person.getState()).append("','"); //$NON-NLS-1$
            sql.append(person.getZip()).append("','"); //$NON-NLS-1$
            sql.append(person.getParentFirstname()).append("','"); //$NON-NLS-1$
            sql.append(person.getParentLastname()).append("','"); //$NON-NLS-1$
            sql.append(person.getParentEmail()).append("','"); //$NON-NLS-1$
            sql.append(person.getUsername()).append("','"); //$NON-NLS-1$

            // Hash the password before insertion into the database
            String hashed = BCrypt.hashpw(person.getNewPassword(), BCrypt.gensalt());
            sql.append(hashed).append("'"); //$NON-NLS-1$
            sql.append(")"); //$NON-NLS-1$
            
        } else {
            // Verify that the session is valid before allowing an update
            boolean isAuthentic = isSessionValid(loginSession);
            if (!isAuthentic) {
                return null;
            }
            sql.append("update people set "); //$NON-NLS-1$
            sql.append("surname='").append(person.getLname()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("givenname='").append(person.getFname()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("middlename='").append(person.getMname()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("email='").append(person.getEmail()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("birthdate='").append(person.getBday()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("home_phone='").append(person.getHomephone()).append("',");   //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("cell_phone='").append(person.getCellphone()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("work_phone='").append(person.getWorkphone()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("street1='").append(person.getStreet1()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("street2='").append(person.getStreet2()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("city='").append(person.getCity()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("state='").append(person.getState()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("zipcode='").append(person.getZip()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("parentfirstname='").append(person.getParentFirstname()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("parentsurname='").append(person.getParentLastname()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$
            sql.append("parentemail='").append(person.getParentEmail()).append("',"); //$NON-NLS-1$ //$NON-NLS-2$

            if (person.getNewUsername() != null && person.getNewUsername().length() > 0 && 
                    !person.getUsername().equals(person.getNewUsername())) {
                sql.append("username='").append(person.getNewUsername()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            } else {
                sql.append("username='").append(person.getUsername()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            if (person.getNewPassword() != null && person.getNewPassword().length() > 0) {
                // Hash the password before insertion into the database
                String hashed = BCrypt.hashpw(person.getNewPassword(), BCrypt.gensalt());
                sql.append(",password='").append(hashed).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            }
            sql.append(" where pid=").append(person.getPid()); //$NON-NLS-1$
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
                        "SELECT max(pid) from people where email LIKE '" +  //$NON-NLS-1$
                        person.getEmail() + "'"); //$NON-NLS-1$
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
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
            throw new SQLRecordException(ex);
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
    
    public boolean resetPassword(String username) {
        boolean successFlag = false;
        Person person = null;
        LoginSession loginSession = null;
        
        if (username != null) {

            // TODO: Look up the username in the DB to find the email address
            String email = lookupUsername(username);
            System.out.println("Found email: " + email);
            
            // TODO: Reset the password
            
            // TODO: Email the new password to the user
            
            if (email != null) {
                successFlag = true;
            }
        }
        
        return successFlag;
    }
    
    private String lookupUsername(String username) {
        String email = "";
        // Query the database to get the list of emails
        StringBuffer sql = new StringBuffer();
        sql.append("select pid, username, email "); 
        sql.append("from people "); 
        sql.append("where username = '");
        sql.append(username).append("';");
        System.out.println(sql.toString());
        
        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                SessionSkatingClass sc = new SessionSkatingClass();
                long pid = rs.getLong(1);
                String uname = rs.getString(2);
                email = rs.getString(3);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
        }
        
        // TODO: need to return the pid too for more processing
        return email;
    }

    public boolean findUsername(String email) {
        boolean successFlag = false;
        Person person = null;
        LoginSession loginSession = null;
        
        if (email != null) {

            // TODO: Look up the email in the DB to find the usernames
                        
            // TODO: Email the list of usernames to the user's registered email
            
            successFlag = true;
        }
        
        return successFlag;
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
        sql.append("otherinstructors, surname, givenname, activesession, discountDate from sessionclasses");
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
                sc.setActiveSession(rs.getBoolean(14));
                sc.setDiscountDate(rs.getString(15));
                classList.add(sc);
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
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
            idsql.append("SELECT NEXTVAL(\'\"payment_id_seq\"\')"); //$NON-NLS-1$
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
            psql.append("insert into payment (paymentId, paypal_status) VALUES ("  //$NON-NLS-1$
                    + paymentId + ", 'Pending')"); //$NON-NLS-1$
            System.out.println(psql.toString());
   
            stmt = con.createStatement();
            stmt.executeUpdate(psql.toString());
            stmt.close();
            con.close();
            results.setPaymentId(paymentId);
            
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
            results.setMembershipCreated(false);
            results.setMembershipErrorMessage("SQLException: " + ex.getMessage()); //$NON-NLS-1$
        }
        
        // Check if a membership entry should be created, and do so
        if (createMembership) {
            results.setMembershipAttempted(true);
            // Create the SQL INSERT statement
            StringBuffer sql = new StringBuffer();
            sql.append("insert into membership (pid, paymentid, season) VALUES ('"); //$NON-NLS-1$
            sql.append(person.getPid()).append("','"); //$NON-NLS-1$
            sql.append(paymentId).append("','"); //$NON-NLS-1$
            sql.append(SessionSkatingClass.calculateSeason());
            sql.append("')"); //$NON-NLS-1$
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
                msql.append("select mid, pid, paymentid, season, paypal_status from memberstatus where "); //$NON-NLS-1$
                msql.append("pid = '").append(person.getPid()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
                msql.append(" AND "); //$NON-NLS-1$
                msql.append("season LIKE '").append(season).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
                System.out.println(msql.toString());
                stmt = con.createStatement();
                ResultSet rs = stmt.executeQuery(msql.toString());
                if (rs.next()) {
                    // if we found a matching record, the record was created
                    results.setMembershipId(rs.getLong(1));
                    results.setMembershipCreated(true);
                    results.setPaymentId(paymentId);
                    results.setMembershipStatus(rs.getString(5));
                }
                stmt.close();
                con.close();

            } catch(SQLException ex) {
                System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
                results.setMembershipCreated(false);
                results.setMembershipErrorMessage("SQLException: " + ex.getMessage()); //$NON-NLS-1$
            }
        }
        
        // Loop through each of the entries we've been passed, and for each one
        // insert it in the database, look up its rosterid to create a new
        // RosterEntry for it, add this to the list of created roster entries
        for (RosterEntry entry : newEntryList) {
            
            RosterEntry newEntry = null;
            
            // Create the SQL INSERT statement
            StringBuffer sql = new StringBuffer();
            sql.append("insert into roster (classid, pid, paymentId) values ('"); //$NON-NLS-1$
            sql.append(entry.getClassid()).append("','"); //$NON-NLS-1$
            sql.append(entry.getPid()).append("',"); //$NON-NLS-1$
            sql.append(paymentId);
            sql.append(")"); //$NON-NLS-1$
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
                rsql.append(" WHERE classid = '").append(entry.getClassid()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
                rsql.append(" AND "); //$NON-NLS-1$
                rsql.append("pid = '").append(entry.getPid()).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
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
                System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
                entriesFailed.add(new Long(entry.getClassid()));
            }
        }
        
        results.setEntriesCreated(entriesCreated);
        results.setEntriesNotCreated(entriesFailed);
        
        return results;
    }
    
    /**
     * Cancel the registration entries and membership entries associated with a
     * payment invoice.  The method checks that the person removing the entry is
     * the person who submitted it (or has role of COACH or ADMIN), 
     * and that it is in Pending status.
     */
    public boolean cancelInvoice(LoginSession loginSession, long paymentid) {
        // Check authentication credentials
        if (!isSessionValid(loginSession)) {
            return false;
        }
        
        // Look up the paymentid and see if the pid matches, or if the login 
        // person is authorized because they are a coach or administrator
        boolean isAuthorized = false;
        try {
            // Now check if the person logged in matches the registrant for the record
            // and that the payment is actually pending
            boolean isPending = false;
            StringBuffer invoiceQuery = new StringBuffer();
            invoiceQuery.append("select py.paymentid, py.paypal_status, r.rosterid, r.pid " +
            		"from payment py, roster r " + 
            		"where py.paymentid = r.paymentid " + 
            		"and py.paypal_status = 'Pending' " + 
            		"and py.paymentid = ");
            invoiceQuery.append(paymentid);
            System.out.println(invoiceQuery.toString());
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(invoiceQuery.toString());
            if (rs.next()) {
                isPending = true;
                long pid = rs.getLong(4);
                // Check if the person who is logged in owns the invoice records
                if (loginSession.getPerson().getPid() == pid) {
                    isAuthorized = true;
                }
            }
            stmt.close();
            System.out.println("cancelAuth: " + isPending + " " + isAuthorized);
            con.close();

            // if not yet authorized, but the record is pending, see if the
            // loginSession represents a coach or admin, and if so authorize
            if (!isAuthorized && isPending) {
                long role = getRole(loginSession.getPerson().getPid());
                if (role >= Person.COACH) {
                    isAuthorized = true;
                    System.out.println("cancelAuth role is: " + role);
                }
            }

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        if (isAuthorized) {
            
            // Delete the invoice, cascading to the membership and roster tables
            Connection con = getConnection();
            try {
                con.setAutoCommit(false);
                
                String tables[] = {"membership", "roster", "payment"}; 
                for (String table : tables) {
                    StringBuffer sql = new StringBuffer();
                    sql.append("DELETE from "); 
                    sql.append(table);
                    sql.append(" where paymentid = ");
                    sql.append(paymentid);
                    System.out.println(sql.toString());
                    Statement stmt = con.createStatement();
                    int rowcount = stmt.executeUpdate(sql.toString());
                    System.out.println("Deleted " + rowcount + " rows from " + table + ".");
                    stmt.close(); 
                }
                con.commit();
                con.close();
    
            } catch(SQLException ex) {
                try {
                    con.rollback();
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Unable to rollback during invoice deletion." + e.getMessage());
                }
                System.err.println("SQLException: " + ex.getMessage());
                return false;
            }
        }
        
        // Return true if the entry was deleted successfully
        return isAuthorized;
    }
    
    /**
     * Update the roster table with new section and levelpassed information.
     * The method checks that the person removing the entry is
     * a COACH or ADMIN role.
     */
    public boolean saveRoster(LoginSession loginSession, long rosterid, String newLevel, String newSection) {
        // Check authentication credentials
        if (!isSessionValid(loginSession)) {
            return false;
        }

        // Check if person is authorized because they are a coach or administrator
        boolean isAuthorized = false;
        long role = getRole(loginSession.getPerson().getPid());
        if (role >= Person.COACH) {
            isAuthorized = true;
            System.out.println("cancelAuth role is: " + role);
        }
        
        if (isAuthorized) {
            // Save the new information to the roster tables
            Connection con = getConnection();
            try {
                con.setAutoCommit(false);            
                StringBuffer sql = new StringBuffer();
                sql.append("UPDATE roster ");
                sql.append("SET");
                sql.append(" levelpassed = '").append(newLevel).append("'");
                sql.append(", section = '").append(newSection).append("'");
                sql.append(" where rosterid = ");
                sql.append(rosterid);
                System.out.println(sql.toString());
                Statement stmt = con.createStatement();
                int rowcount = stmt.executeUpdate(sql.toString());
                System.out.println("Updated " + rowcount + " rows in roster.");
                stmt.close(); 
                con.commit();
                con.close();
    
            } catch(SQLException ex) {
                try {
                    con.rollback();
                    con.close();
                } catch (SQLException e) {
                    System.out.println("Unable to rollback during invoice deletion." + e.getMessage());
                    return false;
                }
                System.err.println("SQLException: " + ex.getMessage());
                return false;
            }
        }
        
        return isAuthorized;
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
        
        return getRoster(loginSession, sql.toString());
    }
    
    /**
     * Look up the roster of students enrolled in a class and
     * return it as an ArrayList.
     * @param loginSession the session used to authenticate
     * @param classId the identifier for the class reqeusted
     * @return an ArrayList of RosterEntry objects 
     */
    public ArrayList<RosterEntry> getClassRoster(LoginSession loginSession, long classId) {
        // Create the query string to find the roster membership
        StringBuffer sql = new StringBuffer();
        sql.append(ROSTER_QUERY);
        sql.append(" WHERE classid = ").append(classId);
        System.out.println(sql.toString());
        
        return getRoster(loginSession, sql.toString());
    }
    
    /**
     * Look up a roster of classes.  The exact roster looked up depends on the sql
     * that is passed into the class, sometimes for a particular student, sometimes
     * for a particular class.
     * @param loginSession used to authenticate the session
     * @param rosterQuery the SQL query used to find the roster
     * @return and ArrayList of RosterEntry objects matching the query
     */
    private ArrayList<RosterEntry> getRoster(LoginSession loginSession, String rosterQuery) {

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
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
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
        entry.setSection(rs.getString(11));
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
        sql.append("select pid, surname, givenname, middlename, email, birthdate, home_phone, " +
        		"cell_phone, work_phone, street1, street2, city, state, zipcode, parentfirstname, " + 
        		"parentsurname, parentemail, username, password, role from people where "); 
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
                person.setBday(rs.getString(6));
                person.setHomephone(rs.getString(7));
                person.setCellphone(rs.getString(8));
                person.setWorkphone(rs.getString(9));
                person.setStreet1(rs.getString(10));
                person.setStreet2(rs.getString(11));
                person.setCity(rs.getString(12));
                person.setState(rs.getString(13));
                person.setZip(rs.getString(14));
                person.setParentFirstname(rs.getString(15));
                person.setParentLastname(rs.getString(16));
                person.setParentEmail(rs.getString(17));
                person.setUsername(rs.getString(18));
                person.setRole(rs.getInt(20));
                person.setPassword(null);
                person.setMember(false);
            }
            stmt.close();
            
            // Now look up if the person already paid their membership this season
            // If so, set their membership flag
            String season = SessionSkatingClass.calculateSeason();
            StringBuffer msql = new StringBuffer();
            msql.append("select mid, pid, paymentid, season, paypal_status from memberstatus where "); //$NON-NLS-1$
            msql.append("pid = '").append(pid).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            msql.append(" AND "); //$NON-NLS-1$
            msql.append("season LIKE '").append(season).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
            System.out.println(msql.toString());
            stmt = con.createStatement();
            rs = stmt.executeQuery(msql.toString());
            if (rs.next()) {
                // if we found a matching record, they have paid their membership
                person.setMember(true);
                person.setMembershipId(rs.getLong(1));
                person.setMembershipPaymentId(rs.getLong(3));
                person.setMembershipStatus(rs.getString(5));
            } else {
                person.setMember(false);
                person.setMembershipId(0);
                person.setMembershipPaymentId(0);
                person.setMembershipStatus("Unpaid"); //$NON-NLS-1$
            }
            stmt.close();
            
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
        }
        
        return person;
    }

    /**
     * Look up the role for a person, assuming the session is authorized already for this lookup
     * @param pid the identifier of the person to look up
     * @return the role for this person
     */
    private long getRole(long pid) {
        long role = Person.SKATER;
        StringBuffer roleQuery = new StringBuffer();
        roleQuery.append("select p.pid, p.role " +
                "from people p " + 
                "where p.pid = ");
        roleQuery.append(pid);
        System.out.println(roleQuery.toString());
        Connection con = getConnection();
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(roleQuery.toString());
            if (rs.next()) {
                role = rs.getLong(2);
            }
            stmt.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return role;
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
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the id of the person if valid, otherwise 0
     */
    private int checkPassword(String username, String password) {
        int pid = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("select pid,password from people where "); //$NON-NLS-1$
        sql.append("username LIKE '").append(username).append("'"); //$NON-NLS-1$ //$NON-NLS-2$
        System.out.println(sql.toString());

        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(sql.toString());
            if (rs.next()) {
                pid = rs.getInt(1);
                String hashed = rs.getString(2);
                // Check that an unencrypted password matches one that has
                // previously been hashed
                if (!BCrypt.checkpw(password, hashed)) {
                    System.out.println("It does not match"); //$NON-NLS-1$
                    pid = 0;
                }
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
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
            System.err.print("ClassNotFoundException: "); //$NON-NLS-1$
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASS);
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage()); //$NON-NLS-1$
        }
        
        return con;
    }
}
