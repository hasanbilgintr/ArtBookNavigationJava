package com.hasanbilgin.artbooknavigationjava.roomdb;


import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.hasanbilgin.artbooknavigationjava.model.ArtModel;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ArtDao {

    //ArtModel modelin sınıf ismidir
    @Query("SELECT * FROM ArtModel")
    Flowable<List<ArtModel>> getArtWithNameAndId();

    @Query("SELECT * FROM ArtModel WHERE id = :id")
    Flowable<ArtModel> getArtById(int id);

    @Insert
    Completable insert(ArtModel art);

    @Delete
    Completable delete(ArtModel art);




}
