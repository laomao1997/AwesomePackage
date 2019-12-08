package io.github.laomao1997.apack

import android.Manifest
import android.app.Service
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import io.github.laomao1997.apack.Constant.URL_PREFIX
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), MainContract.View {


    private lateinit var mPresenter: MainContract.Presenter
    private lateinit var vibrator: Vibrator
    private var onFirstRequest = true

    var count: Int = 0

    var mPackList = ArrayList<PackBean>()

    private lateinit var mToolbar: Toolbar
    private lateinit var mRecyclerView: RecyclerView
    private lateinit var mAdapter: MainAdapter
    private lateinit var mSwipeRefresher: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
    }

    override fun onResume() {
        super.onResume()
        mPresenter = MainPresenter()
        mPresenter.attachView(this)
        requestPermission()
    }

    override fun makeToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun updateView(packList: ArrayList<PackBean>) {
        if (mSwipeRefresher.isRefreshing) {
            mSwipeRefresher.isRefreshing = false
        }
        count = packList.size
        if (onFirstRequest) {
            onFirstRequest = false
        } else {
            // 当返回的数据不一样
            if (mPackList[0].gitNumber != packList[0].gitNumber &&
                mPackList[1].gitNumber == packList[1].gitNumber &&
                !onFirstRequest
            ) {
                sendNotification()
            }
        }
        mPackList.clear()
        mPackList.addAll(packList)
        mAdapter.updateData(mPackList)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.set_name -> {
                var name: EditText? = null
                alert("设置打包人") {
                    customView {
                        verticalLayout {
                            name = editText {
                                setText(mPresenter.get())
                                hint = "请输入打包人名字全拼"
                                textSize = 24f
                            }
                            padding = dip(16)
                        }
                    }
                    yesButton {
                        makeToast("打包人设置为 ${name?.text.toString()}")
                        mPresenter.save(name?.text.toString())
                        mPresenter.getData()
                        onFirstRequest = true
                    }
                    noButton { }
                }.show()
            }
            else -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun getContext(): Context {
        return this
    }

    override fun onPause() {
        super.onPause()
        mPresenter.detachView()
    }

    private fun initView() {
        mToolbar = findViewById(R.id.toolbar)
        mSwipeRefresher = findViewById(R.id.swipe_refresh)
        mRecyclerView = findViewById(R.id.recycle_view)
        mAdapter = MainAdapter()
        setSupportActionBar(mToolbar)
        mRecyclerView.adapter = mAdapter
        mAdapter.setData(mPackList)
        mRecyclerView.layoutManager = LinearLayoutManager(this)
        mAdapter.setOnDownloadClickListener(DownloadClickListenerImpl())
        mSwipeRefresher.setOnRefreshListener {
            makeToast("刷新")
            mPresenter.getData()
        }
    }

    private fun requestPermission() = runWithPermissions(Manifest.permission.INTERNET) {
        mPresenter.getData()
    }

    private fun openLink(link: String) {
        browse(link)
    }

    private fun sendNotification() {
        vibrator = getSystemService(Service.VIBRATOR_SERVICE) as Vibrator
        if (vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= 26) {
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        2000,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                vibrator.vibrate(2000)
            }
        }
        alert {
            customView {
                verticalLayout {
                    textView("有新包") {
                        textSize = sp(8).toFloat()
                        textColor = android.graphics.Color.BLACK
                    }.lparams() {
                        bottomMargin = dip(10)
                    }
                    if (mPackList.size > 0) {
                        textView("时间：${mPackList[0].time}")
                        textView("分支：${mPackList[0].branchName}")
                        textView("作者：${mPackList[0].owner}")
                        textView("Git号：${mPackList[0].gitNumber}")
                    }
                    padding = dip(16)
                }
            }
            positiveButton("立即下载") {
                downloadFirstPack()
            }
            noButton {  }
        }.show()
    }

    private fun downloadFirstPack() {
        if (mPackList.size <= 0) {
            return
        }
        longToast("开始下载 ${mPackList[0].owner} 的 ${mPackList[0].branchName} 分支下Git编号为 ${mPackList[0].gitNumber} 包")
        openLink("$URL_PREFIX${mPackList[0].link}")
    }

    inner class DownloadClickListenerImpl: MainAdapter.OnDownloadClickListener {
        override fun onClick(position: Int) {
            longToast("开始下载 ${mPackList[position].owner} 的 ${mPackList[position].branchName} 分支下Git编号为 ${mPackList[position].gitNumber} 包")
            openLink("$URL_PREFIX${mPackList[position].link}")
        }
    }
}
