package com.example.rossf.audiospectrumanalyzer;

import java.util.Date;

public class RecordedItemInfo {

    public Integer ID; //I wanted this to be a GUID but for now a Int will do
    public static final String KEY_ID = "id";
    public String UniqueID;
    public static final String KEY_UNIQUEID = "uniqueid";
    public Date DateTime;
    public static final String KEY_DATETIME = "datetime";
    public String Location;
    public static final String KEY_LOCATION = "location"; //deprciated. Dont use anymore. Keeping as the database already has data for this
    public String LocationLat;
    public static final String KEY_LOCATIONLAT = "locationLat";
    public String LocationLong;
    public static final String KEY_LOCATIONLONG = "locationLong";
    public String Name;
    public static final String KEY_NAME = "name";
}
