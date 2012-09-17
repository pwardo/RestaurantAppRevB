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

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RatingBar;
import android.widget.TextView;

public class RestaurantDetailView extends Activity {

    private TextView nameText;
    private TextView addressText;
    private TextView phoneText;
    private TextView noteText;

    private RatingBar ratingbarValue;
    private Long mRowId;
    private RestaurantDBAdapter myDbHelper;
    private static final int ACTIVITY_EDIT=1;
    private static final int UPDATE_ID = Menu.FIRST;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myDbHelper = new RestaurantDBAdapter(this);
        myDbHelper.open();

        setContentView(R.layout.record_detail);
        setTitle(R.string.detail_record);

        nameText = (TextView) findViewById(R.id.name);
        addressText = (TextView) findViewById(R.id.address);
        phoneText = (TextView) findViewById(R.id.phone);
        noteText = (TextView) findViewById(R.id.note);

        ratingbarValue = (RatingBar) findViewById(R.id.ratingbar);

        mRowId = (savedInstanceState == null) ? null :
            (Long) savedInstanceState.getSerializable(RestaurantDBAdapter.KEY_ROWID);
		if (mRowId == null) {
			Bundle extras = getIntent().getExtras();
			mRowId = extras != null ? extras.getLong(RestaurantDBAdapter.KEY_ROWID)
									: null;
		}

		
		populateFields();
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor record = myDbHelper.fetchRecord(mRowId);
            startManagingCursor(record);
            nameText.setText(record.getString(
                    record.getColumnIndexOrThrow(RestaurantDBAdapter.KEY_NAME)));
            addressText.setText(record.getString(
                    record.getColumnIndexOrThrow(RestaurantDBAdapter.KEY_ADDRESS))); 
            phoneText.setText(record.getString(
                    record.getColumnIndexOrThrow(RestaurantDBAdapter.KEY_PHONE)));  
            noteText.setText(record.getString(
                            record.getColumnIndexOrThrow(RestaurantDBAdapter.KEY_NOTE)));
            ratingbarValue.setRating(record.getFloat(
    				record.getColumnIndexOrThrow(RestaurantDBAdapter.KEY_RATING)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, UPDATE_ID, 0, R.string.menu_update);
        return true;
    }
    

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case UPDATE_ID:
                updateRecord(mRowId);
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    private void updateRecord(long id) {
        Intent i = new Intent(this, RestaurantEditRecord.class);
        i.putExtra(RestaurantDBAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(RestaurantDBAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String name = nameText.getText().toString();
        String address = addressText.getText().toString();
        String phone = phoneText.getText().toString();
        String note = noteText.getText().toString();
        float rating = ratingbarValue.getRating();


        if (mRowId == null) {
            long id = myDbHelper.createRecord(name, address, phone, note, rating);
            if (id > 0) {
                mRowId = id;
            }
        } else {
            myDbHelper.updateRecord(mRowId, name, address, phone, note, rating);
        }
    }
}