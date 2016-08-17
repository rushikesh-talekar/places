package com.example.android_places.utils;

import android.content.res.AssetManager;
import android.graphics.Typeface;

import java.util.Hashtable;

/**
 * Created by Vishwajit on 9/9/15.
 */
public class TypefaceCache {

    private static final Hashtable<String, Typeface> CACHE = new Hashtable<String, Typeface>();

    public static Typeface get(AssetManager manager, String name) {
        synchronized (CACHE) {

            if (!CACHE.containsKey(name)) {
                Typeface t = Typeface.createFromAsset(manager, name);
                CACHE.put(name, t);
            }
            return CACHE.get(name);
        }
    }

}
