package com.ai.mylibrary.bean
/*
* 弹幕数量
* */
class LikeIndexBean : ArrayList<LikeIndexBeanItem>()

data class LikeIndexBeanItem(
    val is_like: Boolean,
    val like_id: Int,
    val likes: Int
)