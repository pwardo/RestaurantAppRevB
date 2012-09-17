/*
 * Assignment 2, Restaurant App
 * Module: HCI and GUI Programming
 * 
 * Student Name: Patrick Ward
 * Student Number: 2761238
 * 
 * Hold down to delete a record
 * Press Menu to get options
 */

package ie.gcd.hci;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class RestaurantDBAdapter {

    public static final String KEY_NAME = "name";
    public static final String KEY_ADDRESS = "address";
    public static final String KEY_PHONE = "phone";
    public static final String KEY_NOTE = "note";
    public static final String KEY_RATING = "rating";
    public static final String KEY_ROWID = "_id";

    private static final String TAG = "RestaurantDBAdapter";
    private DatabaseHelper myDbHelper;
    private SQLiteDatabase mDb;

    /**
     * Database creation sql statement
     */
    private static final String DATABASE_CREATE =
        "create table records (_id integer primary key autoincrement, "
        + "name text not null, address text not null, phone text, "
        + "note text not null, rating float not null);";

    private static final String DATABASE_NAME = "restaurantsDB3";
    private static final String DATABASE_TABLE = "records";
    private static final int DATABASE_VERSION = 2;

    private final Context myContext;

    private static class DatabaseHelper extends SQLiteOpenHelper {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to "
                    + newVersion + ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS records");
            onCreate(db);
        }
    }

    /**
     * Constructor - takes the context to allow the database to be
     * opened/created
     * 
     * @param context the Context within which to work
     */
    public RestaurantDBAdapter(Context context) {
        this.myContext = context;
    }

    /**
     * Open the records database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException if the database could be neither opened or created
     */
    public RestaurantDBAdapter open() throws SQLException {
        myDbHelper = new DatabaseHelper(myContext);
        mDb = myDbHelper.getWritableDatabase();
        return this;
    }

    public void close() {
        myDbHelper.close();
    }


    /**
     * Create a new record using the name, address and phone provided. If the record is
     * successfully created return the new rowId for that record, otherwise return
     * a -1 to indicate failure.
     * 
     * @param name the name of the record
     * @param address the address of the record
     * @param phone the phone of the record
     * @param note the note of the record
     * @return rowId or -1 if failed
     */
    public long createRecord(String name, String address, String phone, 
    		String note, float rating) {
    	
        ContentValues initialValues = new ContentValues();
        initialValues.put(KEY_NAME, name);
        initialValues.put(KEY_ADDRESS, address);
        initialValues.put(KEY_PHONE, phone);
        initialValues.put(KEY_NOTE, note);
        initialValues.put(KEY_RATING, rating);

        return mDb.insert(DATABASE_TABLE, null, initialValues);
    }

    /**
     * Delete the record with the given rowId
     * 
     * @param rowId id of record to delete
     * @return true if deleted, false otherwise
     */
    public boolean deleteRecord(long rowId) {
        return mDb.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all records in the database
     * 
     * @return Cursor over all records
     */
    public Cursor fetchAllRecords() {
        return mDb.query(DATABASE_TABLE, new String[] {KEY_ROWID, KEY_NAME, KEY_ADDRESS, 
        		KEY_PHONE, KEY_NOTE, KEY_RATING}, null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the record that matches the given rowId
     * 
     * @param rowId id of record to retrieve
     * @return Cursor positioned to matching record, if found
     * @throws SQLException if record could not be found/retrieved
     */
    public Cursor fetchRecord(long rowId) throws SQLException {
        Cursor mCursor =
            mDb.query(true, DATABASE_TABLE, new String[] {KEY_ROWID,
                    KEY_NAME, KEY_ADDRESS, KEY_PHONE, KEY_NOTE, KEY_RATING}, KEY_ROWID + "=" + rowId, null,
                    null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the record using the details provided. The record to be updated is
     * specified using the rowId, and it is altered to use the name, address and phone
     * values passed in
     * 
     * @param rowId id of record to update
     * @param name value to set record name to
     * @param address value to set record address to
     * @param phone value to set record phone to
     * @param note value to set record note to
     * @return true if the record was successfully updated, false otherwise
     */
    public boolean updateRecord(long rowId, String name, String address, String phone, String note, float rating) 
    {
        ContentValues args = new ContentValues();
        args.put(KEY_NAME, name);
        args.put(KEY_ADDRESS, address);
        args.put(KEY_PHONE, phone);
        args.put(KEY_NOTE, note);
        args.put(KEY_RATING, rating);

        return mDb.update(DATABASE_TABLE, args, KEY_ROWID + "=" + rowId, null) > 0;
    }
}
