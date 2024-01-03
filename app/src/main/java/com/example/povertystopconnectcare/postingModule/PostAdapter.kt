package com.example.povertystopconnectcare.postingModule

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.povertystopconnectcare.R

class PostAdapter(private val context: Context,
                  private var postList: ArrayList<Post>,
                  private var likeList: ArrayList<Like>,
                  private val dbHelper: DatabaseHelper)
    : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

    private var onClickItem: ((Post) -> Unit)? = null

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun setFilter(filteredPosts: ArrayList<Post>) {
        postList = filteredPosts
        notifyDataSetChanged()
    }

    fun addPosts(items: ArrayList<Post>) {
        this.postList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (Post) -> Unit) {
        this.onClickItem = callback
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.activity_post_main, parent, false)

    )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bindView(post)
        holder.itemView.setOnClickListener { onClickItem?.invoke(post) }
        val savedValue = sharedPreferences.getInt("userID", 0)

        val postId = post.id // Assuming this is the post ID
        val userId = dbHelper.getUserIdByPostId(postId)

        if (userId != null) {
            val username = dbHelper.getUsernameByUserId(userId)
            holder.username.text = username ?: "Unknown User"
        } else {
            holder.username.text = "Unknown User"
        }

        val likeImageView = holder.itemView.findViewById<ImageView>(R.id.btnLike)
        val likeCountTextView = holder.itemView.findViewById<TextView>(R.id.likeCountTextView)
        val bookmarkImageView = holder.itemView.findViewById<ImageView>(R.id.btnBookmark)

        val isBookmarked = dbHelper.isPostBookmarked(savedValue, post.id)
        // Set initial bookmark icon based on whether the post is bookmarked by the user
        if (isBookmarked) {
            bookmarkImageView.setImageResource(R.drawable.bookmark_selected)
        } else {
            bookmarkImageView.setImageResource(R.drawable.bookmark)
        }

        bookmarkImageView.setOnClickListener {
            toggleBookmark(post.id, savedValue, bookmarkImageView)
        }

        // Set initial like count for each post
        val likeCount = dbHelper.getLikeCountForPost(post.id)
        likeCountTextView.text = likeCount.toString()

        val isLiked = isPostLikedByUser(post.id, savedValue)

        if (isLiked) {
            likeImageView.setImageResource(R.drawable.like_red)
        } else {
            likeImageView.setImageResource(R.drawable.like)
        }

        likeImageView.setOnClickListener {
            if (savedValue != 0) {
                val like = Like(postId = post.id, userId = savedValue)

                // Check if the user has already liked/disliked this post
                val alreadyLiked = dbHelper.isPostLiked(post.id, savedValue)

                if (alreadyLiked) {
                    // User has already liked/disliked, so remove the like from the database
                    val success = dbHelper.removeLike(like)
                    if (success != -1) {
                        val updatedLikeCount = dbHelper.getLikeCountForPost(post.id)
                        likeCountTextView.text = updatedLikeCount.toString()
                        likeImageView.setImageResource(R.drawable.like)
                    } else {
                        Log.e("LikeOperation", "Failed to remove like")
                    }
                } else {
                    // User has not liked/disliked, so add the like to the database
                    val success = dbHelper.addLike(like)
                    if (success != -1L) {
                        val updatedLikeCount = dbHelper.getLikeCountForPost(post.id)
                        likeCountTextView.text = updatedLikeCount.toString()
                        likeImageView.setImageResource(R.drawable.like_red)
                    } else {
                        Log.e("LikeOperation", "Failed to add like")
                    }
                }
            } else {
                Log.e("LikeOperation", "User ID not found")
            }
        }


        holder.itemView.findViewById<ImageView>(R.id.btnComment).setOnClickListener {
            val intent = Intent(context, CommentActivity::class.java)
            intent.putExtra("POST_ID", post.id)
            context.startActivity(intent)
        }

    }

    private fun toggleBookmark(postId: Int, userId: Int, bookmarkImageView: ImageView) {
        val isBookmarked = dbHelper.isPostBookmarked(userId, postId)

        if (isBookmarked) {
            // If post is already bookmarked, remove bookmark
            val bookmark = Bookmark(postId = postId, userId = userId)
            val success = dbHelper.removeBookmark(bookmark)
            if (success != -1) {
                bookmarkImageView.setImageResource(R.drawable.bookmark)
            } else {
                // Handle removal failure
                Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show()
            }
        } else {
            // If post is not bookmarked, add bookmark
            val bookmark = Bookmark(postId = postId, userId = userId)
            val success = dbHelper.addBookmark(bookmark)
            if (success != -1L) {
                bookmarkImageView.setImageResource(R.drawable.bookmark_selected)
            } else {
                // Handle removal failure
                Toast.makeText(context, "Failed to add bookmark", Toast.LENGTH_SHORT).show()
            }
        }
    }


    override fun getItemCount():Int {
        return postList.size
    }

    private fun isPostLikedByUser(postId: Int, userId: Int): Boolean {
        // Perform a database query to check if the user has liked this post
        val dbHelper = DatabaseHelper(context) // Initialize your database helper class
        return dbHelper.isPostLiked(postId, userId)
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.postImage)
        private var title = view.findViewById<TextView>(R.id.postTitle)
        private var description = view.findViewById<TextView>(R.id.postDescription)
        internal var username = view.findViewById<TextView>(R.id.usernameTextView)


        fun bindView(post: Post) {
            // Load the image from ByteArray using Glide
            Glide.with(itemView.context)
                .load(post.image)
                .into(image)

            title.text = post.title
            description.text = post.description

        }

    }

}