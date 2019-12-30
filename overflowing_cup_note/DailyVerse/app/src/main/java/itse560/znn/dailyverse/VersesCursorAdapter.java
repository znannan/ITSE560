package itse560.znn.dailyverse;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import itse560.znn.dailyverse.data.VersesContract.VerseEntry;

/**
 * {@link VersesCursorAdapter} is an adapter for a list or grid view
 * that uses a {@link Cursor} of  data as its data source. This adapter knows
 * how to create list items for each row of  data in the {@link Cursor}.
 */
public class VersesCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link VersesCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public VersesCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.verse_list_item, parent, false);
    }

    /**
     * This method binds the verse data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current verse can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView textTextView = (TextView) view.findViewById(R.id.versecontent1);
        TextView refTextView = (TextView) view.findViewById(R.id.versereference1);
        TextView versionTextView = (TextView) view.findViewById(R.id.verseversion1);
        TextView timeTextView = (TextView) view.findViewById(R.id.time1);


        int textColumnIndex = cursor.getColumnIndex(VerseEntry.COLUMN_VERSE_TEXT);
        int refColumnIndex = cursor.getColumnIndex(VerseEntry.COLUMN_VERSE_REF);
        int versionColumnIndex = cursor.getColumnIndex(VerseEntry.COLUMN_VERSE_VERSION);
        int timeColumnIndex = cursor.getColumnIndex(VerseEntry.COLUMN_VERSE_TIME);


        String versText = cursor.getString(textColumnIndex);
        String versRef = cursor.getString(refColumnIndex);
        String verseTime = cursor.getString(timeColumnIndex);
        String versVersion = cursor.getString(versionColumnIndex);



        // Update the TextViews with the attributes for the current verse
        textTextView.setText(versText);
        refTextView.setText(versRef);
        timeTextView.setText(verseTime);
        versionTextView.setText(versVersion);
    }
}
