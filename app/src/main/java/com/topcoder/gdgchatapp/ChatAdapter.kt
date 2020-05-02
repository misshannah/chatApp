package com.topcoder.gdgchatapp

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.chat_item.view.*

class ChatAdapter(val uid: String): RecyclerView.Adapter<ChatAdapter.ChatViewHolder>() {
    private var messages = mutableListOf<Message>()
    fun setMessages(messages: MutableList<Message>){
        this.messages = messages
        notifyDataSetChanged()
    }
    class ChatViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder =
        ChatViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.chat_item, parent, false))

    override fun getItemCount(): Int = messages.size

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        holder.itemView.message_tv.text = messages[position].text

        if(messages[position].senderId == uid){
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                gravity = Gravity.END
            }
            holder.itemView.message_tv.setBackground(ContextCompat.getDrawable(holder.itemView.context, R.drawable.outgoing_bubble));
            holder.itemView.message_tv.layoutParams = params
            holder.itemView.message_tv.setPadding(calculateDp(8, holder.itemView),calculateDp(5, holder.itemView),
                calculateDp(24, holder.itemView),calculateDp(5, holder.itemView))
        } else {
            val params = FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
                .apply {
                    gravity = Gravity.START
                }
            holder.itemView.message_tv.setBackground(ContextCompat.getDrawable(holder.itemView.context, R.drawable.incoming_bubble));
            holder.itemView.message_tv.layoutParams = params
            holder.itemView.message_tv.setPadding(calculateDp(24, holder.itemView),calculateDp(5, holder.itemView),
                calculateDp(8, holder.itemView),calculateDp(5, holder.itemView))
        }
    }

    private fun calculateDp(pixel: Int, view: View): Int{
        val scale: Float = view.context.resources.displayMetrics.density
        return (pixel * scale + 0.5f).toInt()
    }
}