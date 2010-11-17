package org.jsc.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import com.csvreader.CsvWriter;

public class CsvStreamWriter {
    
    CsvWriter csv = null;
    
    public static void main(String[] args) {
        Writer w = new PrintWriter(System.out);
        CsvStreamWriter csw = new CsvStreamWriter(w);
        csw.runTestQuery();
    }

    public CsvStreamWriter(Writer w) {
        csv = new CsvWriter(w, ',');
    }

    public void writeToCsv(Statement stmt, String sql) {
        Connection con = null;
        try {
            con = stmt.getConnection();
            ResultSet rs = stmt.executeQuery(sql);
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
        } catch (SQLException ex) {
            System.err.println("SQLException: " + ex.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                con.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void writeCsvRecord(String[] s) throws IOException {
            csv.writeRecord(s);
            csv.flush();
    }

    private void runTestQuery() {
        Connection con = SkaterRegistrationServiceImpl.getConnection();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT sc.season,sc.sessionname as session, ");
        sql.append("sc.classtype, sc.day, p.surname, p.givenname, y.paypal_status ");
        sql.append("FROM roster r, people p, payment y, sessionclasses sc ");
        sql.append("WHERE r.pid = p.pid ");
        sql.append("AND r.paymentid = y.paymentid ");
        sql.append("AND r.classid = sc.classid ");
        sql.append("AND sc.season = '2010-2011' ");
        sql.append("AND sc.sessionname = '1' ");
        sql.append("ORDER BY sc.season,sc.sessionname, sc.classtype, sc.day, p.surname, p.givenname");
        try {
            Statement stmt = con.createStatement();
            writeToCsv(stmt, sql.toString());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
