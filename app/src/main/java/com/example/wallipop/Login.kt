package com.example.wallipop

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.wallipop.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth



class Login : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize Firebase Auth and SharedPreferences
        firebaseAuth = FirebaseAuth.getInstance()
        sharedPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE)

        val rememberMe = sharedPreferences.getBoolean("learn", false) // niche jo putboolean hai waha jo v value true or false store hui hogi yha automatically change ho jayegi

        // Check if "Remember Me" is enabled and user is logged in
        if (rememberMe && firebaseAuth.currentUser != null) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        // Login button click listener
        binding.loginBtn.setOnClickListener {
            val email = binding.loginEmail.text.toString().trim()
            val pass = binding.loginPassword.text.toString().trim()
            val isRememberMeChecked = binding.yadrakh.isChecked // if you ticked on check box it takes true but if it is not ticked it will store false

            if (email.isNotEmpty() && pass.isNotEmpty()) {
                firebaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
                    if (it.isSuccessful) {
                        // Save "Remember Me" state in SharedPreferences
                        val editor = sharedPreferences.edit()
                        editor.putBoolean("learn", isRememberMeChecked) // isremeberchecked store either true or false
                        editor.apply()

                        // Start MainActivity
                        val userId = firebaseAuth.currentUser?.uid
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("USER_ID", userId)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this,"Login failed", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this, "Fields cannot be empty", Toast.LENGTH_SHORT).show()
            }
        }
        // Redirect to Sign-up activity
        binding.signupRedirect.setOnClickListener {
            val intent = Intent(this, Signup_in::class.java)
            startActivity(intent)
        }
    }
}





