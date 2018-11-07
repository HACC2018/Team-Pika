package com.pika.lambda;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.JsonArray;
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
            String sql = "SELECT * FROM hacc.energy WHERE fulldatetime>";
            ResultSet rs = stmt.executeQuery(sql);

            // Intermediate Java Object of the Data
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();
            JsonObject obj = new JsonObject();

            // Amazon S3
            Calendar cal = Calendar.getInstance();
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String bucketName = "teampika-tempbucket";

            while (rs.next()) {
                Timestamp timestamp = rs.getTimestamp(1);
                obj.addProperty(rsMetaData.getColumnName(1), timestamp.toString());
                for (int i = 2; i <= columnCount; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    String prettyColumnName = StringUtils.capitalize(columnName.replace("_", " "));
                    obj.addProperty(prettyColumnName, rs.getDouble(columnName));
                }

                cal.setTimeInMillis(timestamp.getTime());
                String filename = String.format("%d/%d/%d/%d.json", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH), cal.get(Calendar.HOUR_OF_DAY));
                s3.putObject(bucketName, filename, obj.toString());
            }

            stmt.close();
            conn.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }
}
