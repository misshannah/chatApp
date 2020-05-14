package com.topcoder.gdgchatapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_chat.*
import kotlinx.android.synthetic.main.chat_item.*
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {
    private lateinit var receiverId: String
    var chatId: String? = null
    var receiverName: String? = null
    var receiverToken: String? = null
    var userToken: String? = null
    var msgTime: String? = null
    private lateinit var auth: FirebaseAuth
    lateinit var database: FirebaseDatabase
    private lateinit var adapter: ChatAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        getIntentExtras()
        setChat()
        setSendListener()
    }

    private fun getIntentExtras() {
        receiverName = intent.getStringExtra(Constants.INTENT_KEY_RECIPIENT_NAME)
        supportActionBar?.title = receiverName
        receiverToken = intent.getStringExtra(Constants.INTENT_KEY_RECIPIENT_TOKEN)
        userToken = intent.getStringExtra(Constants.INTENT_KEY_USER_TOKEN)
        receiverId = intent.getStringExtra(Constants.INTENT_KEY_RECIPIENT_ID)?:""
        if (auth.currentUser != null && auth.currentUser!!.uid.isNotEmpty()) {
            chatId = if (receiverId[0] > auth.currentUser!!.uid[0]) receiverId + auth.currentUser!!.uid else auth.currentUser!!.uid + receiverId
            chat_rv.layoutManager = LinearLayoutManager(this)
            adapter = ChatAdapter(auth.currentUser!!.uid)
            chat_rv.adapter = adapter
        }
    }

    private fun setSendListener() {
        send_tv.setOnClickListener {

            val calendar: Calendar = Calendar.getInstance()
            val simpleDateFormat = SimpleDateFormat("h:mm a")
            val currentTimeStamp = simpleDateFormat.format(calendar.getTime())

            chatId?.let {
                if (message_et.text.isNotEmpty()) {
                    val newRef: DatabaseReference =
                        database.reference.child("Messages").child(it).push()
                    val message = Message().apply {
                        text = message_et.text.toString()
                        receiverId = this@ChatActivity.receiverId
                        receiverToken = this@ChatActivity.receiverToken ?: ""
                        senderId = auth.currentUser?.uid ?: ""
                        senderName = auth.currentUser?.displayName ?: ""
                        senderToken = this@ChatActivity.userToken ?: ""
                        msgTime = currentTimeStamp ?: ""

                    }
                    newRef.setValue(message)
                    message_et.setText("")
                }
            }

        }
    }

    private fun setChat() {
        chatId?.let {
            database.reference.child("Messages").child(it).addValueEventListener(object: ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    val messageList = mutableListOf<Message>()
                    for (data in datasnapshot.getChildren()) {
                        messageList.add(data.getValue(Message::class.java)?: Message())
                    }
                    adapter.setMessages(messageList)
                    chat_rv.scrollToPosition(messageList.size - 1);
                }

            })
        }
    }
}
