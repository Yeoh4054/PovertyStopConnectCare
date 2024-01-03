package com.example.povertystopconnectcare.postingModule

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.povertystopconnectcare.R

class DeletePostActivity : AppCompatActivity() {

    private var databaseHelper: DatabaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_delete_post)

        // Initialize your DatabaseHelper here
        databaseHelper = DatabaseHelper(this)

        showDeleteDialog()


    }


    @SuppressLint("SuspiciousIndentation")
    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton("YES") { dialog, _ ->
                val success = databaseHelper?.deletePost(intent.getIntExtra("Id",0)) as Boolean
                if (success)
                    finish()
                    dialog.dismiss()
                    val intent = Intent(this,PostManagementActivity::class.java )
                    startActivity(intent)
            }
            .setNegativeButton("NO") { dialog, _ ->
                    dialog.dismiss()
                    finish()
                    val intent = Intent(this,PostManagementActivity::class.java )
                    startActivity(intent)
            }
            .show()
    }

}