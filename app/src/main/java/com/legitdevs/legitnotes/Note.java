package com.legitdevs.legitnotes;

import com.legitdevs.legitnotes.database.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.net.URI;

/**
 * Created by mattia on 13/05/16.
 */

@Table(database = DatabaseManager.class)
public class Note extends BaseModel{

    @PrimaryKey
    int noteID;
    @Column
    private String title;
    @Column
    private String text;
    @Column
    private String category;
    @Column
    private String media;

    public Note (String text) {
        //TODO noteID = generato in base alla data in millisecondi
        title = "";
        this.text = text;
        category = "";
        media = "";
        save();
    }
    public Note (String title, String text) {
        //TODO noteID = generate in base alla data in millisecondi
        this.title = title;
        this.text = text;
        category = "";
        media = "";
        save();
    }

    public void setAttachment() {

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
