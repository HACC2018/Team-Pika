package com.pika.lambda;

import java.io.IOException;
import java.sql.*;

import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;


public class RDSToS3 {

    public void myHandler() throws IOException {
        Connection conn = null;
        Statement stmt = null;
        final String URL = String.format("jdbc:postgresql://%s:%s/%s", System.getenv("DB_HOST"), System.getenv("DB_PORT"), System.getenv("DB_NAME"));
        final String USER = System.getenv("DB_USER");
        final String PASS = System.getenv("DB_PASS");

        try {
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection(URL, USER, PASS);

            stmt = conn.createStatement();
            String sql = "SELECT * FROM hacc.energy";
            ResultSet rs = stmt.executeQuery(sql);

            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            JsonObject obj = new JsonObject();

            while (rs.next()) {
                obj.addProperty(rsMetaData.getColumnName(1), rs.getString(1));
                for (int i = 2; i <= columnCount; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    String prettyColumnName = StringUtils.capitalize(columnName.replace("_", " "));
                    obj.addProperty(prettyColumnName, rs.getDouble(columnName));
                }
            }

            System.out.println(obj);

            stmt.close();
            conn.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }
}
