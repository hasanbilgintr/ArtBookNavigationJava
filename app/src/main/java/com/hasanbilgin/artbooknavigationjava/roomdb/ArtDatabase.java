package com.hasanbilgin.artbooknavigationjava.roomdb;


import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.hasanbilgin.artbooknavigationjava.model.ArtModel;

@Database(entities = {ArtModel.class}, version = 1)
public abstract class ArtDatabase extends RoomDatabase {
    public abstract ArtDao artDao();

}
