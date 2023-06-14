package com.example.kukaskitaappv2.ui.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.kukaskitaappv2.R
import com.example.kukaskitaappv2.databinding.ActivityMainBinding
import com.example.kukaskitaappv2.source.datastore.UserPreferences
import com.example.kukaskitaappv2.ui.addItem.AddItemActivity
import com.example.kukaskitaappv2.ui.info.InformationActivity
import com.example.kukaskitaappv2.ui.login.LoginActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "myToken")
class MainActivity : AppCompatActivity(){
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var foodAdapter: FoodAdapter
    private val binding: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory.getInstance(
            this,
            UserPreferences.getInstance(dataStore)
        )
    }

    private val mOnNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.scan -> {

                }
                R.id.history -> {

                }
            }
            false
        }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        supportActionBar?.title = "KulkasKita App"

        bottomNavigationView = findViewById(R.id.bottom_navigator)
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener)

        foodAdapter = FoodAdapter()
        binding.rvInventory.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = foodAdapter
        }

        hideSystemUI()

        checkSession()

        binding.fabAddInventory.setOnClickListener {
            val intent = Intent(this, AddItemActivity::class.java)
            startActivity(intent)
        }
    }



    private fun hideSystemUI() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window,
            window.decorView.findViewById(android.R.id.content)).let { controller ->
            controller.hide(WindowInsetsCompat.Type.systemBars())
            // When the screen is swiped up at the bottom
            // of the application, the navigationBar shall
            // appear for some time
            controller.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.option_menu, menu)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.search).actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.queryHint = resources.getString(R.string.hint)
//        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
//            override fun onQueryTextSubmit(query: String): Boolean {
//                showLoading(true)
//                mainViewModel.findUser(query)
//                searchView.clearFocus()
//                return true
//            }
//            override fun onQueryTextChange(newText: String): Boolean {
//                return false
//            }
//        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_logout -> {
                mainViewModel.logout()
            }
            R.id.menu_information -> {
                val intent = Intent(this, InformationActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkSession() {
        mainViewModel.checkToken().observe(this) {
            if (it == "null") {
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }else{
                mainViewModel.getListFood.observe(this) { pagingData ->
                    foodAdapter.submitData(lifecycle, pagingData)
                }
            }
        }
    }


}
