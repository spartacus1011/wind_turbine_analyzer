package com.example.rossf.audiospectrumanalyzer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

//This is also the datasource
public class DatabaseHelper extends SQLiteOpenHelper {

    //version number to upgrade database version
    //each time if you Add, Edit table, you need to change the
    //version number.
    private static final int DATABASE_VERSION = 9;

    // Database Name
    private static final String DATABASE_NAME = "crud.db";

    public static final String TABLE = "RecordedItemInformation";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //All necessary tables you like to create will create here

        String CREATE_TABLE_STUDENT = "CREATE TABLE " + TABLE + "("
                + RecordedItemInfo.KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT ,"
                + RecordedItemInfo.KEY_UNIQUEID + " TEXT, "
                + RecordedItemInfo.KEY_DATETIME + " TEXT, "
                + RecordedItemInfo.KEY_LOCATION + " TEXT, "
                + RecordedItemInfo.KEY_LOCATIONLAT + " TEXT, "
                + RecordedItemInfo.KEY_LOCATIONLONG + " TEXT, "
                + RecordedItemInfo.KEY_NAME + " TEXT )";

        db.execSQL(CREATE_TABLE_STUDENT);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        if(oldVersion == 7) {
            String ALTER_TABLE_LAT = "ALTER TABLE " + TABLE + " ADD COLUMN " + RecordedItemInfo.KEY_LOCATIONLAT + " TEXT ";
            db.execSQL(ALTER_TABLE_LAT);

            String ALTER_TABLE_LONG = "ALTER TABLE " + TABLE + " ADD COLUMN " + RecordedItemInfo.KEY_LOCATIONLONG + " TEXT ";
            db.execSQL(ALTER_TABLE_LONG);
        }
        else {
            // Dont drop the DB for now, just in case
            // Drop older table if existed, all data will be gone!!!
            //db.execSQL("DROP TABLE IF EXISTS " + TABLE);

            // Create tables again
            //onCreate(db);
        }


    }

    public Integer InsertNew(Integer id,String uniqueid, String date, String locationLat, String locationLong, String name){
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(RecordedItemInfo.KEY_UNIQUEID, uniqueid);
        values.put(RecordedItemInfo.KEY_DATETIME, date);
        //values.put(RecordedItemInfo.KEY_LOCATION, location); depreciated
        values.put(RecordedItemInfo.KEY_LOCATIONLAT, locationLat);
        values.put(RecordedItemInfo.KEY_LOCATIONLONG, locationLong);
        values.put(RecordedItemInfo.KEY_NAME, name);

        // Inserting Row
        long student_Id = db.insert(TABLE, null, values);
        db.close(); // Closing database connection
        return (int) student_Id;
    }

    public List<RecordedItemInfo> GetAllRecords()  {

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery =  "SELECT * " +
                " FROM " + TABLE;

        List<RecordedItemInfo> allRecords = new ArrayList<RecordedItemInfo>();

        Cursor cursor = db.rawQuery(selectQuery, null);
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                RecordedItemInfo recordedItemInfo = GetFullRecordedItemInfo(cursor);

                allRecords.add(recordedItemInfo);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return allRecords;
    }

    //need to check what would be the best way to get the data. For now will use the ID
    //or i can just make multiple methods
    public RecordedItemInfo GetSingleRecordById(int id) {

        SQLiteDatabase db = getReadableDatabase();

        String selectQuery = String.format("SELECT * FROM %1$s WHERE %2$s = @%2$s",TABLE, RecordedItemInfo.KEY_ID);


        Cursor cursor = db.rawQuery(selectQuery, new String[]{Integer.toString(id)});
        cursor.moveToFirst();

        RecordedItemInfo recordedItemInfo = GetFullRecordedItemInfo(cursor);

        cursor.close();
        db.close();

        return recordedItemInfo;
    }

    //DANGEROUS ONLY USE DURING TESTING
    public void DeleteAll(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from " + TABLE);
    }

    private RecordedItemInfo GetFullRecordedItemInfo(Cursor cursor) {

        RecordedItemInfo recordedItemInfo = new RecordedItemInfo();

        recordedItemInfo.ID = cursor.getInt(cursor.getColumnIndex(RecordedItemInfo.KEY_ID));
        recordedItemInfo.UniqueID = cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_UNIQUEID));

        //date time gets a bit messy
        try {
            recordedItemInfo.DateTime = new SimpleDateFormat("dd-MM-yyyy h:mm:ss").parse(cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_DATETIME)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        recordedItemInfo.Location = cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_LOCATION)); //depreciated
        recordedItemInfo.LocationLat = cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_LOCATIONLAT));
        recordedItemInfo.LocationLong = cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_LOCATIONLONG));
        recordedItemInfo.Name = cursor.getString(cursor.getColumnIndex(RecordedItemInfo.KEY_NAME));


        return recordedItemInfo;
    }

}