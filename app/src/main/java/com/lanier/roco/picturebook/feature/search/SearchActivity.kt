package com.lanier.roco.picturebook.feature.search

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.databinding.DataBindingUtil
import com.lanier.roco.picturebook.R
import com.lanier.roco.picturebook.database.entity.Spirit
import com.lanier.roco.picturebook.databinding.ActivitySearchBinding
import com.lanier.roco.picturebook.ext.launchSafe
import com.lanier.roco.picturebook.ext.toast
import com.lanier.roco.picturebook.feature.main.SpiritAdapter
import com.lanier.roco.picturebook.feature.main.SpiritShowPopup
import com.lanier.roco.picturebook.manager.AppData
import com.lanier.roco.picturebook.widget.rv.EqualDivider
import com.lanier.roco.picturebook.widget.rv.OnItemClickListener
import com.lanier.roco.picturebook.widget.rv.OnLoadMoreListener

class SearchActivity : AppCompatActivity() {

    private val binding : ActivitySearchBinding by lazy {
        DataBindingUtil.setContentView(this, R.layout.activity_search)
    }

    private val viewmodel by viewModels<SearchViewModel>()

    private val mAdapter by lazy {
        SpiritAdapter().apply {
            onItemClickListener = object : OnItemClickListener<Spirit> {
                override fun onItemClick(t: Spirit, position: Int) {
                    SpiritShowPopup.show(spirit = t, supportFragmentManager)
                }
            }

            onLoadMoreListener = object : OnLoadMoreListener {
                override fun onLoadMore() {
                    viewmodel.search(binding.etSearch.text.toString())
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

        binding.recyclerview.adapter = mAdapter
        val divider = ContextCompat.getDrawable(this, R.drawable.equal_divider)
        binding.recyclerview.addItemDecoration(EqualDivider(divider!!, 3))

        initListener()
    }

    private fun initListener() {
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
        binding.etSearch.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val inputText = binding.etSearch.text.toString()
                if (inputText.isEmpty()) return@setOnEditorActionListener true
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                manager.hideSoftInputFromWindow(v.windowToken, 0)

            }
            return@setOnEditorActionListener true
        }

        launchSafe {
            viewmodel.spirits.observe(this@SearchActivity) {
                mAdapter.isEnd = it.third
                if (it.first == 1) {
                    mAdapter.data = it.second
                } else {
                    mAdapter.addData(it.second)
                }
            }
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.menuFilter) {
        }
        return super.onOptionsItemSelected(item)
    }


}