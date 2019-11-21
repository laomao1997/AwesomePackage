package io.github.laomao1997.apack

import android.content.Context

interface MainContract {

    interface View {
        fun makeToast(msg: String)

        fun updateView(packList: ArrayList<PackBean>)

        fun getContext(): Context
    }

    interface Presenter {
        fun getData()

        fun attachView(view: View)

        fun detachView()

        fun save(name: String)

        fun get(): String
    }

    interface Model {
        suspend fun getData(owner: String): ArrayList<PackBean>
    }

}