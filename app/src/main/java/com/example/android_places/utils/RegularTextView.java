package com.example.android_places.utils;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Typeface;
import android.os.Build;
import android.util.AttributeSet;



public class RegularTextView extends android.widget.TextView {
    public RegularTextView(Context context) {
        super(context);
        setFont();
    }

    public RegularTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setFont();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RegularTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        setFont();
    }

    /**
     * This method is used to set the given font to the TextView.
     */
    private void setFont() {
        Typeface typeface = TypefaceCache.get(getContext().getAssets(), "fonts/Interstate_Regular.TTF");
        setTypeface(typeface);
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }
}
