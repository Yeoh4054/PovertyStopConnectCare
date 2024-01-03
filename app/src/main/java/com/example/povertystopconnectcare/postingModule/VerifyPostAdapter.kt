package com.example.povertystopconnectcare.postingModule

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.povertystopconnectcare.R

class VerifyPostAdapter(private val dbHelper: DatabaseHelper)
    : RecyclerView.Adapter<VerifyPostAdapter.PostViewHolder>(){

    private var postList: ArrayList<Post> = ArrayList()
    private var onClickItem: ((Post) -> Unit)? = null


    fun addPosts(items: ArrayList<Post>) {
        this.postList = items
        notifyDataSetChanged()
    }

    fun setOnClickItem(callback: (Post) -> Unit) {
        this.onClickItem = callback
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = PostViewHolder (
        LayoutInflater.from(parent.context).inflate(R.layout.activity_verify_details, parent, false)
    )

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = postList[position]
        holder.bindView(post, holder.itemView.context)
        holder.itemView.setOnClickListener { onClickItem?.invoke(post) }

        val postId = post.id // Assuming this is the post ID
        val userId = dbHelper.getUserIdByPostId(postId)

        if (userId != null) {
            val username = dbHelper.getUsernameByUserId(userId)
            holder.username.text = username ?: "Unknown User"
        } else {
            holder.username.text = "Unknown User"
        }

    }


    override fun getItemCount():Int {
        return postList.size
    }

    class PostViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        private var image = view.findViewById<ImageView>(R.id.postImageVerify)
        private var title = view.findViewById<TextView>(R.id.postTitleVerify)
        private var description = view.findViewById<TextView>(R.id.postDescriptionVerify)
        private var btnVerify: ImageView = view.findViewById(R.id.Verify)
        private var btnNotVerify: ImageView = view.findViewById(R.id.NotVerify)
        internal var username = view.findViewById<TextView>(R.id.usernameTextView)

        fun bindView(post: Post, context: Context) {
            // Load the image from ByteArray using Glide
            Glide.with(itemView.context)
                .load(post.image)
                .into(image)

            title.text = post.title
            description.text = post.description

            btnVerify.setOnClickListener {
                Toast.makeText(context, "Post Verified!", Toast.LENGTH_SHORT).show()
            }

            btnNotVerify.setOnClickListener {
                val intent = Intent(context,NotVerifyActivity::class.java )
                intent.putExtra("Mode","D")
                intent.putExtra("Id",post.id)
                context.startActivity(intent)
            }

        }

    }
}