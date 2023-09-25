package com.ai.mylibrary.bean
//保存股票代码信息
data class SaveStockInfoBean(
    val code: Int,
    val `data`: List<SaveData>,
    val message: String
)

data class SaveData(
    val count: String
)