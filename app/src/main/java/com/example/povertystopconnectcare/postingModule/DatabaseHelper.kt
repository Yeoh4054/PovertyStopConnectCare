package com.example.povertystopconnectcare.postingModule

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.widget.Toast
import java.lang.Exception

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

        companion object {
            private const val DATABASE_VERSION = 1
            private const val DATABASE_NAME = "post.db"

            private const val TBL_POST = "tbl_post"
            private const val ID = "id"
            private const val IMAGE = "image"
            private const val TITLE = "title"
            private const val DESCRIPTION = "description"

            private const val TBL_LIKE = "tbl_like"
            private const val LIKE_ID = "likeid"

            private const val TBL_COMMENT = "tbl_comment"
            private const val COMMENT_ID = "comment_id"
            private const val COMMENT_TEXT = "comment_text"

            private const val TBL_BOOKMARK = "tbl_bookmark"
            private const val BOOKMARK_ID = "bookmark_id"

            private const val TBL_USER = "tbl_user"
            private const val UID = "uid"
            private const val USERNAME = "username"

            private const val POST_ID = "post_id"
            private const val USER_ID = "user_id"

            private const val FOREIGN_KEY_ON = "PRAGMA foreign_keys=ON;"

        }

        override fun onCreate(db: SQLiteDatabase?) {
            // Create tbl_post table with foreign key constraint
            val createTblPost = ("CREATE TABLE " + TBL_POST + "("
                    + ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + IMAGE + " BLOB,"
                    + TITLE + " TEXT,"
                    + DESCRIPTION + " TEXT,"
                    + USER_ID + " INTEGER,"
                    + " FOREIGN KEY(" + USER_ID + ") REFERENCES " + TBL_USER + "(" + UID + ")"
                    + " ON DELETE CASCADE" // Optional: Delete posts associated with a user if that user is deleted
                    + " )")
            db?.execSQL(createTblPost)

            val createTblUser = ("CREATE TABLE " + TBL_USER + "("
                    + UID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + USERNAME + " TEXT" + " )")
            db?.execSQL(createTblUser)

            // Insert dummy data into user table
            val userValues = ContentValues()
            userValues.put(UID, 1)
            userValues.put(USERNAME, "JohnDoe")
            db?.insert(TBL_USER, null, userValues)

            // Insert dummy data into user table
            val userValues2 = ContentValues()
            userValues.put(UID, 2)
            userValues.put(USERNAME, "SitiJay")
            db?.insert(TBL_USER, null, userValues)

            val createTblLike = ("CREATE TABLE " + TBL_LIKE + "("
                    + LIKE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + POST_ID + " INTEGER,"
                    + USER_ID + " INTEGER,"
                    + " FOREIGN KEY(" + USER_ID + ") REFERENCES " + TBL_USER + "(" + UID + ")"
                    + " ON DELETE CASCADE,"
                    + " FOREIGN KEY(" + POST_ID + ") REFERENCES " + TBL_POST + "(" + ID + ")"
                    + " ON DELETE CASCADE"
                    + " )")
            db?.execSQL(createTblLike)

            val createTblComment = ("CREATE TABLE " + TBL_COMMENT + "("
                    + COMMENT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + COMMENT_TEXT + " TEXT,"
                    + POST_ID + " INTEGER,"
                    + USER_ID + " INTEGER,"
                    + " FOREIGN KEY(" + USER_ID + ") REFERENCES " + TBL_USER + "(" + UID + ")"
                    + " ON DELETE CASCADE,"
                    + " FOREIGN KEY(" + POST_ID + ") REFERENCES " + TBL_POST + "(" + ID + ")"
                    + " ON DELETE CASCADE"
                    + " )")
            db?.execSQL(createTblComment)

            val createTblBookmark = ("CREATE TABLE " + TBL_BOOKMARK + "("
                    + BOOKMARK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + POST_ID + " INTEGER,"
                    + USER_ID + " INTEGER,"
                    + " FOREIGN KEY(" + USER_ID + ") REFERENCES " + TBL_USER + "(" + UID + ")"
                    + " ON DELETE CASCADE,"
                    + " FOREIGN KEY(" + POST_ID + ") REFERENCES " + TBL_POST + "(" + ID + ")"
                    + " ON DELETE CASCADE"
                    + " )")
            db?.execSQL(createTblBookmark)


            // Enable foreign key constraints
            db?.execSQL(FOREIGN_KEY_ON)
        }

        override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
            db!!.execSQL("DROP TABLE IF EXISTS $TBL_POST")
            db!!.execSQL("DROP TABLE IF EXISTS $TBL_LIKE")
            db!!.execSQL("DROP TABLE IF EXISTS $TBL_COMMENT")
            db!!.execSQL("DROP TABLE IF EXISTS $TBL_BOOKMARK")
            db!!.execSQL("DROP TABLE IF EXISTS $TBL_USER")
            onCreate(db)
        }

        fun insertPost(post: Post): Long {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(IMAGE, post.image)
            contentValues.put(TITLE, post.title)
            contentValues.put(DESCRIPTION, post.description)
            contentValues.put(USER_ID, post.userId)

            val success = db.insert(TBL_POST, null, contentValues)
            db.close()
            return success
        }

        fun addLike(like: Like): Long {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(POST_ID, like.postId)
            contentValues.put(USER_ID, like.userId)
            val success = db.insert(TBL_LIKE, null, contentValues)
            db.close()
            return success
        }

        fun getLikeCountForPost(postId: Int): Int {
            val db = this.readableDatabase
            val query = "SELECT COUNT(*) FROM $TBL_LIKE WHERE $POST_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(postId.toString()))

            var count = 0
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0)
            }

            cursor.close()
            db.close()

            return count
        }

        fun isPostLiked(postId: Int, userId: Int): Boolean {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TBL_LIKE WHERE $POST_ID = ? AND $USER_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(postId.toString(), userId.toString()))

            val liked = cursor.count > 0
            cursor.close()
            db.close()

            return liked
        }

        fun removeLike(like: Like): Int {
            val db = this.writableDatabase
            val success = db.delete(
                TBL_LIKE,
                "$POST_ID = ? AND $USER_ID = ?",
                arrayOf(like.postId.toString(), like.userId.toString())
            )
            db.close()
            return success
        }

        // Function to add a comment
        fun addComment(comment: Comment): Long {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(COMMENT_TEXT, comment.commentText)
            contentValues.put(POST_ID, comment.postId)
            contentValues.put(USER_ID, comment.userId)

            val success = db.insert(TBL_COMMENT, null, contentValues)
            db.close()
            return success
        }

        fun removeComment(commentId: Int, userId: Int, postId: Int): Int {
            val db = this.writableDatabase
            return db.delete(
                TBL_COMMENT,
                "$COMMENT_ID = ? AND $USER_ID = ? AND $POST_ID = ?",
                arrayOf(commentId.toString(), userId.toString(), postId.toString())
            )
        }

    fun updateComment(comment: Comment): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COMMENT_TEXT, comment.commentText)

        val whereClause = "$COMMENT_ID = ? AND $USER_ID = ? AND $POST_ID = ?"
        val whereArgs = arrayOf(
            comment.commentId.toString(),
            comment.userId.toString(),
            comment.postId.toString()
        )

        val rowsAffected = db.update(TBL_COMMENT, contentValues, whereClause, whereArgs)
        db.close()

        return rowsAffected > 0
    }

        fun getCommentsForPost(postId: Int): ArrayList<Comment> {
            val commentList: ArrayList<Comment> = ArrayList()
            val selectQuery = "SELECT * FROM $TBL_COMMENT WHERE $POST_ID = $postId"
            val db = this.readableDatabase

            val cursor = db.rawQuery(selectQuery, null)
            if (cursor.moveToFirst()) {
                val commentIdIndex = cursor.getColumnIndex(COMMENT_ID)
                val commentTextIndex = cursor.getColumnIndex(COMMENT_TEXT)
                val userIdIndex = cursor.getColumnIndex(USER_ID)

                do {
                    val commentId = if (commentIdIndex != -1) cursor.getInt(commentIdIndex) else -1
                    val commentText = if (commentTextIndex != -1) cursor.getString(commentTextIndex) else ""
                    val userId = if (userIdIndex != -1) cursor.getInt(userIdIndex) else -1

                    val comment = Comment(commentId, commentText, postId, userId)
                    commentList.add(comment)
                } while (cursor.moveToNext())
            }

            cursor.close()
            db.close()
            return commentList
        }

        fun addBookmark(bookmark: Bookmark): Long {
            val db = this.writableDatabase
            val contentValues = ContentValues()
            contentValues.put(USER_ID, bookmark.userId)
            contentValues.put(POST_ID, bookmark.postId)
            val success = db.insert(TBL_BOOKMARK, null, contentValues)
            db.close()
            return success
        }

        fun removeBookmark(bookmark: Bookmark): Int {
            val db = this.writableDatabase
            val success = db.delete(
                TBL_BOOKMARK,
                "$USER_ID = ? AND $POST_ID = ?",
                arrayOf(bookmark.userId.toString(), bookmark.postId.toString())
            )
            db.close()
            return success
        }

        fun isPostBookmarked(userId: Int, postId: Int): Boolean {
            val db = this.readableDatabase
            val query = "SELECT * FROM $TBL_BOOKMARK WHERE $USER_ID = ? AND $POST_ID = ?"
            val cursor = db.rawQuery(query, arrayOf(userId.toString(), postId.toString()))

            val bookmarked = cursor.count > 0
            cursor.close()
            db.close()

            return bookmarked
        }

        fun getBookmarkedPosts(userId: Int): ArrayList<Post> {
            val bookmarkedPosts = ArrayList<Post>()
            val db = this.readableDatabase
            val query = "SELECT * FROM $TBL_BOOKMARK WHERE $USER_ID = $userId"

            val cursor = db.rawQuery(query, null)
            if (cursor.moveToFirst()) {
                do {
                    val postIdIndex = cursor.getColumnIndex(POST_ID)
                    val postId = if (postIdIndex != -1) cursor.getInt(postIdIndex) else -1

                    val post = getPostById(postId)
                    post?.let { bookmarkedPosts.add(it) }
                } while (cursor.moveToNext())
            }
            cursor.close()
            return bookmarkedPosts
        }

        // Function to get posts for a specific user ID
        fun getPostsForUser(userId: Int): ArrayList<Post> {
        val postList: ArrayList<Post> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_POST WHERE $USER_ID = $userId"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        // Ensure columns exist in the cursor before retrieving data
        val idIndex = cursor.getColumnIndex(ID)
        val imageIndex = cursor.getColumnIndex(IMAGE)
        val titleIndex = cursor.getColumnIndex(TITLE)
        val descriptionIndex = cursor.getColumnIndex(DESCRIPTION)

        if (idIndex >= 0 && imageIndex >= 0 && titleIndex >= 0 && descriptionIndex >= 0) {
            // Retrieve posts associated with the provided user ID
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(idIndex)
                    val image = cursor.getBlob(imageIndex)
                    val title = cursor.getString(titleIndex)
                    val description = cursor.getString(descriptionIndex)

                    val post = Post(id = id, image = image, title = title, description = description, userId = userId)
                    postList.add(post)
                } while (cursor.moveToNext())
            }
        }

        cursor.close()
        db.close()

        return postList
    }

    fun getUsernameByUserId(userId: Int): String? {
        val db = this.readableDatabase
        var username: String? = null

        val selectQuery = "SELECT $USERNAME FROM $TBL_USER WHERE $UID = ?"
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        cursor?.let {
            if (it.moveToFirst()) {
                val usernameIndex = it.getColumnIndex(USERNAME)
                username = if (usernameIndex != -1) it.getString(usernameIndex) else null
            }
            it.close()
        }
        db.close()

        return username
    }

    fun getUserIdByPostId(postId: Int): Int? {
        val db = this.readableDatabase
        var userId: Int? = null

        val selectQuery = "SELECT $USER_ID FROM $TBL_POST WHERE $ID = ?"
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(postId.toString()))

        cursor?.let {
            if (it.moveToFirst()) {
                val userIdIndex = it.getColumnIndex(USER_ID)
                userId = if (userIdIndex != -1) it.getInt(userIdIndex) else null
            }
            it.close()
        }
        db.close()

        return userId
    }


    fun getUserById(userId: Int): User? {
        val db = this.readableDatabase
        var user: User? = null

        val selectQuery = "SELECT * FROM $TBL_USER WHERE $UID = ?"
        val cursor: Cursor? = db.rawQuery(selectQuery, arrayOf(userId.toString()))

        cursor?.let {
            if (it.moveToFirst()) {
                val idColumnIndex = it.getColumnIndex(UID)
                val usernameColumnIndex = it.getColumnIndex(USERNAME)

                // Check if the column indices are valid (-1 indicates column not found)
                if (idColumnIndex != -1 && usernameColumnIndex != -1) {
                    val id = it.getInt(idColumnIndex)
                    val username = it.getString(usernameColumnIndex)

                    user = User(userId = id, username = username)
                } else {
                    // Handle the case where columns are not found
                    // Log an error, throw an exception, or handle it as per your requirement
                }
            }
            it.close()
        }

        return user
    }

    fun getPostById(postId: Int): Post? {
        val db = this.readableDatabase
        var post: Post? = null

        val cursor = db.query(
            TBL_POST,
            arrayOf(ID, IMAGE, TITLE, DESCRIPTION),
            "$ID = ?",
            arrayOf(postId.toString()),
            null,
            null,
            null
        )

        val idColumnIndex = cursor.getColumnIndex(ID)
        val imageColumnIndex = cursor.getColumnIndex(IMAGE)
        val titleColumnIndex = cursor.getColumnIndex(TITLE)
        val descriptionColumnIndex = cursor.getColumnIndex(DESCRIPTION)

        if (idColumnIndex != -1 && imageColumnIndex != -1 && titleColumnIndex != -1 && descriptionColumnIndex != -1) {
            if (cursor.moveToFirst()) {
                val id = cursor.getInt(idColumnIndex)
                val image = cursor.getBlob(imageColumnIndex)
                val title = cursor.getString(titleColumnIndex)
                val description = cursor.getString(descriptionColumnIndex)

                post = Post(id, image, title, description)
            }
        } else {
            "error"
        }

        cursor.close()
        db.close()

        return post
    }

    fun updatePost(post: Post): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(IMAGE, post.image)
        contentValues.put(TITLE, post.title)
        contentValues.put(DESCRIPTION, post.description)

        // Update the post with the specified ID
        val success = db.update(
            TBL_POST,
            contentValues,
            "$ID = ?",
            arrayOf(post.id.toString())).toLong()
        db.close()
        return Integer.parseInt("$success") != -1
    }

    fun deletePost(postId: Int): Boolean {
        val db = this.writableDatabase

        val success = db.delete(
            TBL_POST,
            "$ID = ?",
            arrayOf(postId.toString())).toLong()
        db.close()
        return Integer.parseInt("$success") != -1
    }


    fun getAllPosts(): ArrayList<Post> {
        val postList: ArrayList<Post> = ArrayList()
        val selectQuery = "SELECT * FROM $TBL_POST"
        val db = this.readableDatabase

        val cursor: Cursor?

        try {
            cursor = db.rawQuery(selectQuery, null)
        } catch (e: Exception) {
            e.printStackTrace()
            db.execSQL(selectQuery)
            return ArrayList()
        }

        var id: Int
        var image: ByteArray
        var title: String
        var description: String

        if (cursor.moveToFirst()) {
            do {
                val idIndex = cursor.getColumnIndex("id")
                val imageIndex = cursor.getColumnIndex("image")
                val titleIndex = cursor.getColumnIndex("title")
                val descriptionIndex = cursor.getColumnIndex("description")

                val id = if (idIndex >= 0) cursor.getInt(idIndex) else -1
                val image = if (imageIndex >= 0) cursor.getBlob(imageIndex) else null
                val title = if (titleIndex >= 0) cursor.getString(titleIndex) else ""
                val description = if (descriptionIndex >= 0) cursor.getString(descriptionIndex) else ""

                val post = Post(id = id, image = image, title = title, description  = description)
                postList.add(post)
            } while (cursor.moveToNext())
        }
        cursor.close()
        db.close()

        return postList
    }


}


