package com.ai.mylibrary.bean
/*
* 获取诊股数
* */
data class StockCountBean(
    val code: Int,
    val `data`: Data,
    val message: String
)

data class Data(
    val count: Int
)