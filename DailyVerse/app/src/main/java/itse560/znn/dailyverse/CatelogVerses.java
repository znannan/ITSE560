package itse560.znn.dailyverse;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import itse560.znn.dailyverse.data.VersesContract;

//import android.support.design.widget.FloatingActionButton;
//import android.support.v7.app.AppCompatActivity;

/**
 * Displays list of verse that were entered and stored in the app.
 */
public class CatelogVerses extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /** Identifier for the verse data loader */
    private static final int VERSE_LOADER = 0;

    /** Adapter for the ListView */
    VersesCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

//        // Setup FAB to open EditorActivity
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
//                startActivity(intent);
//            }
//        });

        // Find the ListView which will be populated with the verse data
        ListView verseListView = (ListView) findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        verseListView.setEmptyView(emptyView);

        // Setup an Adapter to create a list item for each row of verse data in the Cursor.
        // There is no verse data yet (until the loader finishes) so pass in null for the Cursor.
        mCursorAdapter = new VersesCursorAdapter(this, null);
        verseListView.setAdapter(mCursorAdapter);

        // Setup the item click listener
 //       verseListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                // Create new intent to go to {@link EditorActivity}
//                Intent intent = new Intent(CatelogVerses.this, EditorActivity.class);
//
//                // Form the content URI that represents the specific pet that was clicked on,
//                // by appending the "id" (passed as input to this method) onto the
//                // {@link PetEntry#CONTENT_URI}.
//                // For example, the URI would be "content://com.example.android.pets/pets/2"
//                // if the pet with ID 2 was clicked on.
//                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI, id);
//
//                // Set the URI on the data field of the intent
//                intent.setData(currentPetUri);
//
//                // Launch the {@link EditorActivity} to display the data for the current pet.
//                startActivity(intent);
//            }
 //       });

        // Kick off the loader
        getLoaderManager().initLoader(VERSE_LOADER, null, this);
    }

    /**
     * Helper method to insert hardcoded pet data into the database. For debugging purposes only.
     */
    private void insertVerse() {
        // Create a ContentValues object where column names are the keys,
        // and Toto's verse attributes are the values.
        ContentValues values = new ContentValues();
        values.put(VersesContract.VerseEntry.COLUMN_VERSE_TEXT, "");
        values.put(VersesContract.VerseEntry.COLUMN_VERSE_REF, "");
        values.put(VersesContract.VerseEntry.COLUMN_VERSE_VERSION, VersesContract.VerseEntry.COLUMN_VERSE_VERSION);
        values.put(VersesContract.VerseEntry.COLUMN_VERSE_TIME, "");

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link VersesContract.VerseEntry#CONTENT_URI} to indicate that we want to insert
        // into the verses database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(VersesContract.VerseEntry.CONTENT_URI, values);
    }

    /**
     * Helper method to delete all verse in the database.
     */
    private void deleteAllVerses() {
        int rowsDeleted = getContentResolver().delete(VersesContract.VerseEntry.CONTENT_URI, null, null);
        Log.v("CatalogActivity", rowsDeleted + " rows deleted from verse database");
    }
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu options from the res/menu/menu_catalog.xml file.
//        // This adds menu items to the app bar.
//        getMenuInflater().inflate(R.menu.menu_catalog, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // User clicked on a menu option in the app bar overflow menu
//        switch (item.getItemId()) {
//            // Respond to a click on the "Insert dummy data" menu option
//            case R.id.action_insert_dummy_data:
//                insertPet();
//                return true;
//            // Respond to a click on the "Delete all entries" menu option
//            case R.id.action_delete_all_entries:
//                deleteAllPets();
//                return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Define a projection that specifies the columns from the table we care about.
        String[] projection = {
                VersesContract.VerseEntry._ID,
                VersesContract.VerseEntry.COLUMN_VERSE_TEXT,
                VersesContract.VerseEntry.COLUMN_VERSE_REF,
                VersesContract.VerseEntry.COLUMN_VERSE_VERSION,
                VersesContract.VerseEntry.COLUMN_VERSE_TIME
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                VersesContract.VerseEntry.CONTENT_URI,   // Provider content URI to query
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link VERSECursorAdapter} with this new cursor containing updated VERSE data
        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mCursorAdapter.swapCursor(null);
    }
}