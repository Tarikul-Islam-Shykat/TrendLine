package com.example.trendline.viewModels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.trendline.db.SavedArticle
import com.example.trendline.model.News
import com.example.trendline.repo.NewsRepo
import com.example.trendline.wrapper.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsVIewModel (val newsRepo: NewsRepo, application: Application) : AndroidViewModel(application){

    // adding news data
    val breakingNews: MutableLiveData<Resource<News>> = MutableLiveData()
    val pageNumber = 1


    // get cateogry news
    val categoryNews : MutableLiveData<Resource<News>> = MutableLiveData()


    // to get saved news
    val getSavedNews = newsRepo.getAllSavedNews()

    init {
        getBreakingNews("us")
    }

    fun getBreakingNews(code: String) = viewModelScope.launch {
        checkInternetandBreakingNews(code)
    }

    fun getCategory(cat: String) = viewModelScope.launch {
        categoryNews.postValue(Resource.Loading())
        val response = newsRepo.getCategoryNews(cat)
        categoryNews.postValue(handleNewsResponse(response))
    }


    fun insertArticle(savedArticle: SavedArticle){
        insertNews(savedArticle)
    }


    fun insertNews(savedArticle : SavedArticle){
        viewModelScope.launch(Dispatchers.IO){
            newsRepo.insertNews(savedArticle)
        }
    }


    fun deleteAllArticle(){
        deleteAll()
    }


    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        newsRepo.deleteAll()
    }



    // get the breaking news
    private suspend fun checkInternetandBreakingNews(code: String){
        breakingNews.postValue(Resource.Loading())

        try{
            if (hasInternetConnection()){
                val response = newsRepo.getBreakingNews(code, pageNumber)
                breakingNews.postValue(handleNewsResponse(response))
            }

            else { breakingNews.postValue(Resource.Error("NO INTERNET CONNECTION")) }

        }
        catch (t: Throwable){
            when (t){
                is IOException -> breakingNews.postValue(Resource.Error("NETWORK FAILER"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }


    }


    private fun handleNewsResponse(response: Response<News>): Resource<News>? {
        if (response.isSuccessful){
            response.body()?.let {
                    resultresponse->
                return Resource.Success(resultresponse)
            }
        }
        return Resource.Error(response.message())
    }


    // check internet connection //without this code our app will crash when there is no wiifi.
    private fun hasInternetConnection(): Boolean {
        val connectivityManager = getApplication<NewsApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val activeNetwork = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            connectivityManager.activeNetworkInfo?.run {
                return when(type) {
                    ConnectivityManager.TYPE_WIFI -> true
                    ConnectivityManager.TYPE_MOBILE -> true
                    ConnectivityManager.TYPE_ETHERNET -> true
                    else -> false
                }
            }
        }
        return false
    }

}