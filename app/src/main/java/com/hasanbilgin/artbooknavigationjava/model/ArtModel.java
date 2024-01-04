package com.hasanbilgin.artbooknavigationjava.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ArtModel {

    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String artname;


    @ColumnInfo(name = "artistname")
    public String artistName;

    @ColumnInfo(name = "year")
    public String year;


    @ColumnInfo(name = "image")
    public byte[] image;

    public ArtModel(String artname,  String artistName,  String year,  byte[] image) {
        this.artname = artname;
        this.artistName = artistName;
        this.year = year;
        this.image = image;
    }



}
