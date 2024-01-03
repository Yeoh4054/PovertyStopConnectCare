package com.example.povertystopconnectcare.postingModule

import org.w3c.dom.Comment
import java.util.*

data class Post(
    var id: Int = 0,
    var image: ByteArray? = null,
    var title: String = "",
    var description: String = "",
    var userId: Int = 0

)

data class User(
    var userId: Int = 0,
    var username: String = ""
)

data class Like(
    var likeId: Int = 0,
    var postId: Int = 0,
    var userId: Int = 0
)

data class Comment(
    var commentId: Int = 0,
    var commentText: String = "",
    var postId: Int = 0,
    var userId: Int = 0
)

data class Bookmark(
    var bookmarkId: Int = 0,
    var postId: Int = 0,
    var userId: Int = 0
)




