package com.legitdevs.legitnotes;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by mattia on 13/05/16.
 */

public class Note {

    private static final String KEY_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_DATE = "date";
    private static final String KEY_TEXT = "text";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_MEDIA = "media";

    private int id;
    private String title;
    private Date date;
    private String text;
    private String category;
    private String media;

    public Note() {
        title = "";
        date = new Date();  //today
        text = "";
        category = "";
        media = "";
        id = hashCode();
    }
    public Note(String title, String text) {
        this.title = title;
        date = new Date();  //today
        this.text = text;
        category = "";
        media = "";
        id = hashCode();
    }

    public int getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }
    public void setText(String text) {
        this.text = text;
    }

    public String getCategory() {
        return category;
    }
    public void setCategory(String category) {
        this.category = category;
    }

    public String getMedia() {
        return media;
    }
    public void setMedia(String media) {
        this.media = media;
    }

    @Override
    public int hashCode() {
        return title.hashCode() + text.hashCode() + category.hashCode();
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_ID, id);
        hashMap.put(KEY_TITLE, title);
        hashMap.put(KEY_DATE, date);
        hashMap.put(KEY_TEXT, text);
        hashMap.put(KEY_CATEGORY, category);
        hashMap.put(KEY_MEDIA, media);

        return hashMap;
    }

}
