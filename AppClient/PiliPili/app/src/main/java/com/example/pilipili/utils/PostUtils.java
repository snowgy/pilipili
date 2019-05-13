package com.example.pilipili.utils;

import android.util.Log;

import org.json.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

/** An http client to do post */
public class PostUtils extends Thread {
    private static final String TAG = "PostUtils";
    String requestURL;
    HashMap<String, String> postDataParams;
    org.json.simple.JSONObject res;

    /**
     * create a PostUtils
     * @param requestURL api url
     * @param postDataParams parameters in HashMap
     */
    public PostUtils(String requestURL, HashMap<String, String> postDataParams){
        this.requestURL = requestURL;
        this.postDataParams = postDataParams;
    }

    /**
     * Thread run behavior
     */
    public void run(){
        doPost(requestURL, postDataParams);
    }

    /**
     * do post to remote server
     * @param requestURL api url
     * @param postDataParams parameters in HashMap
     */
    public void doPost(String requestURL,
                                    HashMap<String, String> postDataParams) {
        URL url;
        String response = "";
        StringBuffer responseBuffer = new StringBuffer();
        try{
            url = new URL(requestURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("Content-Type", "application/json");
            JSONObject root = new JSONObject();
            root.put("userName", postDataParams.get("userName"));
            root.put("password", postDataParams.get("password"));
            String str = root.toString();
            Log.e(TAG, str);
            byte[] outputBytes = str.getBytes("UTF-8");
            OutputStream os = conn.getOutputStream();
            os.write(outputBytes);
            int responseCode = conn.getResponseCode();
            Log.e(TAG, responseCode+"");
            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        conn.getInputStream(), "UTF-8"));
                while ((line = br.readLine()) != null) {
                    // response += line;
                    responseBuffer.append(line);
                }
                br.close();
                response = responseBuffer.toString();
                Log.e(TAG, response);
                setJson((org.json.simple.JSONObject)(new JSONParser().parse(response)));
            } else {
                response = "";
                setJson(null);
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * set request url
     * @param requestURL request url
     */
    public void setRequestURL(String requestURL) {
        this.requestURL = requestURL;
    }

    /**
     * set post data
     * @param postDataParams post data
     */
    public void setPostDataParams(HashMap<String, String> postDataParams) {
        this.postDataParams = postDataParams;
    }

    /**
     * Get the return json object.
     * @return return json object.
     */
    public org.json.simple.JSONObject getJson() {
        return res;
    }

    /**
     * Set the return json Object.
     * @param json json object
     */
    public void setJson(org.json.simple.JSONObject json) {
        res = json;
    }
}
