package com.example.trendline.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.example.trendline.R
import com.example.trendline.db.NewsDatabase
import com.example.trendline.db.SavedArticle
import com.example.trendline.model.Source
import com.example.trendline.repo.NewsRepo
import com.example.trendline.utils.Constants
import com.example.trendline.viewModels.NewsVIewModel
import com.example.trendline.viewModels.NewsViewModelFactory
import com.google.android.material.floatingactionbutton.FloatingActionButton

class FragmentArticle : Fragment() {

    lateinit var  viewModel: NewsVIewModel
    lateinit var args : FragmentArticleArgs

    var stringCheck =""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle("Article News")


        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsVIewModel::class.java]

        args = FragmentArticleArgs.fromBundle(requireArguments()) // getting data from another fragment.

        val fab = view.findViewById<FloatingActionButton>(R.id.fab)

        val textTitle: TextView = view.findViewById(R.id.tvTitle)
        val tSource: TextView = view.findViewById(R.id.tvSource)
        val tDescription: TextView = view.findViewById(R.id.tvDescription)
        val tPubslishedAt: TextView = view.findViewById(R.id.tvPublishedAt)
        val imageView: ImageView = view.findViewById(R.id.articleImage)

        val source = Source(args.article.source!!.id, args.article.source!!.name!!)
        // setting the data
        textTitle.setText(args.article.title)
        tSource.setText(source.name)
        tDescription.setText(args.article.description)
        tPubslishedAt.setText(Constants.DateFormat(args.article.publishedAt))
        Glide.with(requireActivity()).load(args.article.urlToImage).into(imageView)

        // ch
        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer{ // -> checking if the news already in the database.
            for(i in it){
                if(args.article.title == i.title){
                    stringCheck = i.title
                }
            }
        })


        fab.setOnClickListener { //  -> saving the news
            if(args.article.title == stringCheck  ){
                Toast.makeText(context, "Article exists in the saved list", Toast.LENGTH_SHORT).show()
            }
            else
            {
                viewModel.insertArticle(SavedArticle(
                    0,
                    args.article.description!!,
                    args.article.publishedAt!!,
                    source!!,
                    args.article.title!!,
                    args.article.url!!,
                    args.article.urlToImage!!
                ))
                Toast.makeText(context, "Saved Successfully", Toast.LENGTH_SHORT).show()
                view.findNavController().navigate(R.id.action_fragmentArticle_to_fragmentSaveNews)
            }
        }


    }


}