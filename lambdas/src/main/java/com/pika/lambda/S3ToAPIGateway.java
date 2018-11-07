package com.pika.lambda;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class S3ToAPIGateway {

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

    public String myHandler(RequestClass input) throws IOException {
        String starttime = input.starttime;
        String endtime = input.endtime;
        String bucket_name = "teampika-tempbucket";
        String filename = "LOL3.json";

        System.out.println(starttime + ' ' + endtime);
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        S3Object item = s3.getObject(bucket_name, filename);
        BufferedReader reader = new BufferedReader(new InputStreamReader(item.getObjectContent()));
        String temp;
        StringBuilder builder = new StringBuilder();
        while((temp = reader.readLine()) != null) {
            builder.append(temp);
        }
        ObjectMapper mapper = new ObjectMapper();
        System.out.println(builder.toString());
        JsonNode json = mapper.readTree(builder.toString());
        System.out.println(json.path("kW_AG_ENGINEERING_MAIN_MTR").asDouble());
        return json.asText();
    }
}
