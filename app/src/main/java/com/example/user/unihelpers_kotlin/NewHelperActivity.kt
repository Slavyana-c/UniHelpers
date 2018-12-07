package com.example.user.unihelpers_kotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.xwray.groupie.GroupAdapter
import com.xwray.groupie.Item
import com.xwray.groupie.ViewHolder
import kotlinx.android.synthetic.main.activity_new_helper.*
import com.google.firebase.database.DatabaseReference
import kotlinx.android.parcel.Parcelize
import kotlinx.android.synthetic.main.activity_new_helper.view.*
import kotlinx.android.synthetic.main.user_row_new_helper.view.*


class NewHelperActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_helper)
        supportActionBar?.title = "Select Helper"
//        val adapter = GroupAdapter<ViewHolder>()
//
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//        adapter.add(UserItem())
//
//        newHelperRecyclerView.adapter = adapter


        fetchUsers()
    }

    companion object {
       val USER_KEY = "USER_KEY"
    }

    // Reads and displays users from databse
    private fun fetchUsers() {
        val reference = FirebaseDatabase.getInstance().getReference("/users")
        reference.addListenerForSingleValueEvent(object: ValueEventListener{
            override fun onDataChange(p0: DataSnapshot) {
                val adapter = GroupAdapter<ViewHolder>()

                // Go through the users
                p0.children.forEach{
                    val user= it.getValue(User::class.java)
                    if(user != null && user.helper) {
                        adapter.add(UserItem(user))
                    }
                    adapter.setOnItemClickListener { item, view ->

                        // When a row is clicked
                        val userItem = item as UserItem
                        val intent = Intent(view.context, ChatLogActivity::class.java)
                        intent.putExtra(USER_KEY, userItem.user)
                        startActivity(intent)
                        finish()
                    }
                }
                newHelperRecyclerView.adapter = adapter
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }
}

class UserItem(val user: User): Item<ViewHolder>() {
    override fun bind(viewHolder: ViewHolder, position: Int) {
        viewHolder.itemView.newHelperUsernameTextView.text = user.username
    }

    override fun getLayout(): Int {
        return R.layout.user_row_new_helper
    }
}

@Parcelize
class User(val uid: String, val username: String, val helper: Boolean):Parcelable {
    constructor() : this("", "", false)
}