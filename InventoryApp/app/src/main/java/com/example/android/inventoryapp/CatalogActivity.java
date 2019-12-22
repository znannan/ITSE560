package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

public class CatalogActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the inventory data loader.
     */
    private static final int INVENTORY_LOADER = 0;

    /**
     * Adapter for the {@link RecyclerView}.
     */
    private InventoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Find a reference to the RecyclerView in the layout.
        RecyclerView recyclerView = findViewById(R.id.list);
        // Set LayoutManager to a TWO columns and VERTICAL list.
        recyclerView.setLayoutManager(
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        // Setup an adapter to create a list item for each row of inventory data in the cursor.
        mAdapter = new InventoryAdapter(this, null);
        recyclerView.setAdapter(mAdapter);

        // Setup the item click listener.
        mAdapter.setOnItemClickListener(new InventoryAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position, long id) {
                // Create new intent to go to DetailActivity.
                Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);

                // Get the content URI with ID of current item.
                Uri currentItemUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, id);
                // Set the URI on the data field of the intent.
                intent.setData(currentItemUri);

                // Launch the DetailActivity to display the data for the current item.
                startActivity(intent);
            }
        });

        // Kick off the loader.
        getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_add) {
            // Open DetailActivity to add a item.
            Intent intent = new Intent(CatalogActivity.this, DetailActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING,
                InventoryEntry.COLUMN_ITEM_NUMBER_SOLD,
                InventoryEntry.COLUMN_ITEM_PRICE};

        // This loader will execute the ContentProvider's query method on a background thread.
        return new CursorLoader(this,   // Parent activity context.
                InventoryEntry.CONTENT_URI,     // Provider content URI to query.
                projection,                     // Columns to include in the resulting Cursor.
                null,                         // No selection clause.
                null,                   // No selection arguments.
                null);                      // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update the adapter with the new cursor containing updated per data.
        mAdapter.swapCursor(data);

        // Find the empty view and set the visibility.
        View emptyView = findViewById(R.id.empty_view);
        if (mAdapter.getItemCount() == 0) {
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted.
        mAdapter.swapCursor(null);
    }
}
