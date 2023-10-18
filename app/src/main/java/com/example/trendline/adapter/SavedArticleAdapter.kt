package com.example.trendline.adapter

import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.trendline.R
import com.example.trendline.db.SavedArticle
import com.example.trendline.model.Article
import com.example.trendline.utils.Constants

class SavedArticleAdapter(): RecyclerView.Adapter<SavedArticleAdapter.SavedHolder>() {
    var newsList = listOf<SavedArticle>()
    private  var listener: ArticleAdapter.ItemClickListener_? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SavedHolder {
        Log.d("ArticleAdapter", "onCreateViewHolder")
        val view = LayoutInflater.from(parent.context).inflate(R.layout.sample_news_list, parent, false)
        val viewHolder = SavedHolder(view)
        return  viewHolder
    }


    override fun onBindViewHolder(holder: SavedHolder, position: Int) {
        Log.d("ArticleAdapter", "onBindViewHolder")
        val article = newsList[position]
        val requestOptions = RequestOptions()
        holder.itemView.apply {
            holder.textTitle.setText(article.title)
            holder.tvSource.setText(article.source!!.name)
            holder.tvDescription.setText(article.description)
            holder.tvPubslishedAt.setText(Constants.DateFormat(article.publishedAt))
            // for image : fancy way of showing loading option before news pops up .
            Glide.with(this).load(article.urlToImage).apply(requestOptions).listener(object :
                RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    holder.pb.visibility = View.VISIBLE
                    return  false
                }

                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    holder.pb.visibility = View.INVISIBLE
                    return  false
                }

            }).transition(DrawableTransitionOptions.withCrossFade()).into(holder.imageView)


        }


    }
    fun  setItemClickListern(listener: ArticleAdapter.ItemClickListener_){
        this.listener = listener
    }

    override fun getItemCount(): Int {
        return  newsList.size
    }

    fun setList(articles: List<SavedArticle>) {
        this.newsList = articles
        notifyDataSetChanged()
    }

    class  SavedHolder(itemView: View) : RecyclerView.ViewHolder(itemView){

        val textTitle : TextView = itemView.findViewById(R.id.tvTitle)
        val tvSource : TextView = itemView.findViewById(R.id.tvSource)
        val tvDescription : TextView = itemView.findViewById(R.id.tvDescription)
        val tvPubslishedAt : TextView = itemView.findViewById(R.id.tvPublishedAt)
        val imageView : ImageView = itemView.findViewById(R.id.ivArticleImage)
        val pb : ProgressBar = itemView.findViewById(R.id.pbImage)
    }
}