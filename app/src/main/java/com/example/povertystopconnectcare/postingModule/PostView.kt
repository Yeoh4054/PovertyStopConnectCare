package com.example.povertystopconnectcare.postingModule

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.povertystopconnectcare.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView

class PostView : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: PostAdapter? = null

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView

    private lateinit var search: EditText
    private lateinit var btnSearch: ImageView
    private lateinit var btnDrawer: ImageView

    private var postList = ArrayList<Post>()
    private var likeList = ArrayList<Like>()

    private lateinit var sharedPreferences: SharedPreferences // Declare sharedPreferences

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_view)
        // Initialize dbHelper
        dbHelper = DatabaseHelper(this)

        // Initialize sharedPreferences within onCreate
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        bottomNavigationView = findViewById(R.id.bottom_navigation_menu)
        search = findViewById(R.id.editSearch)
        btnSearch = findViewById(R.id.btnSearch)
        btnDrawer = findViewById(R.id.drawer)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)

        btnDrawer.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        // Set up the Navigation Drawer
        val toggle = ActionBarDrawerToggle(
            this,
            drawerLayout,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()


        // Set navigation item listener
        navigationView.setNavigationItemSelectedListener { menuItem ->
            // Handle navigation item clicks here
            when (menuItem.itemId) {
                R.id.homeScreen -> {
                    // Handle item 1 click
                    startActivity(Intent(this, PostView::class.java))
                    true
                }
                R.id.verifyPost -> {
                    // Handle item 1 click
                    startActivity(Intent(this, VerifyPostActivity::class.java))
                    true
                }
                R.id.bookmark-> {
                    // Handle item 2 click
                    startActivity(Intent(this, BookmarkActivity::class.java))
                    true
                }
                // Handle other navigation items similarly
                else -> false
            }
        }

        recyclerView = findViewById(R.id.recyclerView)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Set reverse layout
        layoutManager.stackFromEnd = true // Stack items from the bottom
        recyclerView.layoutManager = layoutManager

        adapter = PostAdapter(this, postList, likeList, dbHelper)
        recyclerView.adapter = adapter

        // Fetch and display posts
        getPost()

        // Set listener for item selection
        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Start HomeActivity
                    startActivity(Intent(this, PostView::class.java))
                    true
                }
                R.id.navigation_addpost -> {
                    // Start DashboardActivity
                    startActivity(Intent(this, PostActivity::class.java))
                    true
                }
                R.id.navigation_profile -> {
                    // Start DashboardActivity
                    startActivity(Intent(this, PostManagementActivity::class.java))
                    true
                }
                else -> false
            }
        }

        btnSearch.setOnClickListener {
            search.visibility = View.VISIBLE // Show the search EditText when the search button is clicked
            btnSearch.visibility = View.GONE

            // Set focus to the search EditText
            search.requestFocus()
        }

        // Detect touch outside of the search EditText to hide it
        val parentLayout = findViewById<View>(R.id.drawer_layout) // Replace with your parent layout ID
        parentLayout.setOnTouchListener { _, _ ->
            hideKeyboard() // Hide keyboard if it's open
            if (search.visibility == View.VISIBLE) {
                search.visibility = View.GONE // Hide the search EditText
                btnSearch.visibility = View.VISIBLE // Show the search icon
                filterPosts("") // Reset the search by passing an empty query
            }
            true
        }


        search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // Not implemented for now
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not implemented for now
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterPosts(s.toString())
            }
        })

        val editor:SharedPreferences.Editor = sharedPreferences.edit()
        editor.putInt("userID", 1)
        editor.apply()


    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search.windowToken, 0)
    }

    private fun getPost() {
        postList = dbHelper.getAllPosts()
        Log.e("pppp", "${postList.size}")

        // Display data in RecyclerView
        adapter?.addPosts(postList)

    }

    private fun filterPosts(query: String) {
        val filteredList = postList.filter { post ->
            val titleMatch = post.title?.contains(query, ignoreCase = true) == true
            val descriptionMatch = post.description?.contains(query, ignoreCase = true) == true
            titleMatch || descriptionMatch
        } as ArrayList<Post>
        adapter?.setFilter(filteredList)
    }
}