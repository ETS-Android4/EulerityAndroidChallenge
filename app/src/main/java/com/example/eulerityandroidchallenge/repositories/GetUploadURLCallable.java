package com.example.eulerityandroidchallenge.repositories;

import com.example.eulerityandroidchallenge.App;
import com.example.eulerityandroidchallenge.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Callable;

/**
 *      An implementation of Callable which fetches the upload URL asynchronously and returns it to UploadImage
 */

public class GetUploadURLCallable implements Callable<String> {
    @Override
    public String call() {
        String result = "";
        String inputLine;
        try {
            URL url = new URL(App.getResourcesStatic().getString(R.string.upload_url));
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setReadTimeout(15000);
            connection.setConnectTimeout(15000);
            connection.connect();

            InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());

            BufferedReader reader = new BufferedReader(streamReader);

            while ((inputLine = reader.readLine()) != null) {
                result = result.concat(inputLine);
            }
            connection.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String output = "";
        JSONObject obj;
        try {
            obj = new JSONObject(result);
            output = obj.getString("url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return output;
    }
}
