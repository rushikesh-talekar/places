package com.example.android_places.utils;

import android.graphics.Bitmap;

/**
 * Created by avanin on 17/8/16.
 */
public class AttributedPhoto {

    public final CharSequence attribution;

    public final Bitmap bitmap;

    public AttributedPhoto(CharSequence attribution, Bitmap bitmap) {
        this.attribution = attribution;
        this.bitmap = bitmap;
    }
}
