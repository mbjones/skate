package org.jsc.server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

    /**
     * Create a new account for the person in question
     * @return long the person identifier (pid) for the account created, or 0 on error
     */
    public long createAccount(Person person) {

        // Case: Inserting a new person
        if (person.getPid() > 0) {
            // TODO: check if the account already exists
            int pid = insertPerson(person);
            return pid;
        // Case: Updating an existing account
        } else if (person.getPid() == 0) {
            return person.getPid();
        } else {
            return 0;
        }
    }
    
    /**
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the id of the person if valid, otherwise 0
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
        Person person = null;
        person = lookupPerson(pid);
        return person;
    }
    
    /**
     * Check if the user is in the database, and if the given password matches
     * @param username the username of the person who is signing in
     * @param password the password of the person who is signing in
     * @return the id of the person if valid, otherwise 0
     */
    private Person lookupPerson(long pid) {

        StringBuffer sql = new StringBuffer();
        sql.append("select pid, surname, givenname, middlename, email, home_phone, birthdate from people where ");
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
            }
            stmt.close();
            con.close();

        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return person;
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
     * Create a new person entry in the backing relational database.
     * @param person the Person to be created in the database
     * @return the identifier of the person created
     */
    private int insertPerson(Person person) {
        int pid = 0;

        StringBuffer sql = new StringBuffer();
        sql.append("insert into people");
        sql.append(" (surname, givenname, middlename, email, home_phone, birthdate, password) ");
        sql.append("values ('");
        sql.append(person.getLname()).append("','");
        sql.append(person.getFname()).append("','");
        sql.append(person.getMname()).append("','");
        sql.append(person.getEmail()).append("','");
        sql.append(person.getHomephone()).append("','");
        sql.append(person.getBday()).append("','");
        sql.append(person.getPassword1()).append("'");
        sql.append(")");
        System.out.println(sql.toString());

        try {
            Connection con = getConnection();
            Statement stmt = con.createStatement();
            stmt.executeUpdate(sql.toString());
            stmt.close();
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT max(pid) from people");
            if (rs.next()) {
                pid = rs.getInt(1);
            } else {
                pid = 0;
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
        String url = "jdbc:postgresql://localhost/jscdb";
        String dbuser = "jones";
        String dbpass = "";
        String dbdriver = "org.postgresql.Driver";
        Connection con = null;
        
        try {
            Class.forName(dbdriver);
        } catch(java.lang.ClassNotFoundException e) {
            System.err.print("ClassNotFoundException: ");
            System.err.println(e.getMessage());
        }

        try {
            con = DriverManager.getConnection(url, dbuser, dbpass);
        } catch(SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        }
        
        return con;
    }
}
