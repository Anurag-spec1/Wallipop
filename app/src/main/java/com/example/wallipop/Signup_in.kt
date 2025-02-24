package com.example.wallipop

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.wallipop.databinding.ActivitySignupInBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Signup_in : AppCompatActivity() {

    private lateinit var binding: ActivitySignupInBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var databaseReference: DatabaseReference
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupInBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        sharedPreferences = getSharedPreferences("wallipopPrefs", MODE_PRIVATE)
        firebaseAuth = FirebaseAuth.getInstance()
        databaseReference= FirebaseDatabase.getInstance().getReference("Users")
        val uid = firebaseAuth.currentUser?.uid

        var dialog = ProgressDialog(this)
        dialog.setTitle("loading")

        binding.signupBtn.setOnClickListener{
            val email = binding.signupEmail.text.toString().trim()
            val pass = binding.signupPassword.text.toString().trim()
            val conpass = binding.signupConfirmpass.text.toString().trim()

            dialog.show()

            if(email.isNotEmpty() && pass.isNotEmpty() && conpass.isNotEmpty()){
                if (pass==conpass){
                    firebaseAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener{
                        if (it.isSuccessful){
                            val intent = Intent(this, Login::class.java)
                            startActivity(intent)
                        } else{
                            Toast.makeText(this,it.exception.toString(), Toast.LENGTH_SHORT).show()
                        }
                    }
                    dialog.dismiss()
                } else{
                    binding.signupPassword.setError("password is incorrect")
                    Toast.makeText(this,"Password is Incorrect", Toast.LENGTH_SHORT).show()
                }
            }else{
                Toast.makeText(this,"Password can not be empty", Toast.LENGTH_SHORT).show()
            }
        }
        binding.loginRedirect.setOnClickListener{
            val loginintent = Intent(this, Login::class.java)
            startActivity(loginintent)
        }
    }
}
