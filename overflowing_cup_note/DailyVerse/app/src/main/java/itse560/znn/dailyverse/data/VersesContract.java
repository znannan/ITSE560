package itse560.znn.dailyverse.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;



/**
 * API Contract for the  app.
 */
public final class VersesContract {

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private VersesContract() {}

    public static final String CONTENT_AUTHORITY = "itse560.znn.dailyverse";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_VERSE = "verses";
    /**
     * Inner class that defines constant values for the  database table.
     * Each entry in the table represents a single .
     */
    public static final class VerseEntry implements BaseColumns {
        /** The content URI to access the  data in the provider */
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_VERSE);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list .
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VERSE;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single .
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_VERSE;

        /** Name of database table for Verse */
        public final static String TABLE_NAME = "verses";

        /**
         * Unique ID number for the Verse (only for use in the database table).
         *
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;


        public final static String COLUMN_VERSE_TEXT ="text";


        public final static String COLUMN_VERSE_REF = "reference";


        public final static String COLUMN_VERSE_VERSION = "version";


        public final static String COLUMN_VERSE_TIME = "time";



    }

}


