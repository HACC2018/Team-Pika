package com.pika.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;

import java.sql.*;


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
            Gson obj = new Gson();
            while(rs.next()) {
                for (int i = 0; i < columnCount; i++) {
                    String column_name = rsMetaData.getColumnName(i);
                    obj.put(column_name, rs.getObject(column_name));

                }
            }
            System.out.println(rs);
            stmt.close();
            conn.close();
        } catch ( Exception e ) {
            System.err.println( e.getClass().getName()+": "+ e.getMessage() );
            System.exit(0);
        }
//        System.out.println("Table created successfully");
    }
}
