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

    /** Aggregates the contents of a single JsonArray into a JsonObject
     *
     * @param arr JsonArray that stores the aggregated JsonObject
     * @param agg JsonArray to aggregate into a single JsonObject
     * @param timeLevel Set the Timestamp to the correct format for the aggregation level
     */
    private void aggregateJsonArray(JsonArray arr, JsonArray agg, int timeLevel) {
        JsonObject obj = new JsonObject();
        for (int i = 0; i < agg.size(); i++) {
            for (String key : agg.get(i).getAsJsonObject().keySet()) {

                // Format the Timestamp for the aggregation level
                if (key.equals("fulldatetime")) {
                    switch (timeLevel) {
                        case Calendar.DAY_OF_MONTH:
                            obj.addProperty("fulldatetime", agg.get(i).getAsJsonObject().get(key).getAsString().split(" ")[0]);
                            break;
                        case Calendar.MONTH:
                            obj.addProperty("fulldatetime", agg.get(i).getAsJsonObject().get(key).getAsString().split(" ")[0]);
                            break;
                    }
                    continue;
                }
                
                if (obj.get(key) == null) {
                    obj.addProperty(key, agg.get(i).getAsJsonObject().get(key).getAsInt());
                } else {
                    obj.addProperty(key, obj.get(key).getAsInt() + agg.get(i).getAsJsonObject().get(key).getAsInt());
                }
            }
        }
        arr.add(obj);
    }

    /** AWS Lambda Function that pipes data from PostgreSQL to AWS S3 for the REST API to consume
     *
     * @throws IOException
     */
    public void myHandler() throws IOException {
        Connection conn = null;
        Statement stmt = null;

        // Initialize the Database credentials
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

            int prevDay, prevMonth, prevYear;
            prevDay = prevMonth = prevYear = -1;

            while (rs.next()) {
                if (prevYear == -1) {
                    cal.setTimeInMillis(rs.getTimestamp(1).getTime());
                    prevDay = cal.get(Calendar.DAY_OF_MONTH);
                    prevMonth = cal.get(Calendar.MONTH);
                    prevYear = cal.get(Calendar.YEAR);
                }

                // Read in the ResultSet to a JSON object
                JsonObject obj = new JsonObject();
                Timestamp timestamp = rs.getTimestamp(1);
                cal.setTimeInMillis(timestamp.getTime());

                // Add the Timestamp
                obj.addProperty(rsMetaData.getColumnName(1), timestamp.toString());

                // Add each building's energy total
                for (int i = 2; i <= columnCount; i++) {
                    String columnName = rsMetaData.getColumnName(i);
                    String prettyColumnName = StringUtils.capitalize(columnName.replace("_", " "));
                    obj.addProperty(prettyColumnName, rs.getDouble(columnName));
                }

                // Daily JSON store to S3, Records are by hours
                if (prevDay != cal.get(Calendar.DAY_OF_MONTH)) {
                    aggregateJsonArray(days, hours, Calendar.DAY_OF_MONTH);
                    String filename = String.format("%d/%d/%d.json", prevYear, prevMonth, prevDay);
                    s3.putObject(bucketName, filename, hours.toString());
                    hours = new JsonArray();
                }

                // Monthly JSON store to S3, Records are by days
                if (prevMonth != cal.get(Calendar.MONTH)) {
                    aggregateJsonArray(months, days, Calendar.MONTH);
                    String filename = String.format("%d/%d.json", prevYear, prevMonth);
                    s3.putObject(bucketName, filename, days.toString());
                    days = new JsonArray();
                }

                // Yearly JSON store to S3, Records are by months
                if (prevYear != cal.get(Calendar.YEAR)) {
                    String filename = String.format("%d.json", prevYear);
                    s3.putObject(bucketName, filename, months.toString());
                    months = new JsonArray();
                }

                // Update the previous date
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
