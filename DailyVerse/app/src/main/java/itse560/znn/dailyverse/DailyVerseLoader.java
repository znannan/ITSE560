package itse560.znn.dailyverse;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 * 通过使用 AsyncTask 执行
 * 给定 URL 的网络请求，加载地震列表。
 */
public class DailyVerseLoader extends AsyncTaskLoader<List<DailyVerse>> {

    /** 日志消息标签 */
    private static final String LOG_TAG = DailyVerseLoader.class.getName();

    /** 查询 URL */
    private String mUrl;

    /**
     * 构建新 {@link DailyVerseLoader}。
     *
     * 活动的 @param 上下文
     * 要从中加载数据的 @param url
     */
    public DailyVerseLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * 这位于后台线程上。
     */
    @Override
    public List<DailyVerse> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // 执行网络请求、解析响应和提取verse列表。
        List<DailyVerse> dailyVerses = QueryUtils.fetchDailyVerseData(mUrl);
        return dailyVerses;
    }
}