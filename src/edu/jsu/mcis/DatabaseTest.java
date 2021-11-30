package edu.jsu.mcis;

import java.sql.*;
import org.json.simple.*;

public class DatabaseTest {
    
    public static void main(String[] args) {
        
        DatabaseTest dbtest = new DatabaseTest();
        JSONArray results =  dbtest.getJSON();
        
        System.out.println( JSONValue.toJSONString(results) );
    
    }
    public JSONArray getJSON() {
        
        Connection conn = null;
        PreparedStatement pstSelect = null, pstUpdate = null;
        ResultSet resultset = null;
        ResultSetMetaData metadata = null;
        
        String query;
        JSONArray record = new JSONArray();
        
        boolean hasresults;
        int resultCount, columnCount;
        
        try {
            
            /* Server Details */
            String server = ("jdbc:mysql://localhost/p2_test");
            String username = "root";
            String password = "CS488";
            System.out.println("Connecting to " + server + "...");
            
            /* MySQL JDBC Driver */
            
            conn = DriverManager.getConnection(server, username, password);
            
            if (conn.isValid(0)) {
                
                System.out.println("Successful Connection!");
                
                query = "SELECT * FROM people";
                pstSelect = conn.prepareStatement(query);
                
                System.out.println("Query select...");
                hasresults = pstSelect.execute();                
                
                System.out.println("Query Results ...");
                while ( hasresults || pstSelect.getUpdateCount() != -1 ) {
                    if ( hasresults ) {
                        resultset = pstSelect.getResultSet();
                        metadata = resultset.getMetaData();
                        columnCount = metadata.getColumnCount();
                        
                        while(resultset.next()) {
                            JSONObject json = new JSONObject();
                            
                            /* Loop ResultSet */

                            for (int i = 2; i <= columnCount; i++) {                                
                                json.put(metadata.getColumnLabel(i),resultset.getString(i));
                            }
                            record.add(json);
                        }
                    }

                    else {

                        resultCount = pstSelect.getUpdateCount();  
                        if ( resultCount == -1 ) {
                            break;
                        }
                    }
                    
                    hasresults = pstSelect.getMoreResults();

                }
                
            }
            
            conn.close();
        }
        
        catch (Exception e) {
            System.err.println(e.toString());
        }
        
        finally {
            
            if (resultset != null) { try { resultset.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (pstSelect != null) { try { pstSelect.close(); } catch (Exception e) { e.printStackTrace(); } }
            if (pstUpdate != null) { try { pstUpdate.close(); } catch (Exception e) { e.printStackTrace(); } }

        }       
        return record; 
    }    
    
}
    
