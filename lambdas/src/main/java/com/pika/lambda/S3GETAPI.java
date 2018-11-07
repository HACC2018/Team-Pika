package com.pika.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class S3GETAPI {

    public static class RequestClass {
        public RequestClass(String starttime, String endtime) {
            this.starttime = starttime;
            this.endtime = endtime;
        }

        public RequestClass() {
        }

        public String getStarttime() {
            return starttime;
        }

        public void setStarttime(String starttime) {
            this.starttime = starttime;
        }

        public String getEndtime() {
            return endtime;
        }

        public void setEndtime(String endtime) {
            this.endtime = endtime;
        }

        String starttime;
        String endtime;
    }

    public String S3GET(RequestClass input) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        Calendar start_time = Calendar.getInstance();
        start_time.setTimeInMillis(df.parse(input.starttime).getTime());
        Calendar end_time = Calendar.getInstance();
        end_time.setTimeInMillis(df.parse(input.endtime).getTime());
        String bucket_name = "teampika-tempbucket";

        JsonParser parser = new JsonParser();
        JsonArray output = new JsonArray();
        JsonElement jsonElement;

        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

        for(; start_time.compareTo(end_time) <= 0; start_time.add(Calendar.HOUR, 1)) {
            String filename = String.format("%d/%d/%d/%d.json", start_time.get(Calendar.YEAR), start_time.get(Calendar.MONTH),
                    start_time.get(Calendar.DAY_OF_MONTH), start_time.get(Calendar.HOUR_OF_DAY));
            S3Object item = s3.getObject(bucket_name, filename);
            long startTime = System.currentTimeMillis();
            jsonElement = parser.parse(new InputStreamReader(item.getObjectContent()));
            output.add(jsonElement);
            long endTime = System.currentTimeMillis();
            long timeElapsed = endTime - startTime;
            System.out.println("Execution time in milliseconds: " + timeElapsed);
        }
        return output.toString();
    }
}
