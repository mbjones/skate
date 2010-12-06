package org.jsc.server;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

/**
 * A servlet that downloads a file that was previously prepared from the GWT server application.
 * @author Matt Jones
 */
public class CSVDownloadServlet extends HttpServlet {

//    private static final long EXP_MILLIS = 10*60*1000;
    private static final long EXP_MILLIS = 0;


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
            
            deleteOldTempFiles();
            
            stmt.close();
            con.close();
                        
        } catch (SQLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void deleteOldTempFiles() {
                
        StringBuffer isql = new StringBuffer();
        isql.append("SELECT randomkey, filepath FROM downloads WHERE date_updated < ?");
        Connection con = SkaterRegistrationServiceImpl.getConnection();
        PreparedStatement stmt;
        String dsql = "DELETE FROM downloads WHERE randomkey = ?";
        PreparedStatement dstmt;
        try {
            dstmt = con.prepareStatement(dsql);
            
            stmt = con.prepareStatement(isql.toString());
            Date expiration = new Date(System.currentTimeMillis() - EXP_MILLIS);
            stmt.setDate(1, new java.sql.Date(expiration.getTime()));
            System.out.println(stmt.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                
                long key = rs.getLong(1);
                String filepath = rs.getString(2);
                System.out.println("Found old file to delete with key: " + key);
                
                // Having found the filename, delete it
                File data = new File(filepath);
                if (data.exists()) {
                    data.delete();
                }
                
                // Now delete it from the downloads table
                dstmt.setLong(1, key);
                dstmt.executeUpdate();
            }
            stmt.close();
            dstmt.close();
            con.close();
            
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
    }
}
