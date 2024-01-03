package com.example.povertystopconnectcare.postingModule


import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Html.ImageGetter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.povertystopconnectcare.R
import java.io.ByteArrayOutputStream

class PostActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var imagePost: ImageView
    private lateinit var editTitle: EditText
    private lateinit var editDescription: EditText
    private lateinit var btnSubmit: Button
    private lateinit var btnCancel: Button
    private lateinit var btnBack : ImageView

    private lateinit var sharedPreferences: SharedPreferences // Declare sharedPreferences


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post)

        // Initialize sharedPreferences within onCreate
        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)


        imagePost = findViewById(R.id.imagePost)
        editTitle = findViewById(R.id.titlePost)
        editDescription = findViewById(R.id.descriptionPost)
        btnSubmit = findViewById(R.id.submitBtn)
        btnCancel = findViewById(R.id.cancelBtn)
        btnBack = findViewById(R.id.backBtn)
        dbHelper = DatabaseHelper(this)

        btnBack.setOnClickListener {
            val intent = Intent(this,PostView::class.java )
            startActivity(intent)
        }

        imagePost.setOnClickListener {
            openImageChooser()
        }

        btnSubmit.setOnClickListener {
            if (validateInputs()) {
                insertPost()
                val intent = Intent(this, PostView::class.java)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            clearInputs()
        }


    }

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            // Handle the result here
            if (data != null) {
                // Process the selected image
                val selectedImageUri: Uri? = data.data
                if (isValidImageType(selectedImageUri)) {
                    imagePost.setImageURI(selectedImageUri) // Set the image to ImageView
                } else {
                    Toast.makeText(this, "Please select a valid image (JPEG, PNG, GIF, BMP, WEBP)", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private val acceptedImageTypes = arrayOf(
        "image/jpeg",
        "image/png",
        "image/gif",
        "image/bmp",
        "image/webp"
    )

    private fun openImageChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        intent.putExtra(Intent.EXTRA_MIME_TYPES, acceptedImageTypes)
        pickImageLauncher.launch(intent)
    }

    private fun isValidImageType(uri: Uri?): Boolean {
        uri?.let {
            contentResolver.getType(uri)?.let { mimeType ->
                return acceptedImageTypes.contains(mimeType)
            }
        }
        return false
    }


    private fun convertImageToByteArray(imageView: ImageView): ByteArray? {
        val drawable = imageView.drawable
        val bitmap = (drawable as BitmapDrawable).bitmap

        val stream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream)

        return stream.toByteArray()
    }

    private fun validateInputs(): Boolean {
        val title = editTitle.text.toString().trim()
        val description = editDescription.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || imagePost.drawable == null) {
            Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun insertPost() {
        val savedValue = sharedPreferences.getInt("userID", 0)
        val title = editTitle.text.toString()
        val description = editDescription.text.toString()

        // Convert image to a byte array
        val imageByteArray: ByteArray? = convertImageToByteArray(imagePost)

        if (validateInputs()) {
            if (savedValue == 0 || imageByteArray == null || imageByteArray.isEmpty() || title.isEmpty() || description.isEmpty()) {
                Toast.makeText(this, "Please enter required field", Toast.LENGTH_SHORT).show()
            } else {
                val post = Post(image = imageByteArray, title = title, description = description, userId = savedValue)
                val status = dbHelper.insertPost(post)
                // Check insert success or not success
                if (status > -1) {
                    Toast.makeText(this, "Post Inserted!", Toast.LENGTH_SHORT).show()
                    clearInputs()
                } else {
                    Toast.makeText(this, "Post Not Saved!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun clearInputs() {
        editTitle.setText("")
        editDescription.setText("")
        val placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.placeholder)
        imagePost.setImageDrawable(placeholderDrawable) // Set the image to a placeholder
    }

}