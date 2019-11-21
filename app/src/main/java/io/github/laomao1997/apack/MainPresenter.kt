package io.github.laomao1997.apack

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainPresenter: MainContract.Presenter {

    private lateinit var mView: MainContract.View
    private lateinit var mModel: MainContract.Model
    private lateinit var mSharedPreferences: SharedPreferences
    private var onCycling = false

    override fun getData() {
        GlobalScope.launch(Dispatchers.Main) {
            val tmpList = mModel.getData(get())
            if (tmpList.size > 0) {
                mView.updateView(tmpList)
            } else {
                mView.makeToast("收包失败，请检查打包人设置是否有误")
            }

        }
    }

    override fun attachView(view: MainContract.View) {
        this.mView = view
        this.mModel = MainModel()
        this.mSharedPreferences = mView.getContext().getSharedPreferences("MY_SP", Context.MODE_PRIVATE)

        // 开始轮询
        onCycling = true
        GlobalScope.launch(Dispatchers.IO) {
            // 5秒向爬取一次
            while (onCycling) {
                delay(5000)
                launch(Dispatchers.Main) {
                    getData()
                }
            }
        }
    }

    override fun detachView() {
        onCycling = false
    }

    override fun save(name: String) {
        val mSPEditor = mSharedPreferences.edit()
        mSPEditor.putString("name", name)
        mSPEditor.apply()
    }

    override fun get(): String {
        val name = mSharedPreferences.getString("name", "")
        name?.let {
            return it
        }
        return ""
    }

}