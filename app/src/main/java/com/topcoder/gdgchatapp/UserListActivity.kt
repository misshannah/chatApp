package com.topcoder.gdgchatapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_user_list.*


class UserListActivity : AppCompatActivity() {
    private lateinit var database: FirebaseDatabase
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_list)
        supportActionBar?.title = "Your Contacts"
        database = FirebaseDatabase.getInstance()
        getUsers()
    }

    private fun getUsers() {
        database.reference.child("Users").addValueEventListener(object: ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                val userList = mutableListOf<User>()
                for (data in datasnapshot.getChildren()) {
                    val user = data.getValue(User::class.java)?:User()
                    if (user.uid != FirebaseAuth.getInstance().currentUser?.uid)
                        userList.add(user)
                }
                setupRv(userList)
            }

        })
    }

    private fun setupRv(userList: MutableList<User>) {
        user_rv.layoutManager = LinearLayoutManager(this)
        user_rv.adapter = UsersAdapter(userList){
            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra(Constants.INTENT_KEY_RECIPIENT_NAME, it.firstName+" "+it.lastName)
            intent.putExtra(Constants.INTENT_KEY_RECIPIENT_ID, it.uid)
            intent.putExtra(Constants.INTENT_KEY_RECIPIENT_TOKEN, it.token)
            intent.putExtra(Constants.INTENT_KEY_USER_TOKEN, intent.getStringExtra(Constants.INTENT_KEY_USER_TOKEN))
            startActivity(intent)
        }
    }
}
