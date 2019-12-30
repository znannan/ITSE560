//package itse560.znn.dailyverse;
//
//import android.content.SharedPreferences;
//import android.os.Bundle;
//import android.preference.Preference;
//import android.preference.PreferenceFragment;
//import android.preference.PreferenceManager;
//
//import androidx.appcompat.app.AppCompatActivity;
//
////import androidx.preference.Preference;
//////import androidx.preference.PreferenceFragmentCompat;
////import android.support.v7.app.AppCompatActivity;
//
//public class SettingsActivity extends AppCompatActivity {
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.settings_activity);
////        getSupportFragmentManager()
////                .beginTransaction()
////                .replace(R.id.settings, new SettingsFragment())
////                .commit();
////        ActionBar actionBar = getSupportActionBar();
////        if (actionBar != null) {
////            actionBar.setDisplayHomeAsUpEnabled(true);
////        }
//    }
//
//    public static class DailyVersePreferenceFragment extends PreferenceFragment {
//        @Override
//        public void onCreate(Bundle savedInstanceState) {
//            super.onCreate(savedInstanceState);
//            addPreferencesFromResource(R.xml.dailyverse_preferences);
//
//            Preference fontSize = findPreference(getString(R.string.settings_font_size_key));
//            bindPreferenceSummaryToValue(fontSize);
//        }
//
//
//        public boolean onPreferenceChange(Preference preference, Object value) {
//            String stringValue = value.toString();
//            preference.setSummary(stringValue);
//            return true;
//        }
//
//        private void bindPreferenceSummaryToValue(Preference preference) {
//            preference.setOnPreferenceChangeListener( this);
//            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());
//            String preferenceString = preferences.getString(preference.getKey(), "");
//            onPreferenceChange(preference, preferenceString);
//        }
//    }
//
//
//}
package itse560.znn.dailyverse;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import androidx.appcompat.app.AppCompatActivity;



public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    public static class DailyVersePreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.dailyverse_preferences);

            Preference fontSize = findPreference(getString(R.string.settings_font_size_key));
            bindPreferenceSummaryToValue(fontSize);

        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        private void bindPreferenceSummaryToValue(Preference preference) {
            preference.setOnPreferenceChangeListener(this);
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(preference.getContext());
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);
        }
    }
}