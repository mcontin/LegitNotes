package com.legitdevs.legitnotes;

import android.content.Context;

import java.util.HashMap;

/**
 * Created by mattia on 16/05/16.
 */
public class Category {

    private static final String KEY_NAME = "name";
    private static final String KEY_COLOR = "color";

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
    public Category(HashMap<String, Object> categories) {
        name = (String) categories.get(KEY_NAME);
        color = (Integer) categories.get(KEY_COLOR);
    }

    public String getName() {
        return name;
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_NAME, name);
        hashMap.put(KEY_NAME, color);

        return hashMap;
    }
}
