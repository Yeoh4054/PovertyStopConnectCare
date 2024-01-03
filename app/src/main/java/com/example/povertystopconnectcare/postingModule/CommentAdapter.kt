package com.example.povertystopconnectcare.postingModule

import android.app.Dialog
import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.povertystopconnectcare.R

class CommentAdapter(private var context: Context,
                     private var commentList: List<Comment>,
                     private var dbHelper: DatabaseHelper )
    : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    // private var postList: ArrayList<Post> = ArrayList()
    private var onClickItem: ((Comment) -> Unit)? = null

    private val sharedPreferences: SharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun setOnClickItem(callback: (Comment) -> Unit) {
        this.onClickItem = callback
    }

    fun updateComment(updatedText: String, position: Int) {
        if (updatedText.isNotEmpty()) {
            val commentToUpdate = commentList[position]
            commentToUpdate.commentText = updatedText
            notifyItemChanged(position)

            // Update the comment in the database
            dbHelper.updateComment(commentToUpdate)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CommentViewHolder(
        LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_comment_details, parent, false)
    )

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = commentList[position]
        holder.bindView(comment)
        holder.itemView.setOnClickListener { onClickItem?.invoke(comment) }
        val savedValue = sharedPreferences.getInt("userID", 0)

        // Fetch user information based on the userId associated with the comment
        val user = dbHelper.getUserById(comment.userId)

        // Set the username in the corresponding TextView
        holder.commentUser.text = user?.username ?: "Unknown User"

        // Show delete button for the logged-in user's comments
        holder.deleteComment.visibility = if (comment.userId == savedValue) View.VISIBLE else View.GONE

        holder.commentText.text = comment.commentText

        holder.deleteComment.setOnClickListener {
            val comment = commentList[position]
            val postId = comment.postId // Replace with the ID of the post

            // Assuming you have the comment ID available in your comment object
            val commentId = comment.commentId

            // Remove the comment from the database
            val deletedRows = dbHelper.removeComment(commentId, savedValue, postId)
            if (deletedRows > 0) {
                // Remove the comment from the list and update the RecyclerView
                commentList = commentList.filter { it.commentId != commentId }
                notifyDataSetChanged()
                Toast.makeText(holder.itemView.context, "Comment deleted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(holder.itemView.context, "Failed to delete comment", Toast.LENGTH_SHORT).show()
            }
        }

        holder.editComment.visibility = if (comment.userId == savedValue) View.VISIBLE else View.GONE
        holder.editComment.setOnClickListener {
            if (comment.userId == savedValue) {
                val dialog = Dialog(holder.itemView.context)
                dialog.setContentView(R.layout.dialog_edit_comment)

                val editCommentText = dialog.findViewById<EditText>(R.id.editCommentText)
                val updateButton = dialog.findViewById<Button>(R.id.updateButton)

                editCommentText.setText(comment.commentText)

                updateButton.setOnClickListener {
                    val updatedText = editCommentText.text.toString().trim()
                    if (updatedText.isNotEmpty()) {
                        commentList[position].commentText = updatedText
                        notifyItemChanged(position)

                        // Update the comment in the database
                        dbHelper.updateComment(commentList[position])

                        dialog.dismiss()
                    } else {
                        Toast.makeText(holder.itemView.context, "Please enter a comment", Toast.LENGTH_SHORT).show()
                    }
                }

                dialog.show()
            } else {
                Toast.makeText(holder.itemView.context, "You can't edit this comment", Toast.LENGTH_SHORT).show()
            }
        }


    }


    override fun getItemCount(): Int {
        return commentList.size
    }

    class CommentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        internal var commentText = view.findViewById<TextView>(R.id.comment)
        internal var commentUser = view.findViewById<TextView>(R.id.commentUser)
        internal var deleteComment = view.findViewById<ImageView>(R.id.deleteComment)
        internal var editComment = view.findViewById<ImageView>(R.id.editComment)

        fun bindView(comment: Comment) {
            commentText.text = comment.commentText


        }
    }

}


