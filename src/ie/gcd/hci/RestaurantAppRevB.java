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

import java.io.PrintWriter;
import java.io.StringWriter;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class RestaurantAppRevB extends ListActivity {
    private static final int ACTIVITY_CREATE=0;
    private static final int ACTIVITY_EDIT=1;

    private static final int INSERT_ID = Menu.FIRST;
    private static final int DELETE_ID = Menu.FIRST + 1;

    private RestaurantDBAdapter myDbHelper;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		try
		{
	        setContentView(R.layout.records_list);
	        myDbHelper = new RestaurantDBAdapter(this);
	        myDbHelper.open();
	        fillData();
	        registerForContextMenu(getListView());
		}
		catch(Exception e)
        {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
        }
    }

    private void fillData() {
        Cursor recordsCursor = myDbHelper.fetchAllRecords();
        startManagingCursor(recordsCursor);

        // Create an array to specify the fields we want to display in the list (only NAME)
        String[] from = new String[]{RestaurantDBAdapter.KEY_NAME};
//        String[] ratingFrom = new String[]{RestaurantDBAdapter.KEY_RATING};

        // and an array of the fields we want to bind those fields to (in this case just text1)
        int[] to = new int[]{R.id.text1};
//        int[] ratingTo = new int[]{R.id.text2};

        // Now create a simple cursor adapter and set it to display
        SimpleCursorAdapter records = 
            new SimpleCursorAdapter(this, R.layout.records_row, recordsCursor, from, to);
        setListAdapter(records);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        menu.add(0, INSERT_ID, 0, R.string.menu_insert);
        return true;
    }
    
    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        switch(item.getItemId()) {
            case INSERT_ID:
                createRecord();
                return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
            ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, R.string.menu_delete);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case DELETE_ID:
                AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
                myDbHelper.deleteRecord(info.id);
                fillData();
                return true;
        }
        return super.onContextItemSelected(item);
    }

    private void createRecord() {
        Intent i = new Intent(this, RestaurantEditRecord.class);
        startActivityForResult(i, ACTIVITY_CREATE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        Intent i = new Intent(this, RestaurantDetailView.class);
        i.putExtra(RestaurantDBAdapter.KEY_ROWID, id);
        startActivityForResult(i, ACTIVITY_EDIT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        fillData();
    }
}
