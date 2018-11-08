package com.pika.lambda;

import java.io.IOException;
import java.sql.*;
import java.util.Calendar;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;


public class RDSToS3 {

    private void aggregateJsonArray(JsonArray arr, JsonArray agg) {
        JsonObject obj = new JsonObject();
        for (int i = 0; i < agg.size(); i++) {
            for (String key : agg.get(i).getAsJsonObject().keySet()) {
                if (key.equals("FullDateTime")) continue;
                if (obj.get(key) == null) {
                    obj.addProperty(key, agg.get(i).getAsJsonObject().get(key).getAsInt());
                }
                else {
                    obj.addProperty(key, obj.get(key).getAsInt() + agg.get(i).getAsJsonObject().get(key).getAsInt());
                }
            }
        }
        arr.add(obj);
    }

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

            // Intermediate Java Object of the Data
            ResultSetMetaData rsMetaData = rs.getMetaData();
            int columnCount = rsMetaData.getColumnCount();

            JsonArray hours = new JsonArray();
            JsonArray days = new JsonArray();
            JsonArray months = new JsonArray();

            // Amazon S3
            Calendar cal = Calendar.getInstance();
            AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
            String bucketName = "teampika-tempbucket";

            rs.next();
            cal.setTimeInMillis(rs.getTimestamp(1).getTime());
            int prevDay = cal.get(Calendar.DAY_OF_MONTH);
            int prevMonth = cal.get(Calendar.MONTH);
            int prevYear = cal.get(Calendar.YEAR);

            while (rs.next()) {
                if (prevDay == -1) {

                }

                JsonObject obj = new JsonObject();
                Timestamp timestamp = rs.getTimestamp(1);
                obj.addProperty(rsMetaData.getColumnName(1), timestamp.toString());
                for (int i = 2; i <= columnCount; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    String prettyColumnName = StringUtils.capitalize(columnName.replace("_", " "));
                    obj.addProperty(prettyColumnName, rs.getDouble(columnName));
                }

                cal.setTimeInMillis(timestamp.getTime());

                if (prevDay != cal.get(Calendar.DAY_OF_MONTH)) {
                    aggregateJsonArray(days, hours);
                    String filename = String.format("%d/%d/%d.json", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                            cal.get(Calendar.DAY_OF_MONTH));
                    s3.putObject(bucketName, filename, hours.toString());
                    hours = new JsonArray();
                }

                if (prevMonth != cal.get(Calendar.MONTH)) {
                    aggregateJsonArray(months, days);
                    String filename = String.format("%d/%d.json", cal.get(Calendar.YEAR), cal.get(Calendar.MONTH));
                    s3.putObject(bucketName, filename, days.toString());
                    days = new JsonArray();
                }

                if (prevYear != cal.get(Calendar.YEAR)) {
                    String filename = String.format("%d.json", cal.get(Calendar.YEAR));
                    s3.putObject(bucketName, filename, months.toString());
                    months = new JsonArray();
                }

                prevDay = cal.get(Calendar.DAY_OF_MONTH);
                prevMonth = cal.get(Calendar.MONTH);
                prevYear = cal.get(Calendar.YEAR);

                hours.add(obj);
            }

            stmt.close();
            conn.close();

        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
    }
}
