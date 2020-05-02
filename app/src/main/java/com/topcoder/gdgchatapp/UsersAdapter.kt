package com.topcoder.gdgchatapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.users_item.view.*

class UsersAdapter(private val userList: List<User>, val onClick: (User)->Unit): RecyclerView.Adapter<UsersAdapter.UsersViewHolder>() {

    class UsersViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder  =
        UsersViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.users_item, parent, false))

    override fun getItemCount(): Int = userList.size

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        var initials = if(userList[position].firstName.isNotEmpty())userList[position].firstName[0].toUpperCase()+"" else ""
        initials += if(userList[position].lastName.isNotEmpty())userList[position].lastName[0].toUpperCase()+"" else ""
        holder.itemView.initials_tv.text = if(initials.isNotEmpty())initials else "?"
        holder.itemView.name_tv.text = userList[position].firstName+" "+userList[position].lastName
        holder.itemView.setOnClickListener {
            onClick(userList[position])
        }
    }
}