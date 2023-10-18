package com.example.trendline.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface NewsDao {

    @Insert
    suspend fun  insertNews(savedArticle: SavedArticle)

    @Query("SELECT * FROM NEWSARTICLE")
    fun getAllNews() : LiveData<List<SavedArticle>>

    @Query("SELECT * FROM NEWSARTICLE")
    fun getNewsById(): LiveData<SavedArticle>

    @Query("DELETE FROM NEWSARTICLE")
    fun deleteAll()

}