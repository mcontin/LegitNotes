package com.legitdevs.legitnotes;

import android.content.Context;
import android.graphics.Color;
import android.support.v4.content.ContextCompat;

/**
 * Created by mattia on 16/05/16.
 */
public class Category {

    private String name;
    private int color;
    private Context context;

    public Category(Context context) {
        this.context = context;
        name = "";
        color = R.color.white;
    }
    public Category(Context context, String name) {
        this.context = context;
        this.name = name;
        color = R.color.white;
    }
    public Category(Context context, String name, int color) {
        this.context = context;
        this.name = name;
        this.color = color;
    }

}
