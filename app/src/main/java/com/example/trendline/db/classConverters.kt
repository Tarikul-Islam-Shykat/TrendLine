package com.example.trendline.db

import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.example.trendline.model.Source

class classConverters {

    @TypeConverter
    fun fromSource(source: Source):String{
        return  source.name!!
    }

    @TypeConverter
    fun toSource(name: String): Source{
        return Source(name, name)
    }
}