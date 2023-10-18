package com.example.trendline.repo

import androidx.lifecycle.LiveData
import com.example.trendline.db.NewsDao
import com.example.trendline.db.SavedArticle
import com.example.trendline.service.RetrofitInstance
import retrofit2.Retrofit

class NewsRepo(val newsDao: NewsDao) {

    // insert
    suspend fun insertNews(savedArticle: SavedArticle){
        newsDao.insertNews(savedArticle)
    }

    // get all saved news
    fun getAllSavedNews(): LiveData<List<SavedArticle>>{
        return newsDao.getAllNews()
    }

    // get news by id
    fun getNewsById(): LiveData<SavedArticle>{
        return newsDao.getNewsById()
    }

    // getting breakup news
    suspend fun getBreakingNews( code: String, pageNumber: Int) = RetrofitInstance.api.getBreakingNews(code, pageNumber)

    // getting category news
    suspend fun getCategoryNews(code: String) = RetrofitInstance.api.getByCategory(code)

    // delete news
    fun deleteAll(){
        newsDao.deleteAll()
    }

}