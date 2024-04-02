package com.example.learngerman.utils

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.learngerman.R
import com.tonyodev.fetch2.Fetch
import kotlinx.android.synthetic.main.single_message_item.view.*


class MessagesRecylerviewAdapter(var tumMesajlar: ArrayList<MessagesModel>, var myContext: Context) :
    RecyclerView.Adapter<MessagesRecylerviewAdapter.MyMessagesViewHolder>() {

    private var fetch: Fetch? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessagesViewHolder {
        var view =
            LayoutInflater.from(myContext).inflate(R.layout.single_message_item, parent, false)
        return MyMessagesViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tumMesajlar.size
    }

    override fun onBindViewHolder(holder: MyMessagesViewHolder, position: Int) {
        holder.setData(tumMesajlar.get(position))
    }

    class MyMessagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tumLayout = itemView as ConstraintLayout
        var Notimage = tumLayout.tvSingleMessageLine

        fun setData(oankiNotlar: MessagesModel) {
            itemView.tvSingleMessageLine.text = oankiNotlar.message
        }
    }
}