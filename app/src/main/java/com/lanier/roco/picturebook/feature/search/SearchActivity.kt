package com.lanier.roco.picturebook.feature.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Skill
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.ActivitySearchBinding
import com.lanier.roco.picturebook.feature.main.SkillAdapter
import com.lanier.roco.picturebook.feature.main.SpiritAdapter
import com.lanier.roco.picturebook.feature.main.SpiritShowPopup
import com.lanier.roco.picturebook.feature.search.entity.SearchDataType
import com.lanier.roco.picturebook.feature.search.fragment.SearchOptFragment
import com.lanier.roco.picturebook.feature.search.fragment.SearchSpiritOptFragment
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

class SearchActivity : AppCompatActivity() {

    private val binding : ActivitySearchBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_search)
    }

    private val viewmodel by viewModels<SearchViewModel>()
    private val searchDataType: SearchDataType by lazy {
        val type = intent.getIntExtra("searchType", 1)
        when (type) {
            1 -> SearchDataType.Spirit
            2 -> SearchDataType.Skill
            else -> SearchDataType.Spirit
        }
    }

    private val spiritAdapter by lazy {
        SpiritAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Spirit> {
                override fun onItemClick(t: Spirit, position: Int) {
                    SpiritShowPopup.show(spirit = t, supportFragmentManager)
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.loadMore()
                }
            }
        }
    }
    
    private val skillAdapter by lazy { 
        SkillAdapter().apply { 
            onItemClickListener = object : OnItemClickListener<Skill> {
                override fun onItemClick(t: Skill, position: Int) {
                }
            }
            
            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.loadMore()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setSupportActionBar(binding.toolbar)

        viewmodel.searchDataType = searchDataType
        
        when (searchDataType) {
            SearchDataType.Skill -> {
                binding.recyclerview.adapter = skillAdapter
                binding.recyclerview.layoutManager = LinearLayoutManager(this)
                binding.recyclerview.addItemDecoration(
                    DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
                )
            }
            SearchDataType.Spirit -> {
                binding.recyclerview.adapter = spiritAdapter
                val divider = ContextCompat.getDrawable(this, R.drawable.equal_divider)
                binding.recyclerview.addItemDecoration(EqualDivider(divider!!, 3))
            }
        }

        val searchFragment = SearchOptFragment.newInstance(searchDataType)
        searchFragment.onResearchListener = object : ISearchAction {
            override fun onResearch(type: SearchDataType) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
            }
        }
        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.flSearchOpt, searchFragment, SearchSpiritOptFragment::class.java.simpleName)
        transaction.commit()

        initListener()
    }

    private fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                search()
            }
            return@setOnEditorActionListener true
        }
        
        viewmodel.datas.observe(this) {
            when (searchDataType) {
                SearchDataType.Skill -> {
                    skillAdapter.isEnd = it.third
                    val data = it.second as List<Skill>
                    if (it.first == 1) {
                        skillAdapter.data = data
                    } else {
                        skillAdapter.addData(data)
                    }
                }
                SearchDataType.Spirit -> {
                    spiritAdapter.isEnd = it.third
                    val data = it.second as List<Spirit>
                    if (it.first == 1) {
                        spiritAdapter.data = data
                    } else {
                        spiritAdapter.addData(data)
                    }
                }
                null -> {}
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (binding.drawerLayout.isDrawerOpen(GravityCompat.END)) {
                binding.drawerLayout.closeDrawer(GravityCompat.END)
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuFilter) {
            binding.drawerLayout.openDrawer(GravityCompat.END)
        }
        return super.onOptionsItemSelected(item)
    }

    private fun search() {
        val inputText = binding.etSearch.text.toString()
        val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        manager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
        viewmodel.search(inputText)
    }
}