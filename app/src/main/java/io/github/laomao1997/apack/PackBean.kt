package io.github.laomao1997.apack

data class PackBean(
    // 打包人
    val owner: String,
    // 项目名称
    val projectName: String,
    // 分支名
    val branchName: String,
    // 打包时间
    val time: String,
    // git号
    val gitNumber: String,
    // 类型
    val type: String,
    // 下载地址
    val link: String
)