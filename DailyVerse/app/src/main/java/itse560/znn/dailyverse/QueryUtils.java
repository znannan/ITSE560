package itse560.znn.dailyverse;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class QueryUtils {
    //构造方法
    private QueryUtils() {

    }

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();
    /**
     * 从给定字符串 URL 返回新 URL 对象。
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * 向给定 URL 进行 HTTP 请求，并返回字符串作为响应。
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // 如果 URL 为空，则提早返回。
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // 如果请求成功（响应代码 200），
            // 则读取输入流并解析响应。
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the dailyverse JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // 关闭输入流可能会抛出 IOException，这就是
                //  makeHttpRequest(URL url) 方法签名指定可能抛出 IOException 的
                // 原因。
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * 将 {@link InputStream} 转换为包含
     * 来自服务器的整个 JSON 响应的字符串。
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }






    public static List<DailyVerse> extractDailyVerseFromJson(String dailyVerseJson){
        // 如果 JSON 字符串为空或 null，将提早返回。
        if (TextUtils.isEmpty(dailyVerseJson)) {
            return null;
        }

        // 创建一个空 ArrayList
        List<DailyVerse> dailyVerses = new ArrayList<>();

        // 尝试解析 JSON 响应字符串。如果格式化 JSON 的方式存在问题，
        // 则将抛出 JSONException 异常对象。
        // 捕获该异常以便应用不会崩溃，并将错误消息打印到日志中。
        try {

            // 通过 JSON 响应字符串创建 JSONObject
            JSONObject baseJsonResponse = new JSONObject(dailyVerseJson);

            // 提取与名为 "features" 的键关联的 JSONArray，
            // 该键表示特征（或地震）列表。
            JSONObject currentVerse = baseJsonResponse.getJSONObject("verse");

            // For a given verse, extract the JSONObject
            JSONObject currentVerseObj = currentVerse.getJSONObject("details");

            String text = currentVerseObj.getString("text");
            String reference = currentVerseObj.getString("reference");
            String version = currentVerseObj.getString("version");


            // get time in milliseconds
            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();



            // Create a new  object from the JSON response.
            DailyVerse dailyVerse = new DailyVerse(text, reference, version, time);

            // Add the new  to the list of verses.
            dailyVerses.add(dailyVerse);


        }catch (JSONException e){
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        return dailyVerses;
    }

    /**
     * 查询 api网络 数据集并返回  {@link DailyVerse} 对象的列表。
     */
    public static List<DailyVerse> fetchDailyVerseData(String requestUrl) {
        // 创建 URL 对象
        URL url = createUrl(requestUrl);

        // 执行 URL 的 HTTP 请求并接收返回的 JSON 响应
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // 从 JSON 响应提取相关域并创建 列表
        List<DailyVerse> dailyVerses = extractDailyVerseFromJson(jsonResponse);

        // 返回 列表
        return dailyVerses;
    }

}
