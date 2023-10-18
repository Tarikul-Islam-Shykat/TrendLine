package com.example.trendline.screens

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuItemCompat
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trendline.R
import com.example.trendline.adapter.ArticleAdapter
import com.example.trendline.db.NewsDatabase
import com.example.trendline.model.Article
import com.example.trendline.repo.NewsRepo
import com.example.trendline.viewModels.NewsVIewModel
import com.example.trendline.viewModels.NewsViewModelFactory
import com.example.trendline.wrapper.Resource
import de.hdodenhof.circleimageview.CircleImageView


class FragmentBreakingNews : Fragment(), ArticleAdapter.ItemClickListener_, MenuProvider {

    lateinit var  viewModel: NewsVIewModel
    lateinit var  newsadapter : ArticleAdapter
    lateinit var rv : RecyclerView
    lateinit var pb : ProgressBar
    private var addingResponselist = arrayListOf<Article>()

    var isClicked: Boolean = false // for fab button check
    var isOpened : Boolean = false // for search view option check


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_breaking_news, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity).supportActionBar?.setTitle("Breaking News")

        // for menu
        val menuHost: MenuHost = requireActivity()
        menuHost.addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.CREATED)
        setHasOptionsMenu(true)


        val sportCat: CircleImageView = view.findViewById(R.id.sportsImage)
        val techCat: CircleImageView = view.findViewById(R.id.techImage)
        val breakingImage: CircleImageView = view.findViewById(R.id.breakingImage)
        val businessCat: CircleImageView = view.findViewById(R.id.businessImage)

        val noWifi : ImageView = view.findViewById(R.id.noWifi)
        val noWifiText : TextView = view.findViewById(R.id.noWifiText)

        val dao = NewsDatabase.getInstance(requireActivity()).newsDao
        val repository =  NewsRepo(dao)
        val factory = NewsViewModelFactory(repository, requireActivity().application)

        viewModel = ViewModelProvider(this, factory)[NewsVIewModel::class.java]


        rv = view.findViewById(R.id.rvBreakingNews)
        pb = view.findViewById(R.id.paginationProgressBar)

        val cm = requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val nInfo = cm.activeNetworkInfo
        if (nInfo !=null && nInfo.isConnected){
            setUpRecyclerView()
            loadBreakingNews()
            isClicked = true

        }
        else{ // if there is now network or wifi.
            noWifi.visibility = View.VISIBLE
            noWifiText.visibility = View.VISIBLE
        }

        val catlistener = View.OnClickListener {
            when(it.id){
                R.id.sportsImage ->{
                    (activity as AppCompatActivity).supportActionBar?.setTitle("Sports")
                    isClicked = true
                    viewModel.getCategory("sports")
                    loadCategoryNews()
                    setUpRecyclerView()

                }
                R.id.techImage ->{
                    (activity as AppCompatActivity).supportActionBar?.setTitle("Tech")
                    isClicked = true
                    viewModel.getCategory("tech")
                    loadCategoryNews()
                    setUpRecyclerView()
                }
                R.id.breakingImage ->{
                    (activity as AppCompatActivity).supportActionBar?.setTitle("Breaking News")
                    isClicked = true
                    loadBreakingNews()
                }
                R.id.businessImage ->{
                    (activity as AppCompatActivity).supportActionBar?.setTitle("Business")
                    isClicked = true
                    viewModel.getCategory("business")
                    loadCategoryNews()
                    setUpRecyclerView()
                }



            }
        }


        techCat.setOnClickListener(catlistener)
        breakingImage.setOnClickListener(catlistener)
        businessCat.setOnClickListener(catlistener)
        sportCat.setOnClickListener(catlistener)


    }

    private fun loadBreakingNews() {
        viewModel.breakingNews.observe(viewLifecycleOwner) { response ->
            when (response) {

               is Resource.Success -> {
                   Log.i("FragmentBreakingNews", "Success")
                   hideProgressBar()
                   response.data?.let { newsRespnse ->
                       addingResponselist = newsRespnse.articles as ArrayList<Article>
                       newsadapter.setList(newsRespnse.articles)
                   }
               }

               is Resource.Error -> {
                   hideProgressBar()
                   response.message?.let { messsage ->
                       Log.i("FragmentBreakingNews", "error2")
                   }
               }

               is Resource.Loading -> {
                   showProgressBar()
               }

           }
       }
    }

    private fun loadCategoryNews() {
        viewModel.categoryNews.observe(viewLifecycleOwner, Observer {response->

            when (response){

                is Resource.Success->{
                    hideProgressBar()
                    response.data?.let{newsresponse->
                        addingResponselist = newsresponse.articles as ArrayList<Article>
                        newsadapter.setList(newsresponse.articles)
                    }
                }

                is Resource.Error->{
                    hideProgressBar()
                    response.message?.let{messsage->
                        Log.i("BREAKING FRAG", messsage.toString())
                    }
                }

                is Resource.Loading->{
                    showProgressBar()
                }
            }
        })
    }

    private fun setUpRecyclerView() {
        newsadapter = ArticleAdapter()
        newsadapter.setItemClickListern(this)
        rv.apply {
            adapter= newsadapter
            layoutManager = LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        }
    }

    fun showProgressBar(){ pb.visibility = View.VISIBLE }

    fun hideProgressBar(){ pb.visibility = View.INVISIBLE }

    override fun onItemClicked(position: Int, ariticle: Article) {
        val action = FragmentBreakingNewsDirections.actionFragmentBreakingNewsToFragmentArticle(ariticle)
        view?.findNavController()?.navigate(action)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

        menuInflater.inflate(R.menu.menu, menu)

        val menuItem = menu.findItem(R.id.searchNews)
       // val searchView = menuItem.actionView as SearchView
        val searchView = menuItem.actionView as androidx.appcompat.widget.SearchView


        val deleteIcon = menu.findItem(R.id.deleteAll)
        deleteIcon.setVisible(false)



        searchView.setOnSearchClickListener{
            val savedIcon = menu.findItem(R.id.savedNewsFrag)
            savedIcon.setVisible(false)
            isOpened = true
        }

        searchView.queryHint = "Search News"

        searchView.setOnCloseListener (androidx.appcompat.widget.SearchView.OnCloseListener{
            isOpened
            val savedIcon = menu.findItem(R.id.savedNewsFrag)
            savedIcon.setVisible(true)
            isOpened.not()
        })

        searchView.setOnQueryTextListener(object :  androidx.appcompat.widget.SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(newText: String?): Boolean { // p0  = newText (video)
                newFiltererdItem(newText)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newFiltererdItem(newText)
                return true
            }



        })

        super.onCreateOptionsMenu(menu,menuInflater) // for showing the menu.


    }

    private fun newFiltererdItem(query: String?) {

        var newFilteredList = arrayListOf<Article>()

        for (i in addingResponselist){
            if(i.title!!.contains( query!!)){
                newFilteredList.add(i)
            }
        }
        newsadapter.filteredList(newFilteredList)

    }

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        if(menuItem.itemId == R.id.savedNewsFrag){
            view?.findNavController()?.navigate(R.id.action_fragmentBreakingNews_to_fragmentSaveNews)
        }
        return true

    }


}