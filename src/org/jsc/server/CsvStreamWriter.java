package org.jsc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import com.csvreader.CsvWriter;

/**
 * Create a writer that can write out a CSV formatted file using the results
 * from a JDBC PreparedStatement as input.
 * @author jones
 */
public class CsvStreamWriter {
    
    CsvWriter csv = null;
    
    /**
     * Test the class through a static main method
     * @param args not used
     */
    public static void main(String[] args) {
        Writer w = new PrintWriter(System.out);
        CsvStreamWriter csw = new CsvStreamWriter(w);
        csw.runTestQuery();
    }

    /**
     * Construct a new object that writes to Writer w
     * @param w the destination for CSV output, which will be closed when writing completes
     */
    public CsvStreamWriter(Writer w) {
        csv = new CsvWriter(w, ',');
    }

    /**
     * Format and write results of a JDBC query to CSV.
     * @param stmt the PreparedStatement representing the query to be run and written to CSV
     */
    public void writeToCsv(PreparedStatement stmt) {
        Connection con = null;
        try {
            con = stmt.getConnection();
            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData rsm = rs.getMetaData();
            int columnCount = rsm.getColumnCount();
            
            // Get the column headers and write them first
            String[] header = new String[columnCount];
            for (int i=0; i<columnCount; i++) {
                header[i] = rsm.getColumnLabel(i+1);
            }
            writeCsvRecord(header);
            
            // Loop through records, writing each to the CSV stream
            while (rs.next()) {
                String[] record = new String[columnCount];
                for (int i=0; i<columnCount; i++) {
                    record[i] = rs.getString(i+1);
                }
                writeCsvRecord(record);
            }
            stmt.close();
            con.close();
            csv.close();
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
                csv.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Write a single CSV record entry, and flush the buffer as we go.
     * @param s array of values to be written in CSV format
     * @throws IOException
     */
    private void writeCsvRecord(String[] s) throws IOException {
            csv.writeRecord(s);
            csv.flush();
    }

    /**
     * Test method.
     */
    private void runTestQuery() {
        Connection con = SkaterRegistrationServiceImpl.getConnection();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT sc.season,sc.sessionname as session, ");
        sql.append("sc.classtype, sc.day, p.surname, p.givenname, y.paypal_status ");
        sql.append("FROM roster r, people p, payment y, sessionclasses sc ");
        sql.append("WHERE r.pid = p.pid ");
        sql.append("AND r.paymentid = y.paymentid ");
        sql.append("AND r.classid = sc.classid ");
        sql.append("AND sc.season = ? ");
        sql.append("AND sc.sessionname = ? ");
        sql.append("ORDER BY sc.season,sc.sessionname, sc.classtype, sc.day, p.surname, p.givenname");
        try {
            PreparedStatement stmt = con.prepareStatement(sql.toString());
            stmt.setString(1, "2010-2011");
            stmt.setString(2, "1");
            this.writeToCsv(stmt);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
