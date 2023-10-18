package com.example.trendline.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters


@Database(entities =  [SavedArticle::class], version = 1)
@TypeConverters(classConverters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract val newsDao : NewsDao

    companion object{
        @Volatile
        private var INSTANCE : NewsDatabase? = null
        fun getInstance(context: Context): NewsDatabase{

            synchronized(this){
                var instance = INSTANCE
                if(instance == null){
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDatabase::class.java,
                        "news_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
        }

        }

    }

}