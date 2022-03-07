package com.example.android.quakereport;

import android.location.Location;

public class Earthquake {
    private String mMagnitude;
    private String mLocationOffset;
    private String mPrimaryLocation;
    private String mDate;
    private String mTime;
    private String mURL;

    public Earthquake(String Magnitude, String LocationOffset, String PrimaryLocation, String Date,
                      String Time, String URL) {
        mMagnitude = Magnitude;
        mLocationOffset = LocationOffset;
        mPrimaryLocation = PrimaryLocation;
        mDate = Date;
        mTime = Time;
        mURL = URL;
    }

    public String getMagnitude() {
        return mMagnitude;
    }

    public String getLocationOffset() {
        return mLocationOffset;
    }

    public String getPrimaryLocation(){
        return mPrimaryLocation;
    }

    public String getDate() {
        return mDate;
    }

    public String getTime(){
        return mTime;
    }

    public String getURL() {return mURL;}

}
