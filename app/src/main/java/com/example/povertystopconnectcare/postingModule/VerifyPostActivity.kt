package com.example.povertystopconnectcare.postingModule

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.povertystopconnectcare.R

class VerifyPostActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private var adapter: VerifyPostAdapter? = null
    private lateinit var btnBack : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verify_post)

        recyclerView = findViewById(R.id.recyclerViewVerify)
        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Set reverse layout
        layoutManager.stackFromEnd = true // Stack items from the bottom
        recyclerView.layoutManager = layoutManager

        btnBack = findViewById(R.id.backBtn)

        // Initialize dbHelper
        dbHelper = DatabaseHelper(this)

        btnBack.setOnClickListener {
            val intent = Intent(this,PostView::class.java )
            startActivity(intent)
        }

        // Initialize and set the adapter
        adapter = VerifyPostAdapter(dbHelper)
        recyclerView.adapter = adapter

        // Fetch and display posts
        getPost()
    }

    private fun getPost() {
        val postList = dbHelper.getAllPosts()
        Log.e("pppp", "${postList.size}")

        // Display data in RecyclerView
        adapter?.addPosts(postList)

    }
}