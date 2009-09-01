package org.jsc.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * An implementation of a servlet that listens for connections from paypal and 
 * logs the receipt data from the site, but only if it is valid.
 * @author Matt Jones
 */
public class PaypalIPNServlet extends HttpServlet {

//    private static final String PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr";
    private static final String PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    private static final String JDBC_URL = "jdbc:postgresql://localhost/jscdb";
    private static final String JDBC_USER = "jscdb";
    private static final String JDBC_PASS = "1skate2";
    private static final String JDBC_DRIVER = "org.postgresql.Driver";
    private static final String ROSTER_QUERY = "SELECT rosterid, classid, pid, levelPassed, paymentid, payment_amount, paypal_status, date_updated, surname, givenname FROM rosterpeople";
    private static final int MAX_SESSION_INTERVAL = 60 * 30;
    
    public void init() {
        System.out.println("PaypalIPNServlet initialized.");
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        PrintWriter out = null;
        processIpnRequest(request);
/*
        try {
            response.setContentType("text/html");
            out = response.getWriter();
            out.println("<html><body>");
            out.println("<p><b>yowza</b></p>");
                        
            out.print("</body></html>");
            out.close();
            
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
*/

    }

    private void processIpnRequest(HttpServletRequest request) {
        Enumeration<String> en = request.getParameterNames();
        String str = "cmd=_notify-validate";
        while (en.hasMoreElements()) {
            String paramName = en.nextElement();
            String paramValue = request.getParameter(paramName);
            str = str + "&" + paramName + "="
                    + URLEncoder.encode(paramValue);
        }

        // post back to PayPal system to validate
        // NOTE: change http: to https: in the following URL to verify using SSL (for increased security).
        // using HTTPS requires either Java 1.4 or greater, or Java Secure Socket Extension (JSSE)
        // and configured for older versions.
        URL u;
        try {
            u = new URL(PAYPAL_URL);
            URLConnection uc = u.openConnection();
            uc.setDoOutput(true);
            uc.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            PrintWriter pw = new PrintWriter(uc.getOutputStream());
            pw.println(str);
            pw.close();

            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String res = in.readLine();
            in.close();

            // assign posted variables to local variables
            String itemName = request.getParameter("item_name");
            String itemNumber = request.getParameter("item_number");
            String paymentStatus = request.getParameter("payment_status");
            String paymentAmount = request.getParameter("mc_gross");
            String paymentCurrency = request.getParameter("mc_currency");
            String txnId = request.getParameter("txn_id");
            String receiverEmail = request.getParameter("receiver_email");
            String payerEmail = request.getParameter("payer_email");

            //check notification validation
            if (res.equals("VERIFIED")) {
                System.out.println("Verified IPN connection");
                System.out.println("item_name: " + itemName);
                System.out.println("item_number: " + itemNumber);
                System.out.println("payment_status: " + paymentStatus);
                System.out.println("mc_gross: " + paymentAmount);
                System.out.println("mc_currency: " + paymentCurrency);
                System.out.println("txn_id: " + txnId);
                System.out.println("receiver_email: " + receiverEmail);
                System.out.println("payer_email: " + payerEmail);
                // check that paymentStatus=Completed
                // check that txnId has not been previously processed
                // check that receiverEmail is your Primary PayPal email
                // check that paymentAmount/paymentCurrency are correct
                // process payment
            } else if (res.equals("INVALID")) {
                System.out.println("Invalid IPN connection");
                // log for investigation
            } else {
                // error
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    /**
     * Create a new person entry in the backing relational database, or update
     * fields on an existing entry.  If the 'pid' field of the person is empty
     * or 0, a new entry is created.  Otherwise, the entry with the pid is
     * updated.
     * @param person the Person to be created or updated in the database
     * @return the Person that was created or updated, or null on error
     */
    /*
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
    */
    
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
    /*
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
