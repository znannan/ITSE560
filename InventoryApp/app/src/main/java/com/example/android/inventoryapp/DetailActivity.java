package com.example.android.inventoryapp;

import android.Manifest;
import android.app.DialogFragment;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NavUtils;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.example.android.inventoryapp.data.InventoryContract.InventoryEntry;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class DetailActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ImageChooserDialogFragment.ImageChooserDialogListener {

    public static final String LOG_TAG = DetailActivity.class.getSimpleName();

    /**
     * Content authority string of {@link FileProvider} for the app.
     */
    public static final String FILE_PROVIDER_AUTHORITY = "com.example.android.fileprovider.camera";

    /**
     * Maximum quantity of one item. This value is also set in activity_detail.xml.
     */
    private static final long MAXIMUM_QUANTITY = 9999999999L;

    /**
     * Highest price digits of one item.
     */
    private static final int HIGHEST_PRICE_DIGITS = 10;

    /**
     * Identifier for the request of external storage permission.
     */
    private static final int PERMISSION_REQUEST_EXTERNAL_STORAGE = 0;

    /**
     * Identifier for the camera {@link Intent}.
     */
    private static final int REQUEST_IMAGE_CAPTURE = 0;

    /**
     * Identifier for the gallery {@link Intent}.
     */
    private static final int REQUEST_IMAGE_SELECT = 1;

    /**
     * Identifier for the item data loader.
     */
    private static final int EXISTING_ITEM_LOADER = 0;

    /**
     * Content URI for the existing item (null if it's a new item).
     */
    private Uri mCurrentItemUri;

    /**
     * File path for the photo that camera took.
     */
    private String mCurrentPhotoPath;

    /**
     * Image URI for the item, it should be updated whenever new image was set.
     */
    private Uri mLatestItemImageUri = null;

    /**
     * Image for the item, extracts from database and should not change at all time.
     */
    private String mCurrentItemImage = null;

    /**
     * Name for the item, extracts from database and should not change at all time.
     */
    private String mCurrentItemName = null;

    /**
     * Remaining number for the item, extracts from database and should not change at all time.
     * Default number is -1 to make it unreachable.
     */
    private long mCurrentItemNumberRemaining = -1;

    /**
     * Sold number for the item, extracts from database and need to update when save.
     * Default number is -1 to make it unreachable.
     */
    private long mCurrentItemNumberSold = -1;

    /**
     * Price for the item, extracts from database and should not change at all time.
     */
    private String mCurrentItemPrice = null;

    /**
     * ImageView field to set the image of the item.
     */
    private ImageView mImageView;

    /**
     * EditText field to enter the name of the item.
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the quantity of the item.
     */
    private EditText mQuantityEditText;

    /**
     * EditText field to enter the price of the item.
     */
    private EditText mPriceEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if users' creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();

        // If the intent DOES NOT contain a content URI, then it's creating a new item.
        if (mCurrentItemUri == null) {
            // Set the title in AppBar to "Add a Item".
            setTitle(R.string.editor_activity_title_new_item);

            // Invalidate the options menu, so onPrepareOptionsMenu method will be called.
            invalidateOptionsMenu();
        } else {
            // Set the title in AppBar to "Edit the Item".
            setTitle(R.string.editor_activity_title_edit_item);

            // Initialize a loader to read the data from the database
            // and display the current values in the editor.
            getLoaderManager().initLoader(EXISTING_ITEM_LOADER, null, this);
        }

        // Find all relevant views.
        mImageView = findViewById(R.id.edit_image);
        mNameEditText = findViewById(R.id.edit_name);
        mQuantityEditText = findViewById(R.id.edit_quantity);
        mPriceEditText = findViewById(R.id.edit_price);

        // Set the decimal numbers to two and maximum price to 9,999,999,999.99 to the price editor.
        mPriceEditText.setFilters(new InputFilter[]{new DigitsInputFilter(HIGHEST_PRICE_DIGITS, 2)});

        // Setup the quantity plus and minus buttons.
        ImageButton plusButton = findViewById(R.id.button_plus_quantity);
        ImageButton minusButton = findViewById(R.id.button_minus_quantity);
        // Add one to the item quantity.
        plusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityString)) {
                    long quantity = Long.valueOf(quantityString);
                    if (quantity < MAXIMUM_QUANTITY) {
                        mQuantityEditText.setText(String.valueOf(quantity + 1));
                    }
                }
            }
        });
        // Minus one to the item quantity.
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String quantityString = mQuantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(quantityString)) {
                    long quantity = Long.valueOf(quantityString);
                    if (quantity > 0) {
                        mQuantityEditText.setText(String.valueOf(quantity - 1));
                    }
                }
            }
        });

        // Setup image chooser.
        View imageContainer = findViewById(R.id.item_image_container);
        imageContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check permission before anything happens.
                if (hasPermissionExternalStorage()) {
                    // Permission has already been granted, then start the dialog fragment.
                    startImageChooserDialogFragment();
                }
            }
        });
    }

    /**
     * Helper method that check whether the app has permission for external storage or not.
     *
     * @return true when the app has the permission, false when the app don't.
     */
    private boolean hasPermissionExternalStorage() {
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            // Permission is NOT granted.
            if (ActivityCompat.shouldShowRequestPermissionRationale(DetailActivity.this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                // Show an explanation with snack bar to user if needed.
                Snackbar snackbar = Snackbar.make(findViewById(R.id.editor_container),
                        R.string.permission_required, Snackbar.LENGTH_LONG);
                // Prompt user a OK button to request permission.
                snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Request the permission.
                        ActivityCompat.requestPermissions(DetailActivity.this,
                                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                PERMISSION_REQUEST_EXTERNAL_STORAGE);
                    }
                });
                snackbar.show();
            } else {
                // Request the permission directly, if it doesn't need to explain.
                ActivityCompat.requestPermissions(DetailActivity.this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        PERMISSION_REQUEST_EXTERNAL_STORAGE);
            }
            return false;
        } else {
            // Permission has already been granted, then return true.
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // For the first time, permission was granted, then start the dialog fragment.
                startImageChooserDialogFragment();
            } else {
                // Prompt to user that permission request was denied.
                Toast.makeText(this, R.string.toast_permission_denied, Toast.LENGTH_SHORT)
                        .show();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Helper method that start the image chooser dialog fragment.
     */
    private void startImageChooserDialogFragment() {
        DialogFragment fragment = new ImageChooserDialogFragment();
        fragment.show(getFragmentManager(), "imageChooser");
    }

    /**
     * Helper method that create a temporary image {@link File} for the camera {@link Intent}.
     */
    private File createCameraImageFile() throws IOException {
        // Create a collision-resistant image file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Give the image file a storage directory, which is a public one.
        // So photos that user captures with the device camera are accessible by all apps.
        File storageDirectory =
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        // Create a temporary file according to the attributes above.
        File imageFile = File.createTempFile(
                imageFileName,      /* prefix    */
                ".jpg",             /* suffix    */
                storageDirectory    /* directory */
        );
        // Save a file: path for use with ACTION_VIEW intents.
        mCurrentPhotoPath = imageFile.getAbsolutePath();

        return imageFile;
    }

    /**
     * Implements {@link ImageChooserDialogFragment.ImageChooserDialogListener} method,
     * handles the image chooser with camera action here.
     */
    @Override
    public void onDialogCameraClick(DialogFragment dialog) {
        // Intent to camera app with URI.
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the image should save at.
            File imageFile = null;
            try {
                imageFile = createCameraImageFile();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error creating the File " + e);
            }
            // Continue only if the File was successfully created.
            if (imageFile != null) {
                Uri imageURI = FileProvider.getUriForFile(this,
                        FILE_PROVIDER_AUTHORITY, imageFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    /**
     * Helper method that create a temporary image {@link File}
     * for copying image which user select from gallery.
     */
    private File createCopyImageFile() throws IOException {
        // Create a collision-resistant image file name.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        // Give the image file a storage directory, which remain private to the app only.
        File storageDirectory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        // Return a temporary file according to the attributes above.
        return File.createTempFile(
                imageFileName,      /* prefix    */
                ".jpg",             /* suffix    */
                storageDirectory    /* directory */
        );
    }

    /**
     * Implements {@link ImageChooserDialogFragment.ImageChooserDialogListener} method,
     * handles the image chooser with gallery action here.
     */
    @Override
    public void onDialogGalleryClick(DialogFragment dialog) {
        // Intent to gallery app.
        Intent selectPictureIntent = new Intent();
        selectPictureIntent.setAction(Intent.ACTION_GET_CONTENT);
        // Filter to show only images, using the image MIME data type.
        selectPictureIntent.setType("image/*");
        if (selectPictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectPictureIntent, REQUEST_IMAGE_SELECT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // If users choose image one more time, delete the previous unwanted file first.
            deleteFile();

            switch (requestCode) {
                case REQUEST_IMAGE_CAPTURE:
                    // Get the image file: URI from file according to the path.
                    mLatestItemImageUri = Uri.fromFile(new File(mCurrentPhotoPath));
                    // Set the image that URI points to using Glide.
                    GlideApp.with(this).load(mLatestItemImageUri)
                            .transforms(new CenterCrop(), new RoundedCorners(
                                    (int) getResources().getDimension(R.dimen.background_corner_radius)))
                            .into(mImageView);
                    // After using the path, set it to null.
                    // So that user return from gallery app but didn't select a picture,
                    // it will not delete a image file.
                    mCurrentPhotoPath = null;
                    break;
                case REQUEST_IMAGE_SELECT:
                    // Get content: URI from intent data.
                    Uri contentUri = intent.getData();
                    // Set the image that URI points to using Glide.
                    GlideApp.with(this).load(contentUri)
                            .transforms(new CenterCrop(), new RoundedCorners(
                                    (int) getResources().getDimension(R.dimen.background_corner_radius)))
                            .into(mImageView);

                    // Copy image file in a background thread.
                    new copyImageFileTask().execute(contentUri);
                    break;
            }
        } else if (mCurrentPhotoPath != null) {
            // When user return from camera app without actually taking a photo,
            // then delete the unused image file.
            File file = new File(mCurrentPhotoPath);
            if (file.delete()) {
                Toast.makeText(this, android.R.string.cancel, Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Helper method that delete the unwanted file.
     */
    private void deleteFile() {
        // If users choose image one more time, delete the previous unwanted file first.
        if (mLatestItemImageUri != null) {
            File file = new File(mLatestItemImageUri.getPath());
            if (file.delete()) {
                Log.v(LOG_TAG, "Previous file deleted.");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new item, hide the "Delete" and "Order" menu item.
        if (mCurrentItemUri == null) {
            MenuItem DeleteMenuItem = menu.findItem(R.id.action_delete);
            MenuItem OrderMenuItem = menu.findItem(R.id.action_order);

            DeleteMenuItem.setVisible(false);
            OrderMenuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_order:
                if (itemHasChanged()) {
                    // If user order before saving their changes, make a toast to inform user.
                    Toast.makeText(this,
                            getString(R.string.toast_save_first), Toast.LENGTH_SHORT).show();
                } else {
                    // Only email apps should handle this intent.
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    intent.setData(Uri.parse("mailto:"));
                    // Put extra subject with the item name from the database.
                    String subject = "Order " + mCurrentItemName;
                    // Put extra attachment with the item image from the database.
                    intent.putExtra(Intent.EXTRA_SUBJECT, subject);
                    // put extra text with the item.
                    StringBuilder text = new StringBuilder(getString(R.string.intent_email_text, mCurrentItemName));
                    text.append(System.getProperty("line.separator"));
                    // If there is image for item, then add it to the email attachment.
                    if (!mCurrentItemImage.equals(InventoryEntry.NO_IMAGE_AVAILABLE_URI.toString())) {
                        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(mCurrentItemImage));
                        text.append(getString(R.string.intent_email_attachment));
                        text.append(System.getProperty("line.separator"));
                    }
                    text.append(getString(R.string.intent_email_thanks));
                    intent.putExtra(Intent.EXTRA_TEXT, text.toString());
                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    }
                }
                return true;
            case R.id.action_save:
                // Call a helper method to perform the save action to database.
                saveItem();
                return true;
            case R.id.action_delete:
                // Pop up confirmation dialog for deletion.
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // If there are NO unsaved changes, navigating up to parent activity, return early.
                if (!itemHasChanged()) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }
                // Otherwise, setup a dialog to warn the user.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // Delete the unwanted file.
                                deleteFile();
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailActivity.this);
                            }
                        };
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Override the BACK button to pop up a warning {@link AlertDialog},
     * when there are unsaved changes.
     */
    @Override
    public void onBackPressed() {
        // If there are NO unsaved changes at all,
        // keep the behavior by default and return early.
        if (!itemHasChanged()) {
            super.onBackPressed();
            return;
        }
        // Otherwise, setup a dialog to warn the user.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Delete the unwanted file.
                        deleteFile();
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes.
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the necessary attributes.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_warning);
        builder.setPositiveButton(R.string.button_discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.button_keep_editing,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Keep editing" button, so dismiss the dialog.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });

        // Create and show the AlertDialog.
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Setup the deletion confirmation {@link AlertDialog} here.
     */
    private void showDeleteConfirmationDialog() {
        // Setup the warning message view.
        TextView warningText = new TextView(this);
        // Make the "Warning" text bold.
        SpannableStringBuilder stringBuilder =
                new SpannableStringBuilder(getString(R.string.deletion_warning));
        stringBuilder.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, 8, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
        warningText.setText(stringBuilder);
        warningText.setTextColor(getResources().getColor(android.R.color.black));
        warningText.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelOffset(R.dimen.dialog_message_text_size));
        warningText.setPadding(
                getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                getResources().getDimensionPixelOffset(R.dimen.dialog_spacing),
                getResources().getDimensionPixelOffset(R.dimen.dialog_spacing));
        warningText.setGravity(Gravity.CENTER_VERTICAL);

        // Get a AlertDialog builder, set the necessary attributes, and show it.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirm_deletion).setView(warningText)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Delete" button, delete the item.
                        deleteItem();
                    }
                })
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Cancel" button, dismiss the dialog.
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                }).create().show();
    }

    /**
     * Helper method that perform the deletion of the item in the database.
     */
    private void deleteItem() {
        // Only perform the delete if this is an existing item.
        if (mCurrentItemUri != null) {
            // Call the ContentResolver to delete the item at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentItemUri
            // content URI already identifies the item.
            int rowsDeleted = getContentResolver().delete(mCurrentItemUri, null, null);

            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText(this, getString(R.string.toast_delete_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_delete_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }

        // Close the activity.
        finish();
    }

    /**
     * Helper method that save item into database.
     */
    private void saveItem() {
        // Read data from input fields.
        String nameString = mNameEditText.getText().toString().trim();
        String quantityString = mQuantityEditText.getText().toString().trim();
        String priceString = mPriceEditText.getText().toString().trim();

        // If there is any empty edit fields, prompt to user and return early.
        if (TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, R.string.toast_complete_edit, Toast.LENGTH_SHORT).show();
            return;
        }

        // Create a ContentValues object that is going to used with database.
        ContentValues values = new ContentValues();

        values.put(InventoryEntry.COLUMN_ITEM_NAME, nameString);
        values.put(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING, Long.valueOf(quantityString));
        values.put(InventoryEntry.COLUMN_ITEM_PRICE, priceString);

        if (mCurrentItemUri == null) {
            // If user didn't provided an image, then set a default image.
            if (mLatestItemImageUri == null) {
                mLatestItemImageUri = InventoryEntry.NO_IMAGE_AVAILABLE_URI;
            }
            values.put(InventoryEntry.COLUMN_ITEM_IMAGE, mLatestItemImageUri.toString());

            // Set the sold number to default 0.
            values.put(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD, 0);

            // Perform insert action to database and get a URI object.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.toast_insert_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_insert_successful),
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            // If user didn't change the image, then DO NOT put it into ContentValues to update.
            if (mLatestItemImageUri != null) {
                values.put(InventoryEntry.COLUMN_ITEM_IMAGE, mLatestItemImageUri.toString());
            }
            // Update sold number first.
            long numberRemaining = Long.valueOf(quantityString);
            if (numberRemaining < mCurrentItemNumberRemaining) {
                // If user decreases remaining number,
                // then add the corresponding number to sold number.
                mCurrentItemNumberSold += mCurrentItemNumberRemaining - numberRemaining;
            }
            values.put(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD, mCurrentItemNumberSold);

            // Perform update action to database and get the affected rows number.
            int rowsAffected = getContentResolver().update(mCurrentItemUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowsAffected == 0) {
                // If no rows were affected, then there was an error with the update.
                Toast.makeText(this, getString(R.string.toast_update_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the update was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.toast_update_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Exit activity.
        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that contains all columns from the inventory table.
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_IMAGE,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING,
                InventoryEntry.COLUMN_ITEM_NUMBER_SOLD,
                InventoryEntry.COLUMN_ITEM_PRICE
        };

        // Return a new CursorLoader object.
        return new CursorLoader(this,   // Parent activity context
                mCurrentItemUri,                // Query the content URI for the current item
                projection,                     // Columns to include in the resulting Cursor
                null,                            // No selection clause
                null,                            // No selection arguments
                null);                           // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than one row in the cursor.
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it.
        if (cursor.moveToFirst()) {
            // Find the necessary columns of item attributes.
            int imageColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_IMAGE);
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NAME);
            int numberRemainingColumnIndex =
                    cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NUMBER_REMAINING);
            int numberSoldColumnIndex =
                    cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_NUMBER_SOLD);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_ITEM_PRICE);

            // Extract out the value from the Cursor for the given column index.
            mCurrentItemImage = cursor.getString(imageColumnIndex);
            mCurrentItemName = cursor.getString(nameColumnIndex);
            mCurrentItemNumberRemaining = cursor.getLong(numberRemainingColumnIndex);
            mCurrentItemNumberSold = cursor.getLong(numberSoldColumnIndex);
            mCurrentItemPrice = cursor.getString(priceColumnIndex);

            // Update the views on the screen with the values from the database.
            mNameEditText.setText(mCurrentItemName);
            mQuantityEditText.setText(String.valueOf(mCurrentItemNumberRemaining));
            mPriceEditText.setText(mCurrentItemPrice);

            GlideApp.with(this).load(mCurrentItemImage)
                    .transforms(new CenterCrop(), new RoundedCorners(
                            (int) getResources().getDimension(R.dimen.background_corner_radius)))
                    .into(mImageView);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidated, clear out all the data.
        mImageView.setImageBitmap(null);
        mNameEditText.setText("");
        mQuantityEditText.setText("");
        mPriceEditText.setText("");

        mCurrentItemImage = null;
        mCurrentItemName = null;
        mCurrentItemNumberRemaining = -1;
        mCurrentItemNumberSold = -1;
        mCurrentItemPrice = null;
    }

    /**
     * Helper method that tells whether there are changes in four editors.
     *
     * @return true if any editors has changes, or false when there are no changes.
     */
    private boolean itemHasChanged() {
        if (mCurrentItemUri == null) {
            return mImageView.getDrawable() != null
                    || !TextUtils.isEmpty(mNameEditText.getText().toString().trim())
                    || !TextUtils.isEmpty(mQuantityEditText.getText().toString().trim())
                    || !TextUtils.isEmpty(mPriceEditText.getText().toString().trim());
        } else {
            return mLatestItemImageUri != null
                    && !mLatestItemImageUri.toString().equals(mCurrentItemImage)
                    || !mNameEditText.getText().toString().equals(mCurrentItemName)
                    || !mQuantityEditText.getText().toString()
                    .equals(String.valueOf(mCurrentItemNumberRemaining))
                    || !mPriceEditText.getText().toString().equals(mCurrentItemPrice);
        }
    }

    /**
     * Inner class which is an {@link AsyncTask} that copy image which user select from gallery
     * in a background thread.
     */
    private class copyImageFileTask extends AsyncTask<Uri, Void, Uri> {
        @Override
        protected Uri doInBackground(Uri... uris) {
            // If URI is null, bail early.
            if (uris[0] == null) {
                return null;
            }

            try {
                // Create the target file for copy using a helper method.
                File file = createCopyImageFile();
                // Load the input stream from the content: URI.
                InputStream input = getContentResolver().openInputStream(uris[0]);
                // Use output stream to write data into the file.
                OutputStream output = new FileOutputStream(file);
                byte[] buffer = new byte[4 * 1024];
                int bytesRead;
                while ((bytesRead = input.read(buffer)) > 0) {
                    output.write(buffer, 0, bytesRead);
                }
                // Close both input and output stream.
                input.close();
                output.close();

                // Return the file: URI.
                return Uri.fromFile(file);
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error creating the File " + e);
            }

            return null;
        }

        @Override
        protected void onPostExecute(Uri uri) {
            if (uri != null) {
                // Set the file: URI.
                mLatestItemImageUri = uri;
            }
        }
    }

    /**
     * Inner class that implements {@link InputFilter},
     * which help {@link EditText} set the input filter.
     */
    private class DigitsInputFilter implements InputFilter {
        /**
         * The number pattern as the input filter.
         */
        private Pattern mPattern;

        /**
         * Constructor of {@link DigitsInputFilter}.
         *
         * @param digitsBeforeDecimalPoint is the maximum number before the decimal point.
         * @param digitsAfterDecimalPoint  is the maximum number after the decimal point.
         */
        private DigitsInputFilter(int digitsBeforeDecimalPoint, int digitsAfterDecimalPoint) {
            // Use RegEx to get a Pattern object.
            mPattern = Pattern.compile(getString(R.string.price_pattern,
                    digitsBeforeDecimalPoint - 1, digitsAfterDecimalPoint));
        }

        /**
         * This method is called when the buffer is going to replace
         * the range (dstart … dend) of dest with the new text
         * from the range (start … end) of source.
         * <p>
         * Return "" to reject any input that is not match the pattern,
         * or return null to accept the original replacement.
         */
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            // Get the input string regardless the input sequence,
            // which means it always get the actual value of the input.
            String inputString = dest.toString().substring(0, dstart)
                    + source.toString().substring(start, end)
                    + dest.toString().substring(dend, dest.toString().length());
            Matcher matcher = mPattern.matcher(inputString);
            if (!matcher.matches()) {
                return "";
            }
            return null;
        }
    }
}

