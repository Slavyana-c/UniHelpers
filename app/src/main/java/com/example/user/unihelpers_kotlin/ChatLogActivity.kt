package com.example.user.unihelpers_kotlin

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_chat_log.*
import kotlinx.android.synthetic.main.chat_from_row.view.*
import kotlinx.android.synthetic.main.chat_to_row.view.*

class ChatLogActivity : AppCompatActivity() {

    val adapter = GroupAdapter<ViewHolder>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_log)

       // val username = intent.getStringExtra(NewHelperActivity.USER_KEY)
        val user = intent.getParcelableExtra<User>(NewHelperActivity.USER_KEY)
        supportActionBar?.title = user.username

        chatLogRecyclerView.adapter = adapter
        //setupDummyData()

        listenForMessages()

        // Make send button functional
        sendButtonChatLog.setOnClickListener {
            //Toast.makeText(this, "Send pressed", Toast.LENGTH_SHORT).show()
            sendMessage()
        }
    }

    class ChatMessage(val id: String, val text: String, val fromId: String, val toId: String, val timeStamp: Long) {
        constructor(): this("", "", "", "", -1)
    }

    // Listening for new messages
    private fun listenForMessages() {
        val fromId = FirebaseAuth.getInstance().uid
        val toId = intent.getParcelableExtra<User>(NewHelperActivity.USER_KEY).uid

        val ref = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId")
        ref.addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(p0: DataSnapshot, p1: String?) {
                val chatMessage = p0.getValue(ChatMessage::class.java)

                if(chatMessage != null) {

                    if(chatMessage.fromId == fromId) {
                        adapter.add(ChatToItem(chatMessage.text))
                    }
                    else {
                        adapter.add(ChatFromItem(chatMessage.text))
                    }
                }

                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }

            override fun onChildMoved(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildChanged(p0: DataSnapshot, p1: String?) {
            }

            override fun onChildRemoved(p0: DataSnapshot) {
            }

            override fun onCancelled(p0: DatabaseError) {

            }
        })
    }


    // Send message to Firebase
    private fun sendMessage() {

        val text = chatLogEditText.text.toString()

        // Signed in user
        val fromId = FirebaseAuth.getInstance().uid

        // Other user
        val toId =  intent.getParcelableExtra<User>(NewHelperActivity.USER_KEY).uid

        val reference = FirebaseDatabase.getInstance().getReference("/user-messages/$fromId/$toId").push()
        val toReference = FirebaseDatabase.getInstance().getReference("/user-messages/$toId/$fromId").push()

        if(fromId == null) return
        val chatMessage = ChatMessage(reference.key!!, text, fromId, toId, System.currentTimeMillis() / 1000)

        reference.setValue(chatMessage)
            .addOnSuccessListener {
                chatLogEditText.text.clear() // Clear text when message is sent
                chatLogRecyclerView.scrollToPosition(adapter.itemCount - 1)
            }
        toReference.setValue(chatMessage)

        val latestMessageRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$fromId/$toId")
        latestMessageRef.setValue(chatMessage)

        val latestMessageToRef = FirebaseDatabase.getInstance().getReference("/latest-messages/$toId/$fromId")
        latestMessageToRef.setValue(chatMessage)
    }

    // Setup some dummy data for testing
    private fun setupDummyData() {
        val adapter = GroupAdapter<ViewHolder>()

        adapter.add(ChatFromItem("This is my first message which should be the longest"))
        adapter.add(ChatToItem("second"))
        adapter.add(ChatFromItem("Yeah, third, exactly."))

        chatLogRecyclerView.adapter = adapter
    }
}

// Chat from other users
class ChatFromItem(val text: String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.fromRowTextView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_from_row
    }
}

// Chat to other users
class ChatToItem(val text:String): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.toRowTextView.text = text
    }

    override fun getLayout(): Int {
        return R.layout.chat_to_row
    }
}