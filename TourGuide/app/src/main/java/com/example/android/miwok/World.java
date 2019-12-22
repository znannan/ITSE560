
package com.example.android.miwok;


public class World {

    private String mName;

    private String mDesc;

    private int mImageResourceId = NO_IMAGE_PROVIDED;

    private static final int NO_IMAGE_PROVIDED = -1;


    public World(String name, String desc) {
        mName = name;
        mDesc = desc;
    }


    public World(String name, String desc, int imageResourceId) {
        mName = name;
        mDesc = desc;
        mImageResourceId = imageResourceId;
    }

    /**
     * Get the default translation of the word.
     */
    public String getmName() {
        return mName;
    }

    /**
     * Get the mDesc  of the world.
     */
    public String getmDesc() {
        return mDesc;
    }

    /**
     * Return the image resource ID of the word.
     */
    public int getImageResourceId() {
        return mImageResourceId;
    }

    /**
     * Returns whether or not there is an image for this word.
     */
    public boolean hasImage() {
        return mImageResourceId != NO_IMAGE_PROVIDED;
    }
}