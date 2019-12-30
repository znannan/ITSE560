package itse560.znn.dailyverse;

public class DailyVerse {
    private String mText;
    private String mReference;
    private String mVersion;

    private long mTimeInMilliseconds;
    private float mFontSize;

    public DailyVerse(String text, String reference, String version, long timeInMilliseconds){

        mText = text;
        mReference = reference;
        mVersion = version;
        mTimeInMilliseconds = timeInMilliseconds;
    }


    public DailyVerse(String text, String reference, String version, long timeInMilliseconds, float fontSize){

        mText = text;
        mReference = reference;
        mVersion = version;
        mFontSize = fontSize;
        mTimeInMilliseconds = timeInMilliseconds;
    }

    public String getmText() {
        return mText;
    }

    public String getmReference() {
        return mReference;
    }

    public String getmVersion() {
        return mVersion;
    }

    public long getmTimeInMilliseconds() {
        return mTimeInMilliseconds;
    }

    public float getFontSize(){return mFontSize;};

}
