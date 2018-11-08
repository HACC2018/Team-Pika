package com.pika.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

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

        boolean week_level = 7 < end_time.get(Calendar.DAY_OF_YEAR) - start_time.get(Calendar.DAY_OF_YEAR);

        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();

        if(week_level) {
            Map<Integer, JsonObject> map = new HashMap<Integer, JsonObject>();
            for(; start_time.get(Calendar.MONTH) <= end_time.get(Calendar.MONTH); start_time.add(Calendar.MONTH, 1)) {
                String filename = String.format("%d/%d.json", start_time.get(Calendar.YEAR), start_time.get(Calendar.MONTH));
                S3Object item = s3.getObject(bucket_name, filename);
                jsonElement = parser.parse(new InputStreamReader(item.getObjectContent()));
                JsonArray tempArray = jsonElement.getAsJsonArray();

                Calendar tempTime = Calendar.getInstance();

                for(int i = 0; i < tempArray.size(); i++) {
                    JsonObject tempObj = tempArray.get(i).getAsJsonObject();
                    tempTime.setTimeInMillis(Timestamp.valueOf(tempObj.get("fulldatetime").getAsString()).getTime());
                    map.put(tempTime.get(Calendar.DAY_OF_YEAR), tempObj);
                }
            }

            for(; start_time.compareTo(end_time) <= 0; start_time.add(Calendar.DAY_OF_MONTH, 1)) {
                output.add(map.get(start_time.get(Calendar.DAY_OF_YEAR)));
            }
            return output.toString();
        }

        for(; start_time.compareTo(end_time) <= 0; start_time.add(Calendar.DAY_OF_MONTH, 1)) {
            for(; start_time.compareTo(end_time) <= 0; start_time.add(Calendar.DAY_OF_MONTH, 1)) {
                String filename = String.format("%d/%d/%d.json", start_time.get(Calendar.YEAR), start_time.get(Calendar.MONTH),
                        start_time.get(Calendar.DAY_OF_MONTH));
                S3Object item = s3.getObject(bucket_name, filename);
                jsonElement = parser.parse(new InputStreamReader(item.getObjectContent()));
                JsonArray tempArray = jsonElement.getAsJsonArray();
                for(int i = 0; i < tempArray.size(); i++) {
                    output.add(tempArray.get(i));
                }
            }
        }
        return output.toString();
    }
}
