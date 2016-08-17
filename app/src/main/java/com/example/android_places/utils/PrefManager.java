package com.example.android_places.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

/**
 * Created by avanin on 16/8/16.
 */
public class PrefManager {
    private static final String PREFERENCES = "Prefs" ;
    private SharedPreferences sharedPreferences;

    public PrefManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCES,Context.MODE_PRIVATE);
    }

    public void addValue(String key,String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void addValue(String key,int value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }

    public void addValue(String key, Set<String> value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(key,value);
        editor.commit();
    }

    public Set<String> getValue(String key) {
        return sharedPreferences.getStringSet(key,null);
    }

}
