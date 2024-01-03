package com.example.povertystopconnectcare.postingModule

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.example.povertystopconnectcare.R
import java.io.ByteArrayOutputStream

class PostUpdateActivity : AppCompatActivity() {

    private lateinit var btnUpdate: Button
    private lateinit var btnCancel: Button
    private lateinit var editTitleUpdate: EditText
    private lateinit var editDescriptionUpdate: EditText
    private lateinit var imageUpdate: ImageView
    private var databaseHelper: DatabaseHelper? = null
    private var isEditMode: Boolean = false
    private lateinit var btnBack : ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_update)

        btnUpdate = findViewById(R.id.changeBtn)
        btnCancel = findViewById(R.id.cancelBtn)
        editTitleUpdate = findViewById(R.id.titlePostUpdate)
        editDescriptionUpdate = findViewById(R.id.descriptionPostUpdate)
        imageUpdate = findViewById(R.id.imagePostUpdate)
        btnBack = findViewById(R.id.backBtn)

        databaseHelper = DatabaseHelper(this)

        btnBack.setOnClickListener {
            val intent = Intent(this,PostManagementActivity::class.java )
            startActivity(intent)
        }

        imageUpdate.setOnClickListener {
            openImageChooser()
        }


        if (intent != null && intent.getStringExtra("Mode") == "E") {
            // Update Data
            isEditMode = true
            btnUpdate.text = "Update Data"
            btnCancel.visibility = View.VISIBLE
            val post: Post? = databaseHelper!!.getPostById(intent.getIntExtra("Id", 0))
            post?.let {
                editTitleUpdate.setText(it.title)
                editDescriptionUpdate.setText(it.description)
                it.image?.let { imageByteArray ->
                    val bitmap = BitmapFactory.decodeByteArray(imageByteArray, 0, imageByteArray.size)
                    imageUpdate.setImageBitmap(bitmap)
                }
            }
        } else {
            isEditMode = false
            btnUpdate.text = "Save data"
            btnCancel.visibility = View.GONE
        }

        btnUpdate.setOnClickListener {
            if (validateInputs()) {
                updatePost()
            } else {
                Toast.makeText(this, "Please fill all fields and select an image", Toast.LENGTH_SHORT).show()
            }
        }

        btnCancel.setOnClickListener {
            clearInputs()
        }

    }

    private fun validateInputs(): Boolean {
        val title = editTitleUpdate.text.toString().trim()
        val description = editDescriptionUpdate.text.toString().trim()

        return !(title.isEmpty() || description.isEmpty() || imageUpdate.drawable == null)
    }

    private fun clearInputs() {
        editTitleUpdate.setText("")
        editDescriptionUpdate.setText("")
        val placeholderDrawable = ContextCompat.getDrawable(this, R.drawable.placeholder)
        imageUpdate.setImageDrawable(placeholderDrawable)
    }

    private fun updatePost() {
        var success: Boolean = false
        val post = Post()
        if (isEditMode) {
            post.id = intent.getIntExtra("Id", 0)
            post.title = editTitleUpdate.text.toString()
            post.description = editDescriptionUpdate.text.toString()
            post.image = convertImageToByteArray(imageUpdate)

            success = databaseHelper?.updatePost(post) as Boolean
        }

        if (success) {
            val i = Intent(applicationContext, PostManagementActivity::class.java)
            startActivity(i)
            finish()
        } else {
            Toast.makeText(this, "Update failed!", Toast.LENGTH_SHORT).show()
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
                    imageUpdate.setImageURI(selectedImageUri) // Set the image to ImageView
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

}