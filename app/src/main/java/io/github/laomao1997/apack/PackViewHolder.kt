package io.github.laomao1997.apack

import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class PackViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

    val tvTime: TextView = itemView.findViewById(R.id.tv_time)
    val tvBranch: TextView = itemView.findViewById(R.id.tv_branch)
    val tvOwner: TextView = itemView.findViewById(R.id.tv_owner)
    val tvGit: TextView = itemView.findViewById(R.id.tv_git)
    val ivDownload: ImageView = itemView.findViewById(R.id.iv_download)

}