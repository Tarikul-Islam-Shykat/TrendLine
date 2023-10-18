package com.example.trendline.screens

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trendline.R
import com.example.trendline.adapter.ArticleAdapter
import com.example.trendline.adapter.SavedArticleAdapter
import com.example.trendline.db.NewsDatabase
import com.example.trendline.db.SavedArticle
import com.example.trendline.repo.NewsRepo
import com.example.trendline.viewModels.NewsVIewModel
import com.example.trendline.viewModels.NewsViewModelFactory

class FragmentSaveNews : Fragment(), MenuProvider {

    lateinit var  viewModel: NewsVIewModel
    lateinit var  newsadapter : SavedArticleAdapter
    lateinit var rv: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_save_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.setTitle("Saved News")

        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)
        setHasOptionsMenu(true)


        rv = view.findViewById(R.id.rvSavedNews)

        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository = NewsRepo(dao)
        val factory = NewsViewModelFactory(repository, requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[NewsVIewModel::class.java]
        newsadapter = SavedArticleAdapter()



        viewModel.getSavedNews.observe(viewLifecycleOwner, Observer{

            newsadapter.setList(it)
            setUpRecyclerView()
        })

    }

    private fun setUpRecyclerView() {

        rv.apply {
            adapter= newsadapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.menu, menu)

        val searchIcon = menu.findItem(R.id.searchNews)
        val savedIcon = menu.findItem(R.id.savedNewsFrag)

        searchIcon.setVisible(false)
        savedIcon.setVisible(false)

        super.onCreateOptionsMenu(menu, menuInflater)
    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if (menuItem.itemId == R.id.deleteAll){

            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Delete Menu")
            builder.setMessage("Are you sure to delete All saved articles")

            builder.setPositiveButton("Delete All"){dialog, which->
                viewModel.deleteAll()
                Toast.makeText(context, "Deleted All", Toast.LENGTH_SHORT).show()
                view?.findNavController()?.navigate(R.id.action_fragmentSaveNews_to_fragmentBreakingNews)
            }

            builder.setNegativeButton("Cancel"){dialog, which->
                dialog.dismiss()
            }

            val dialog = builder.create()
            dialog.show()
        }
        return true
    }


}