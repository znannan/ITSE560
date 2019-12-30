package itse560.znn.dailyverse;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class DailyVerseAdapter extends ArrayAdapter<DailyVerse> {
    private Context context;

    public DailyVerseAdapter(Context context, List<DailyVerse> dailyVerses){
        super(context, 0, dailyVerses);
        this.context = context;


    }





    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        //这里由于api只返回一条. 所以使用 dailyverse 的layout
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.dailyverse_list_item, parent, false);
        }

        // Find the verse at the given position in the list of verses
        //实际只有1条
        DailyVerse currentDailyVerse = getItem(position);

        // 找到 显示经文内容的 textView的 ID
        TextView versecontentView = (TextView) listItemView.findViewById(R.id.versecontent);
        // 得到当前经文内容
        String versecontent = currentDailyVerse.getmText();
        // 显示当前经文内容
        versecontentView.setText(versecontent);

        //经文出处
        TextView versereferenceView = (TextView) listItemView.findViewById(R.id.versereference);
        String versereference = currentDailyVerse.getmReference();
        versereferenceView.setText(versereference);

        //圣经版本
        TextView verseversionView = (TextView) listItemView.findViewById(R.id.verseversion);
        String verseversion = currentDailyVerse.getmVersion();
        verseversionView.setText(verseversion);


//        float fontSize = currentDailyVerse.getFontSize();


        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this.context);
        String fontSize = sharedPrefs.getString(this.context.getResources().getString(R.string.settings_font_size_key),this.context.getResources().getString(R.string.settings_font_size_default));

        //String fontSize = context.getResources().getString(R.string.settings_font_size_default);
        //Log.v(LOG_TAG+"hahaha",fontSize);
        versecontentView.setTextSize(Float.parseFloat(String.valueOf(fontSize)));
        versereferenceView.setTextSize(Float.parseFloat(String.valueOf(fontSize)));
        verseversionView.setTextSize(Float.parseFloat(String.valueOf(fontSize)));



        return listItemView;


    }


}
