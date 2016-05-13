package com.legitdevs.legitnotes;

/**
 * Created by mattia on 13/05/16.
 */
public class Note {

    private String title;
    private String text;
    private String category;

    public Note (String text) {
        this.text = text;
    }
    public Note (String title, String text) {
        this.title = title;
        this.text = text;
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
}
