package io.github.laomao1997.apack

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import io.github.laomao1997.apack.Constant.TAG

class MainAdapter: RecyclerView.Adapter<PackViewHolder>() {

    private lateinit var mDownloadClickListener: OnDownloadClickListener

    private var mPackList = ArrayList<PackBean>()

    fun setData(packList: ArrayList<PackBean>?) {
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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PackViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_pack, parent, false)
        return PackViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "${mPackList.size}")
        return mPackList.size
    }

    override fun onBindViewHolder(holder: PackViewHolder, position: Int) {
        holder.tvTime.text = mPackList[position].time
        holder.tvBranch.text = mPackList[position].branchName
        holder.tvOwner.text = mPackList[position].owner
        holder.tvGit.text = mPackList[position].gitNumber

        holder.btnDownload.setOnClickListener {
            mDownloadClickListener.onClick(position)
        }
    }

    fun setOnDownloadClickListener(listener: OnDownloadClickListener) {
        this.mDownloadClickListener = listener
    }

    interface OnDownloadClickListener {
        fun onClick(position: Int)
    }

}