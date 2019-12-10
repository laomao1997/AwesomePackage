package io.github.laomao1997.apack

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.laomao1997.apack.Constant.TAG
import io.github.laomao1997.apack.DateUtil.getChinaDateTime

class MainAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var mDownloadClickListener: OnDownloadClickListener

    private var mPackList = ArrayList<PackBean>()

    private var expandedItemCount = 0

    private lateinit var mContext: Context

    fun setData(context: Context, packList: ArrayList<PackBean>?) {
        this.mContext = context
        packList?.let {
            this.mPackList = it
            Log.d(TAG, mPackList.toString())
            notifyDataSetChanged()
        }
    }

    fun updateData(novaList: ArrayList<PackBean>) {
        this.mPackList = novaList
        Log.d(TAG, mPackList.toString())
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            0 -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pack, parent, false)
                PackViewHolder(itemView)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_bottom, parent, false)
                BottomViewHolder(itemView)
            }
        }
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "${mPackList.size}")
        return mPackList.size + expandedItemCount + 1
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position < mPackList.size) {
            holder as PackViewHolder
            holder.tvTime.text = getChinaDateTime(mContext, mPackList[position].time)
            holder.tvBranch.text = mPackList[position].branchName
            holder.tvOwner.text = mPackList[position].owner
            holder.tvGit.text = mPackList[position].gitNumber.replace("....", "……")

            holder.ivDownload.setOnClickListener {
                mDownloadClickListener.onClick(position)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(position < mPackList.size + expandedItemCount) {
            true -> 0
            else -> 1
        }
    }

    fun setOnDownloadClickListener(listener: OnDownloadClickListener) {
        this.mDownloadClickListener = listener
    }

    interface OnDownloadClickListener {
        fun onClick(position: Int)
    }

}