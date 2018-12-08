package com.example.user.unihelpers_kotlin

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_main.*

// Main Login Activity
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.title = "UniHelpers"
        loginButton.setOnClickListener {
            performLogin()
        }
    }

    // Function performed when login is pressed
    private fun performLogin() {
        val email = emailEditText.text.toString()
        val password = passwordEditText.text.toString()

        // Checks if either field is empty
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
            return
        }

        //Firebase authentication of email and password
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(!it.isSuccessful) return@addOnCompleteListener
                else Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()

                // Take user to LatestMessagesActivity
                val intent = Intent(this, LatestMessagesActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK.or(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            .addOnFailureListener {

                // Show error message
                Toast.makeText(this, "${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
