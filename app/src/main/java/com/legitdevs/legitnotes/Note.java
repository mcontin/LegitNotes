package com.legitdevs.legitnotes;

import android.util.Log;

import com.legitdevs.legitnotes.database.DatabaseManager;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.net.URI;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by mattia on 13/05/16.
 */

@Table(database = DatabaseManager.class)
public class Note extends BaseModel{

    @PrimaryKey
    long noteID;
    @Column
    private String title;
    @Column
    private String text;
    @Column
    private String category;
    @Column
    private String media;

    public Note() {
//        noteID=getPrimaryKey();
        title = "";
        text = "";
        category = "";
        media = "";
//        save();
    }
    public Note(String title, String text) {
//        noteID=getPrimaryKey();
        this.title = title;
        this.text = text;
        category = "";
        media = "";
//        save();
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

    public String getMedia() {
        return media;
    }
    public void setMedia(String media) {
        this.media = media;
    }


//    public Date date;
//    public long getPrimaryKey(){
//        SimpleDateFormat sdf= new SimpleDateFormat("dd-M-yyyy hh:mm:ss");
//        String dateInString=""+new Date();
//        try {
//            date=sdf.parse(dateInString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return date.getTime();
//    }
}
