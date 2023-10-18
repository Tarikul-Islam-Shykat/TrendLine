package com.example.trendline.viewModels

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.trendline.repo.NewsRepo

class NewsViewModelFactory (val newsRepo: NewsRepo, val application: Application) : ViewModelProvider.Factory {


    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(NewsVIewModel::class.java)) {
            return NewsVIewModel(newsRepo, application as NewsApplication) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}