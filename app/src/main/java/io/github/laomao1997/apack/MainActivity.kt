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
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.Composable
import androidx.compose.Model
import androidx.compose.unaryPlus
import androidx.core.view.marginBottom
import androidx.ui.core.Text
import androidx.ui.core.dp
import androidx.ui.core.setContent
import androidx.ui.foundation.VerticalScroller
import androidx.ui.graphics.Color
import androidx.ui.layout.*
import androidx.ui.material.*
import com.livinglifetechway.quickpermissions_kotlin.runWithPermissions
import org.jetbrains.anko.*

class MainActivity : AppCompatActivity(), MainContract.View {

    companion object {
        private const val URL_PREFIX = ""
    }

    private lateinit var mPresenter: MainContract.Presenter
    private lateinit var vibrator: Vibrator
    private var onFirstRequest = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NewStory()
        }

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
        DataState.count = packList.size
        if (onFirstRequest) {
            onFirstRequest = false
        } else {
            // 当返回的数据不一样
            if (DataState.mPackList[0].gitNumber != packList[0].gitNumber &&
                DataState.mPackList.size != packList.size &&
                !onFirstRequest
            ) {
                sendNotification()
            }
        }
        DataState.mPackList.clear()
        DataState.mPackList.addAll(packList)
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
            R.id.refresh -> {
                makeToast("刷新")
                mPresenter.getData()
//                sendNotification()
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
                    if (DataState.mPackList.size > 0) {
                        textView("时间：${DataState.mPackList[0].time}")
                        textView("分支：${DataState.mPackList[0].branchName}")
                        textView("作者：${DataState.mPackList[0].owner}")
                        textView("Git号：${DataState.mPackList[0].gitNumber}")
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
        if (DataState.mPackList.size <= 0) {
            return
        }
        openLink("$URL_PREFIX${DataState.mPackList[0].link}")
    }

    @Composable
    fun NewStory() {
        MaterialTheme() {
            VerticalScroller {
                Column(crossAxisAlignment = CrossAxisAlignment.Center) {
                    (0 until DataState.count).forEachIndexed { index, _ ->
                        createListItem(index)
                        Divider(color = Color.Blue, height = 1.dp)
                    }
                    Spacing(5.dp)
                    Text(
                        text = "Developed by Zhang Jinghao",
                        style = (+themeTextStyle { body1 }).withOpacity(0.3f)
                    )
                    Text(
                        text = "如有BUG及可改进之处烦请告知我以修改",
                        style = (+themeTextStyle { body1 }).withOpacity(0.3f)
                    )
                    Spacing(5.dp)
                }
            }
        }
    }

    @Composable
    private fun createListItem(itemIndex: Int) {
        Padding(padding = 8.dp) {
            FlexRow(crossAxisAlignment = CrossAxisAlignment.Center) {
                expanded(1.0f) {
                    Column() {
                        Text(
                            text = DataState.mPackList[itemIndex].time,
                            style = (+themeTextStyle { body1 }).withOpacity(0.6f)
                        )
                        Text(
                            text = DataState.mPackList[itemIndex].branchName,
                            style = +themeTextStyle { h6 }
                        )
                        Text(
                            text = "Owner: ${DataState.mPackList[itemIndex].owner}",
                            style = (+themeTextStyle { body1 }).withOpacity(0.87f)
                        )
                        Text(
                            text = "Git: ${DataState.mPackList[itemIndex].gitNumber}",
                            style = (+themeTextStyle { body2 }).withOpacity(0.6f)
                        )
                    }
                }
                inflexible {
                    Button(
                        "下载",
                        style = ContainedButtonStyle(),
                        onClick = {
                            openLink("$URL_PREFIX${DataState.mPackList[itemIndex].link}")
                        })
                }
            }
        }
    }
}

@Model
object DataState {
    var count: Int = 0

    var mPackList = ArrayList<PackBean>()
}
