package com.example.ticketapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class DatabaseHelper extends SQLiteOpenHelper {

    // LogCat tag
    private static final String TAG = "DatabaseHelper";

    // Database version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    protected static final String DATABASE_NAME = "TDB.db";

    // Table names
    private static final String TABLE_TICKET = "ticket";
    private static final String TABLE_DELETIONS = "deletions";

    // Table columns
    private static final String COL_ID = "id";
    private static final String COL_PLATE = "plate";
    private static final String COL_STATE = "state";
    private static final String COL_TIMESTAMP = "timestamp";
    private static final String COL_INFRACTION = "infraction";
    private static final String COL_LOCATION = "location";
    private static final String COL_NOTES = "notes";
    private static final String COL_LICENCE_PHOTO = "licence_photo";
    private static final String COL_TICKET_PHOTO = "ticket_photo";
    private static final String COL_TOWED = "is_towed";

    private static final String COL_TYPE = "type";

    private static final String COL_TICKET_ID = "ticket_id";
    private static final String COL_DELETION_TIMESTAMP = "deletion_time";


    // Table create statements
    private static final String CREATE_TABLE_TICKET = "CREATE TABLE " + TABLE_TICKET
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_PLATE + " TEXT, " + COL_STATE + " TEXT, " + COL_TIMESTAMP + " TEXT, "
            + COL_INFRACTION + " TEXT, " + COL_LOCATION + " TEXT, " + COL_NOTES + " TEXT, " + COL_LICENCE_PHOTO + " BLOB, " + COL_TICKET_PHOTO + " BLOB," + COL_TYPE + " INTEGER, " + COL_TOWED + " INTEGER)";
    private static final String CREATE_TABLE_DELETIONS = "CREATE TABLE " + TABLE_DELETIONS
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COL_TICKET_ID + " INTEGER, " + COL_PLATE + " TEXT, " + COL_STATE + " TEXT, "
            + COL_TIMESTAMP + " TEXT, " + COL_INFRACTION + " TEXT, " + COL_LOCATION + " TEXT, " + COL_NOTES + " TEXT, " + COL_TYPE + " INTEGER, " + COL_DELETION_TIMESTAMP + " TEXT)";


    private static final String[] INFRACTIONS = {"OVERNIGHT","FIRE LANE","COMMERCIAL LOADING ZONE","NO PASS/PERMIT", "EXCEEDING TIME INDICATED", "NO PARKING ZONE", "OTHER"};
    private static final String[] LOCATIONS = {"LOT 1","LOT 2","LOT 3","LOT 4","LOT 5","LOT 6","LOT 7","LOT 8","CREEKSIDE","ADMIN/WHISTLER KIDS","BASE 2", "FITZ BUS LOOP","SPRINGS LOOP","DAYLODGE LOOP","OTHER"};
    private static final int[] TICKET_TYPES = {1,2}; //1 = ticket, 2 = exception

    private Context mContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create required table
        db.execSQL(CREATE_TABLE_TICKET);
        db.execSQL(CREATE_TABLE_DELETIONS);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        db.disableWriteAheadLogging();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {

        // On upgrade drop older table
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_TICKET);
        //db.execSQL("DROP TABLE IF EXISTS " + TABLE_DELETIONS);

        // Create new tables
        onCreate(db);
    }

    /*
    // TICKET table gets & sets
    */

    public Cursor getAllTickets() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TICKET + " ORDER BY ID DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getAllDeletions() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_DELETIONS + " ORDER BY ID DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getAllTicketsFromSearch(String searchQuery) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TICKET + " WHERE " + COL_PLATE + " LIKE " + "'" + searchQuery + "%' ORDER BY ID DESC";
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getAllTicketsForPlate(String plate, String state) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TICKET + " WHERE " + COL_PLATE + " = " + plate + " AND " + COL_STATE + " = " + state;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public Cursor getTicketForId(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_TICKET + " WHERE " + COL_ID + " = " + id;
        Cursor data = db.rawQuery(query,null);
        return data;
    }

    public boolean addTicket(String plate, String state, String dateTime, String infraction, String location, String notes, byte[] licencePhoto, byte[] ticketPhoto, int type, int isTowed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PLATE, plate);
        contentValues.put(COL_STATE, state);
        contentValues.put(COL_TIMESTAMP, dateTime);
        contentValues.put(COL_INFRACTION, infraction);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_NOTES, notes);
        contentValues.put(COL_LICENCE_PHOTO, licencePhoto);
        contentValues.put(COL_TICKET_PHOTO, ticketPhoto);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_TOWED, isTowed);

        Log.d(TAG, "addData: Adding ticket for plate " + plate + " to " + TABLE_TICKET + " table");

        long result = db.insert(TABLE_TICKET,null,contentValues);

        //if inserted incorrectly will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public boolean recordDeletion(int ticketId, String plate, String state, String dateTime, String infraction,
                                  String location, String notes, int type, String deletionTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TICKET_ID, ticketId);
        contentValues.put(COL_PLATE, plate);
        contentValues.put(COL_STATE, state);
        contentValues.put(COL_TIMESTAMP, dateTime);
        contentValues.put(COL_INFRACTION, infraction);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_NOTES, notes);
        contentValues.put(COL_TYPE, type);
        contentValues.put(COL_DELETION_TIMESTAMP, deletionTime);

        Log.d(TAG, "addData: Recording deletion for plate " + plate + " to " + TABLE_DELETIONS + " table");

        long result = db.insert(TABLE_DELETIONS,null,contentValues);

        //if inserted incorrectly will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    public void deleteTicket(int tickedId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM " + TABLE_TICKET + " WHERE " + COL_ID + " = " + tickedId;
        db.execSQL(query);
    }

    public int getLicenceCount(String plate, String state) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT COUNT(*) FROM " + TABLE_TICKET + " WHERE " + COL_PLATE + " = " + sqlSafeString(plate) + " AND " + COL_STATE + " = " + sqlSafeString(state);
        int count = -1;

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do{
                count = cursor.getInt(0);

            }while(cursor.moveToNext());
        }
        return count;
    }

    public boolean updateTicket(int ticketId, String plate, String state, String dateTime, String infraction, String location, String notes, byte[] licencePhoto, byte[] ticketPhoto, int isTowed) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_PLATE, plate);
        contentValues.put(COL_STATE, state);
        contentValues.put(COL_TIMESTAMP, dateTime);
        contentValues.put(COL_INFRACTION, infraction);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_NOTES, notes);
        contentValues.put(COL_LICENCE_PHOTO, licencePhoto);
        contentValues.put(COL_TICKET_PHOTO, ticketPhoto);
        contentValues.put(COL_TOWED, isTowed);
        String where = "id='" + ticketId + "'";

        Log.d(TAG, "addData: Updating ticket for plate " + plate + " to " + TABLE_TICKET + " table");

        long result = db.update(TABLE_TICKET,contentValues,where,null);

        //if inserted incorrectly will return -1
        if (result == -1) {
            return false;
        } else {
            return true;
        }
    }

    /*
    // Test methods
    */

    /*
    // Utility methods
    */

    private String sqlSafeString(String data) {
        return "'" + data.trim() + "'";
    }


    public void backup(String outFileName) {

        //database path
        final String inFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Backup Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to backup database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void importDB(String inFileName) {

        final String outFileName = mContext.getDatabasePath(DATABASE_NAME).toString();

        try {

            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();

            Toast.makeText(mContext, "Import Completed", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(mContext, "Unable to import database. Retry", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
