package com.ai.mylibrary.bean
//点赞
data class UserAddLikeCountsBean(
    val code: Int,
    val `data`: LikeData,
    val message: String
)

data class LikeData(
    val like_id: Int,
    val likes: Int
)