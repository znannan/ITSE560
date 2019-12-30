package itse560.znn.dailyverse;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

//import android.support.v7.app.AppCompatActivity;



public class DailyverseActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<DailyVerse>>,SharedPreferences.OnSharedPreferenceChangeListener {
    /**
     *  loader ID 的常量值。我们可选择任意整数。
     * 仅当使用多个 loader 时该设置才起作用。
     */
    private static final int DAILYVERSES_LOADER_ID = 1;

    public static final String LOG_TAG = DailyverseActivity.class.getName();
    private static final String VERSE_URL = "https://beta.ourmanna.com/api/v1/get/?format=json&order=random";

    /** 列表的adapter */
    private DailyVerseAdapter mAdapter;

    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dailyverse_activity);

        // Create a fake list of verse.
        ArrayList<DailyVerse> verse = new ArrayList<DailyVerse>();
        //new 一个 adapter
        mAdapter = new DailyVerseAdapter(this, verse);


        // Find a reference to the {@link ListView} in the layout
        //只有一条 记录 但还是套用了 adapter + View
        // 后面用loader 防止 旋转屏幕重复获取 经文.
        ListView versView = (ListView) findViewById(R.id.list_home);

        //空列表
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
        versView.setEmptyView(mEmptyStateTextView);

        versView.setAdapter(mAdapter);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();



        // If there is a network connection, fetch data
        if (networkInfo != null && networkInfo.isConnected()) {

            // 引用 LoaderManager，以便与 loader 进行交互。
            LoaderManager loaderManager = getLoaderManager();

            // 初始化 loader。传递上面定义的整数 ID 常量并为为捆绑
            // 传递 null。为 LoaderCallbacks 参数（由于
            // 此活动实现了 LoaderCallbacks 接口而有效）传递此活动。

            loaderManager.initLoader(DAILYVERSES_LOADER_ID, null, this);



        }else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);

            // Update empty state with no connection error message
            mEmptyStateTextView.setText(R.string.no_internet_connection);
        }

// 不用loader 的时候 启动 AsyncTask 以获取数据
//        DailyVerseAsyncTask task = new DailyVerseAsyncTask();
//        task.execute(VERSE_URL);


        }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
        if (key.equals(getString(R.string.settings_font_size_key))){
            // Clear the ListView as a new query will be kicked off
            mAdapter.clear();

            // Hide the empty state text view as the loading indicator will be displayed
            mEmptyStateTextView.setVisibility(View.GONE);

            // Show the loading indicator while new data is being fetched
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.VISIBLE);

            // Restart the loader to requery  as the query settings have been updated
            getLoaderManager().restartLoader(DAILYVERSES_LOADER_ID, null, this);
        }
    }

    @Override
    public Loader<List<DailyVerse>> onCreateLoader(int i, Bundle bundle) {
        // 为给定 URL 创建新 loader
        return new DailyVerseLoader(this, VERSE_URL);
    }

    @Override
    public void onLoadFinished(Loader<List<DailyVerse>> loader, List<DailyVerse> dailyverses) {

        // 因数据已加载，隐藏加载指示符
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        // 将空状态文本设置为显示“未发现。(No dayliverses found.)”
        mEmptyStateTextView.setText(R.string.no_verse);

        // 清除之前数据的适配器
        mAdapter.clear();

        // 如果存在 {@link DailyVerse} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (dailyverses != null && !dailyverses.isEmpty()) {
            mAdapter.addAll(dailyverses);
        }



    }

    @Override
    public void onLoaderReset(Loader<List<DailyVerse>> loader) {
        // 重置 Loader，以便能够清除现有数据。
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

//    private class DailyVerseAsyncTask extends AsyncTask<String, Void, List<DailyVerse>> {
//
//        /**
//         * 此方法在后台线程上运行并执行网络请求。
//         * 我们不能够通过后台线程更新 UI，因此我们返回
//         * {@link DailyVerse} 的列表作为结果。
//         */
//        @Override
//        protected List<DailyVerse> doInBackground(String... urls) {
//            // 如果不存在任何 URL 或第一个 URL 为空，切勿执行请求。
//            if (urls.length < 1 || urls[0] == null) {
//                return null;
//            }
//
//            List<DailyVerse> result = QueryUtils.fetchDailyVerseData(urls[0]);
//            return result;
//        }
//
//        /**
//         * 后台工作完成后，此方法会在主 UI 线程上运行。
//         * 此方法接收 doInBackground() 方法的返回值作为输入。
//         * 首先，我们清理适配器，除去先前 查询的数据。
//         * 然后，我们使用新列表更新适配器，
//         * 这触发 ListView 重新填充其列表项。
//         */
//
//        @Override
//        protected void onPostExecute(List<DailyVerse> data) {
//            // 清除之前数据的适配器
//            mAdapter.clear();
//            // 如果存在 {@link DailyVerse} 的有效列表，则将其添加到适配器的数据集
//            // 触发 ListView 执行更新。
//            if (data != null && !data.isEmpty()) {
//                mAdapter.addAll(data);
//            }
//
//        }
//
//
//    }
}
