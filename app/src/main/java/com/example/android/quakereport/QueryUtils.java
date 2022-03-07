package com.example.android.quakereport;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.text.DecimalFormat;
import java.util.List;

import static com.example.android.quakereport.EarthquakeActivity.LOG_TAG;

public final class QueryUtils {

    private static final String LOCATION_SEPERATOR = " of ";

    private QueryUtils() {
    }

    private static List<Earthquake> extractFeatureFromJSON(String earthquakeJSON) {

        if (TextUtils.isEmpty(earthquakeJSON)){
            return null;
        }
                List<Earthquake> earthquakes = new ArrayList<>();

        try {

            JSONObject baseJSONResponse = new JSONObject(earthquakeJSON);
            JSONArray earthquakeArray = baseJSONResponse.getJSONArray("features");

            for (int i = 0; i < earthquakeArray.length(); i++){
                JSONObject currentEarthquake = earthquakeArray.getJSONObject(i);
                JSONObject properties = currentEarthquake.getJSONObject("properties");

                Double magnitude = properties.getDouble("mag");
                String location = properties.getString("place");
                long time = properties.getLong("time");
                String url = properties.getString("url");

                DecimalFormat decimalFormatter = new DecimalFormat("0.0");
                String magOutput = decimalFormatter.format(magnitude);

                String primaryLocation;
                String locationOffset;

                if (location.contains(LOCATION_SEPERATOR)) {
                    String[] parts = location.split(LOCATION_SEPERATOR);
                    locationOffset = parts[0] + LOCATION_SEPERATOR;
                    primaryLocation = parts[1];
                }
                else {
                    locationOffset = "Near the";
                    primaryLocation = location;
                }

                long timeInMilliseconds = time;
                Date dateObject = new Date(timeInMilliseconds);
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MMM DD, yyyy");
                String dateToDisplay = dateFormatter.format(dateObject);
                SimpleDateFormat timeFormatter = new SimpleDateFormat("HH:mm:ss a");
                String timeToDisplay = timeFormatter.format(dateObject);

                Earthquake earthquake = new Earthquake(magOutput, locationOffset,
                        primaryLocation, dateToDisplay, timeToDisplay, url);
                earthquakes.add(earthquake);
            }

        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }
       // Return the list of earthquakes
        return earthquakes;
    }

    private static URL createURL(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    public static List<Earthquake> fetchEarthquakeData(String requestURL) {
        URL url = createURL(requestURL);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Earthquake> earthquakes = extractFeatureFromJSON(jsonResponse);
        return earthquakes;
    }
}
