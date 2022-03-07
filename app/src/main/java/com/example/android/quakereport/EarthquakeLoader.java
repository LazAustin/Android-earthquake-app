package com.example.android.quakereport;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class EarthquakeLoader extends AsyncTaskLoader<List<Earthquake>> {
    private static final String LOG_TAG = EarthquakeLoader.class.getName();
    private String mURL;
    public EarthquakeLoader(Context context, String url){
        super(context);
        mURL = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Earthquake> loadInBackground() {
        if (mURL == null){
            return null;
        }
        List<Earthquake> earthquakes = QueryUtils.fetchEarthquakeData(mURL);
        return earthquakes;
    }
}