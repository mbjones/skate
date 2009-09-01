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
import java.sql.Statement;
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
    
    public void init() {
        System.out.println("PaypalIPNServlet initialized.");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Handling POST input");
        processIpnRequest(request, response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Handling GET input");
        processIpnRequest(request, response);
    }        
    
    private void processIpnRequest(HttpServletRequest request, HttpServletResponse response) {
    
        Enumeration<String> en = request.getParameterNames();
        String str = "cmd=_notify-validate";
        while (en.hasMoreElements()) {
            String paramName = en.nextElement();
            String paramValue = request.getParameter(paramName);
            System.out.println("IPN SENT: " + paramName + " ==> " + paramValue);
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

            //check notification validation
            if (res.equals("VERIFIED")) {
                recordTransaction(request);
            } else if (res.equals("INVALID")) {
                System.out.println("Invalid IPN connection");
                // TODO: log for investigation
            } else {
                // TODO: error
            }
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    private void recordTransaction(HttpServletRequest request) {
        // assign posted variables to local variables
        String txnId = request.getParameter("txn_id");
        long paymentId = new Long(request.getParameter("invoice")).longValue();
        String paymentStatus = request.getParameter("payment_status");
        double paymentGross = new Double(request.getParameter("mc_gross")).doubleValue();
        double paypalFee = new Double(request.getParameter("mc_fee")).doubleValue();
        double discount = new Double(request.getParameter("discount")).doubleValue();
        double net = paymentGross - paypalFee;
        String receiverEmail = request.getParameter("receiver_email");
        String payerEmail = request.getParameter("payer_email");
        String payerId = request.getParameter("payer_id");

        System.out.println("Verified IPN connection");
        System.out.println("txn_id: " + txnId);
        System.out.println("paymentid: " + paymentId);
        System.out.println("payment_status: " + paymentStatus);
        System.out.println("mc_gross: " + paymentGross);
        System.out.println("discount: " + discount);
        System.out.println("mc_fee: " + paypalFee);
        System.out.println("net: " + net);
        System.out.println("receiver_email: " + receiverEmail);
        System.out.println("payer_email: " + payerEmail);
        System.out.println("payer_id: " + payerId);

        // check that paymentStatus=Completed
        // check that txnId has not been previously processed
        // check that receiverEmail is your Primary PayPal email
        // check that paymentAmount/paymentCurrency are correct
        // process payment
        
        // Create a SQL statement for updating the payment record
        StringBuffer sql = new StringBuffer();
        sql.append("update payment set ");
        sql.append("paypal_tx_id='").append(txnId).append("',");
        sql.append("paypal_status='").append(paymentStatus).append("'");
        if (paymentStatus.equals("Completed")) {
            sql.append(",paypal_gross=").append(paymentGross).append(",");
            sql.append("paypal_fee=").append(paypalFee).append(",");
            sql.append("discount=").append(discount).append(",");
            sql.append("paypal_net=").append(net).append(",");
            sql.append("payer_email='").append(payerEmail).append("',");
            sql.append("payer_id='").append(payerId).append("' ");
        }
        sql.append(" where paymentid=").append(paymentId);
        // payment_date DATE,         -- the date the payment was made
        System.out.println(sql.toString());
        
        // Now update it
        Connection con = getConnection();
        Statement stmt;
        try {
            stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();
        } catch (SQLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        /*
        IPN SENT: last_name ==> User
        IPN SENT: item_name2 ==> 2009-2010 Session 1 FS Synchro (Sunday 9:15-10:15am)
        IPN SENT: item_name1 ==> 2009-2010 Session 1 FS Club Ice (Friday 5:15-6:15pm)
        IPN SENT: test_ipn ==> 1
        IPN SENT: txn_type ==> cart
        IPN SENT: insurance_amount ==> 0.00
        IPN SENT: mc_handling2 ==> 0.00
        IPN SENT: mc_handling1 ==> 0.00
        IPN SENT: receiver_email ==> james_1202254981_biz@gmail.com
        IPN SENT: residence_country ==> US
        IPN SENT: payment_gross ==> 150.00
        IPN SENT: payment_date ==> 00:22:52 Sep 01, 2009 PDT
        IPN SENT: quantity2 ==> 1
        IPN SENT: payment_status ==> Completed
        IPN SENT: quantity1 ==> 1
        IPN SENT: discount ==> 10.0
        IPN SENT: mc_shipping ==> 0.00
        IPN SENT: first_name ==> Test
        IPN SENT: payer_email ==> matt_1202254711_per@gmail.com
        IPN SENT: protection_eligibility ==> Ineligible
        IPN SENT: payer_id ==> RFUQYKLDCF5YC
        IPN SENT: verify_sign ==> ApGG9CnBIkerLfn5t.mhAiSCmvc0AodE3nok0GG7v1iFjEmyGLeYA.VL
        IPN SENT: payment_type ==> instant
        IPN SENT: business ==> james_1202254981_biz@gmail.com
        IPN SENT: mc_gross_2 ==> 80.00
        IPN SENT: mc_gross_1 ==> 80.00
        IPN SENT: mc_fee ==> 4.65
        IPN SENT: transaction_subject ==> Shopping Cart
        IPN SENT: mc_shipping2 ==> 0.00
        IPN SENT: notify_version ==> 2.8
        IPN SENT: mc_currency ==> USD
        IPN SENT: mc_shipping1 ==> 0.00
        IPN SENT: custom ==> 
        IPN SENT: payment_fee ==> 4.65
        IPN SENT: payer_status ==> verified
        IPN SENT: mc_handling ==> 0.00
        IPN SENT: tax ==> 0.00
        IPN SENT: charset ==> windows-1252
        IPN SENT: tax2 ==> 0.00
        IPN SENT: tax1 ==> 0.00
        IPN SENT: item_number2 ==> 10014
        IPN SENT: item_number1 ==> 10013
        IPN SENT: shipping_discount ==> 0.00
        IPN SENT: invoice ==> 50011
        IPN SENT: mc_gross ==> 150.00
        IPN SENT: receiver_id ==> 339U3JVK2X4E6
        IPN SENT: txn_id ==> 8KH71840UC462913X
        IPN SENT: shipping_method ==> Default
        IPN SENT: num_cart_items ==> 2
        */
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
