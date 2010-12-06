package org.jsc.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * A servlet that downloads a file that was previously prepared from the GWT server application.
 * @author Matt Jones
 */
public class CSVDownloadServlet extends HttpServlet {

    private static final String JDBC_URL = ServerConstants.getString("JDBC_URL"); //$NON-NLS-1$
    private static final String JDBC_USER = ServerConstants.getString("JDBC_USER"); //$NON-NLS-1$
    private static final String JDBC_PASS = ServerConstants.getString("JDBC_PASS"); //$NON-NLS-1$
    private static final String JDBC_DRIVER = ServerConstants.getString("JDBC_DRIVER"); //$NON-NLS-1$
    
    private static final String PAYPAL_URL = "https://www.paypal.com/cgi-bin/webscr";
//    private static final String PAYPAL_URL = "https://www.sandbox.paypal.com/cgi-bin/webscr";
    
    public void init() {
        System.out.println("CSVDownloadServlet initialized.");
    }
    
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Handling POST input");
        processRequest(request, response);
    }
    
    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        System.out.println("Handling GET input");
        processRequest(request, response);
    }        
    
    private void processRequest(HttpServletRequest request, HttpServletResponse response) {
    
        // Extract the key from the request passed in
        long key = 0L;
        String keyString = request.getParameter("key");
        if (keyString != null) {
            key = new Long(keyString);
        }
        String filepath = "";
        
        // Lookup the temp filename for the key that was given, and send it back
        StringBuffer isql = new StringBuffer();
        isql.append("SELECT randomkey, filepath FROM downloads WHERE randomkey = ?");
        Connection con = SkaterRegistrationServiceImpl.getConnection();
        PreparedStatement stmt;
        try {
            stmt = con.prepareStatement(isql.toString());
            stmt.setLong(1, key);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                key = rs.getLong(1);
                filepath = rs.getString(2);
                
                // Having found the filename, open it and send it back to the client
                PrintWriter out = response.getWriter();
                File data = new File(filepath);
                if (data.exists()) {
                    String filename = data.getName();
                    response.setContentType("text/csv");
                    response.setHeader("Content-disposition:", "attachment; filename=" + filename);
                    
                    FileReader fr = new FileReader(data);
                    IOUtils.copy(fr, out);
                }
                out.close();
            }
            stmt.close();
            con.close();
            
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
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
