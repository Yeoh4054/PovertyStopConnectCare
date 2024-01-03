package com.example.povertystopconnectcare.postingModule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.povertystopconnectcare.R

class CommentActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var commentAddText: EditText
    private lateinit var post: Post
    private lateinit var sendBtn: Button
    private var commentList = ArrayList<Comment>()
    private lateinit var adapter: CommentAdapter
    private lateinit var btnBack : ImageView
    private lateinit var sharedPreferences: SharedPreferences // Declare sharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recyclerViewComment)
        commentAddText = findViewById(R.id.commentAdd)
        sendBtn = findViewById(R.id.sendButton)
        btnBack = findViewById(R.id.backBtn)

        val layoutManager = LinearLayoutManager(this)
        layoutManager.reverseLayout = true // Set reverse layout
        layoutManager.stackFromEnd = true // Stack items from the bottom
        recyclerView.layoutManager = layoutManager

        adapter = CommentAdapter(this, commentList, dbHelper)
        recyclerView.adapter = adapter

        btnBack.setOnClickListener {
            val intent = Intent(this,PostView::class.java )
            startActivity(intent)
        }


        val postId = intent.getIntExtra("POST_ID", -1)

        if (postId != -1) {
            post = dbHelper.getPostById(postId) ?: Post() // Replace with your method to get a post by ID
        } else {
            Toast.makeText(this, "Invalid post ID", Toast.LENGTH_SHORT).show()
        }

        // Fetch and display comments for the specific post
        getCommentsForPost()

        sendBtn.setOnClickListener {
            addComment()
        }
    }

    private fun getCommentsForPost() {
        commentList.clear()
        commentList.addAll(dbHelper.getCommentsForPost(post.id))
        adapter.notifyDataSetChanged()
    }

    private fun addComment() {
        val commentData = commentAddText.text.toString().trim()
        if (commentData.isNotEmpty()) {
            val savedValue = sharedPreferences.getInt("userID",0)


            // Create a Comment object
            val comment = Comment(commentText = commentData, postId = post.id , userId = savedValue)

            // Add the comment to the database
            val success = dbHelper.addComment(comment)
            if (success != -1L) {
                commentAddText.text.clear()

                // Fetch and display comments for the specific post after adding a new comment
                getCommentsForPost()
            } else {
                Toast.makeText(this, "Failed to add comment", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Please enter a comment", Toast.LENGTH_SHORT).show()
        }
    }
    

}