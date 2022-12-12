package com.example.roomwithaview

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth


class SignInActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var signIn: Button
    private lateinit var signUp: Button
    private lateinit var firebaseAuth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)
        val signInActivityTopBar = supportActionBar
        firebaseAuth = FirebaseAuth.getInstance()
        signInActivityTopBar?.title = "TODO App Sign In"
        email = findViewById(R.id.email)
        password = findViewById(R.id.password)
        signIn = findViewById(R.id.signIn)
        signUp = findViewById(R.id.signUp)

        signIn.setOnClickListener {
            if (email.text.isNotEmpty() && password.text.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(
                    email.text.toString(),
                    password.text.toString()
                ).addOnCompleteListener {
                    if (it.isSuccessful) {
                        val currentUser = firebaseAuth.currentUser
                        val uid: String = currentUser?.uid.toString()
                        val email: String = currentUser?.email.toString()
                        Log.d("email", currentUser?.email.toString())
                        Log.d("user id", currentUser?.uid.toString())
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("email", email)
                        intent.putExtra("uid", uid)
                        startActivity(intent)
//                        finish()
                    } else {
                        Toast.makeText(this, it.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        signUp.setOnClickListener {
            val intent = Intent(applicationContext, SignupActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = firebaseAuth.currentUser
    }
}