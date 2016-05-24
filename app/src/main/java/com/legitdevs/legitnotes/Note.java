package com.legitdevs.legitnotes;

import android.location.Location;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by mattia on 13/05/16.
 */

public class Note implements Parcelable{

    public static final String KEY_ID = "id";
    public static final String KEY_TITLE = "title";
    public static final String KEY_DATE = "date";
    public static final String KEY_TEXT = "text";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_MEDIA = "media";

    private UUID id;
    private String title;
    private Date date;
    private String text;
    private String category;
    private Location position;
    //la chiave identifica se è immagine, video o audio,
    //il valore è il nome del file
    private HashMap<String, String> medias;

    private String filesDir;

    //TODO id = UUID()
    public Note() {
        id = UUID.randomUUID();
        title = "";
        date = new Date();  //today
        text = "";
        category = "";
        medias = new HashMap<>();
    }
    public Note(String title, String text) {
        id = UUID.randomUUID();
        this.title = title;
        date = new Date();  //today
        this.text = text;
        category = "";
        medias = new HashMap<>();
    }
    public Note(HashMap<String, Object> map){
        id = UUID.fromString(map.get(KEY_ID).toString());
        title = (String) map.get(KEY_TITLE);
        date = new Date((long)map.get(KEY_DATE));
        text = (String) map.get(KEY_TEXT);
        category = (String) map.get(KEY_CATEGORY);
        medias = (HashMap<String, String>) map.get(KEY_MEDIA);
    }

    public UUID getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public Date getDate(){
        return date;
    }
    public void setDate(Date date){
        this.date = date;
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

    public HashMap<String, String> getMedias() {
        return medias;
    }
    public void setMedias(HashMap<String, String> medias) {
        this.medias = medias;
    }

    public void addMedia(String type, File file) {
        medias.put(type, file.getPath());
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location position) {
        this.position = position;
    }

    public HashMap<String, Object> toHashMap(){
        HashMap<String, Object> hashMap = new HashMap<>();

        hashMap.put(KEY_ID, id);
        hashMap.put(KEY_TITLE, title);
        hashMap.put(KEY_DATE, date.getTime());
        hashMap.put(KEY_TEXT, text);
        hashMap.put(KEY_CATEGORY, category);
        hashMap.put(KEY_MEDIA, medias);

        return hashMap;
    }


    //PARCELIZZAZIONE = salvataggio oggetto personalizzato Note nell'Intent

    //boh non so cosa faccia
    @Override
    public int describeContents() {
        return 0;
    }
    //scrivo i dati dell'oggetto nel Parcel di destinazione
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id.toString());
        dest.writeString(title);
        dest.writeLong(date.getTime());
        dest.writeString(text);
        dest.writeString(category);

        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_MEDIA, medias);
        dest.writeBundle(bundle);
    }
    //creo un oggetto Note con la Parcel in arrivo
    public Note(Parcel in){
        id = UUID.fromString(in.readString());
        title = in.readString();
        date = new Date(in.readLong());
        text = in.readString();
        category = in.readString();

        Bundle bundle = in.readBundle();
        medias = (HashMap<String, String>) bundle.getSerializable(KEY_MEDIA);
    }

    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Note createFromParcel(Parcel in) {
            return new Note(in);
        }

        public Note[] newArray(int size) {
            return new Note[size];
        }
    };
}
