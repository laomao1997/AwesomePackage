package io.github.laomao1997.apack

import android.util.Log
import io.github.laomao1997.apack.Constant.TAG
import io.github.laomao1997.apack.Constant.URL_BETTER_PACKAGE
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.info
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements

class MainModel: MainContract.Model, AnkoLogger {

    private var mPackList = ArrayList<PackBean>()

    override suspend fun getData(owner: String): ArrayList<PackBean> {
        withContext(Dispatchers.IO) {
            val pageUrl = String.format(URL_BETTER_PACKAGE, owner)
            try {
                mPackList.clear()
                mPackList.addAll(getHtml(pageUrl))
            } catch (e: Exception) {
                info { e.printStackTrace() }
            }
        }
        return mPackList
    }

    private fun getHtml(pageUrl: String): ArrayList<PackBean> {
        val packList = ArrayList<PackBean>()
        val execute: Connection.Response = Jsoup.connect(pageUrl).execute()
        val doc: Document = Jsoup.parse(execute.body())
        val tbody: Elements = doc.select("tbody")
        val tr: Elements = tbody.select("tr")
        packList.add(PackBean(
            owner = tr.select("td")[0].text(),
            projectName = tr.select("td")[1].text(),
            branchName = tr.select("td")[2].text(),
            time = tr.select("td")[3].text(),
            gitNumber = tr.select("td")[4].text(),
            type = tr.select("td")[5].text(),
            link = tr.select("td")[7].select("a").first().attr("href")
        ))
        for (e in tr.next()) {
            // 打包人
            val owner = e.select("td")[0].text()
            // 项目名称
            val projectName = e.select("td")[1].text()
            // 分支名
            val branchName = e.select("td")[2].text()
            // 打包时间
            val time = e.select("td")[3].text()
            // git号
            val gitNumber = e.select("td")[4].text()
            // 类型
            val type = e.select("td")[5].text()
            // 下载地址
            val link = e.select("td")[7].select("a").first().attr("href")
            val pack = PackBean(owner, projectName, branchName, time, gitNumber, type, link)
            packList.add(pack)
        }
        return packList
    }

}