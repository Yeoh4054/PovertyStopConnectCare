package com.example.povertystopconnectcare.postingModule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.povertystopconnectcare.R
import com.google.android.material.internal.ViewUtils.hideKeyboard

class PostManagementActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: PostManagementAdapter? = null
    private lateinit var btnBack : ImageView
    private var postList = ArrayList<Post>()

    private lateinit var search: EditText
    private lateinit var btnSearch: ImageView

    private lateinit var sharedPreferences: SharedPreferences // Declare sharedPreferences

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_management)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        // Initialize dbHelper
        dbHelper = DatabaseHelper(this)

        recyclerView = findViewById(R.id.recyclerViewManagement)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Set reverse layout
        layoutManager.stackFromEnd = true // Stack items from the bottom
        recyclerView.layoutManager = layoutManager
        adapter = PostManagementAdapter(this, postList, dbHelper)
        recyclerView.adapter = adapter

        btnBack = findViewById(R.id.backBtn)
        search = findViewById(R.id.editSearch)
        btnSearch = findViewById(R.id.btnSearch)


        btnBack.setOnClickListener {
            val intent = Intent(this,PostView::class.java )
            startActivity(intent)
        }

        btnSearch.setOnClickListener {
            search.visibility = View.VISIBLE // Show the search EditText when the search button is clicked
            btnSearch.visibility = View.GONE

            // Set focus to the search EditText
            search.requestFocus()
        }

        // Detect touch outside of the search EditText to hide it
        val parentLayout = findViewById<View>(R.id.managementLayout) // Replace with your parent layout ID
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

        // Fetch and display posts for a specific user ID
        val savedValue = sharedPreferences.getInt("userID",0)
        postList = dbHelper.getPostsForUser(savedValue)
        Log.e("pppp", "${postList.size}")

        // Display data in RecyclerView
        adapter?.addPosts(postList)

    }

    private fun hideKeyboard() {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(search.windowToken, 0)
    }

    private fun filterPosts(query: String) {
        val filteredList = postList.filter { post ->
            val titleMatch = post.title?.contains(query, ignoreCase = true) == true
            val descriptionMatch = post.description?.contains(query, ignoreCase = true) == true
            titleMatch || descriptionMatch
        } as ArrayList<Post>

        adapter?.setFilter(filteredList) // Ensure adapter gets the filtered list
    }

}