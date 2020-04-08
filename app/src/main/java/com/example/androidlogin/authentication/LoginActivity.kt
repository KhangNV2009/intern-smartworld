package com.example.androidlogin.authentication

import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ActivityLoginBinding
import com.example.androidlogin.home_navigation.HomeActivity
import com.example.androidlogin.model.user_model.UserModel
import com.firebase.ui.auth.AuthUI
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var mProviders : List<AuthUI.IdpConfig>
    private lateinit var mAuth: FirebaseAuth
    private var mDB = Firebase.firestore
    private val MY_REQUEST_CODE: Int = 7777

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        binding.lifecycleOwner = this

        mAuth = FirebaseAuth.getInstance()

        supportActionBar?.hide()

        binding.btLogin.setOnClickListener {
            if(binding.outlineTextfieldEmail.editText?.text.toString().trim().isEmpty() || binding.outlineTextfieldPassword.editText?.text.toString().isEmpty()) {
                Toast.makeText(baseContext, "Please enter Email or Password.",
                        Toast.LENGTH_SHORT)
                    .show()
            }
            mAuth.signInWithEmailAndPassword(binding.outlineTextfieldEmail.editText?.text.toString(), binding.outlineTextfieldPassword.editText?.text.toString())
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = mAuth.currentUser
                        updateUI(user)
                    } else {
                        Log.w("LoginActivity", "signInWithEmail:failure", task.exception)
                        Toast.makeText(baseContext, "Authentication failed.",
                            Toast.LENGTH_SHORT).show()
                        updateUI(null)
                    }
                }
        }
        binding.btRegister.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }
        binding.btGoogle.setOnClickListener {
            showSignInOptions()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val user = FirebaseAuth.getInstance().currentUser
                updateUI(user)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth.currentUser
        updateUI(currentUser)
    }

    override fun onResume() {
        super.onResume()
        Log.d("MainActivity","onResume")

    }

    override fun onPause() {
        super.onPause()
        Log.d("MainActivity","onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("MainActivity","onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("MainActivity","onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("MainActivity","onRestart")
    }

    private fun showSignInOptions() {
        mProviders = listOf(
            AuthUI.IdpConfig.GoogleBuilder().build()
        )
        startActivityForResult(
            AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(mProviders)
                .setTheme(R.style.AppTheme)
                .build(),
            MY_REQUEST_CODE)
    }

    private fun updateUI(currentUser: FirebaseUser?) {
        if(currentUser != null) {
            checkAccountIsExists(currentUser)
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
    private fun checkAccountIsExists(currentUser: FirebaseUser?) {
        mDB.collection("user").document(currentUser?.uid.toString())
            .get()
            .addOnSuccessListener { result ->
                if(result.exists()) {
                    Log.d("LoginActivity", "Account is exitst")
                } else {
                    addUserProfile(currentUser?.uid.toString())
                }
            }.addOnFailureListener { exception ->
                Log.w("ProfileFragment", "Error getting documents.", exception)
            }
    }
    private fun addUserProfile(userId: String) {
        val db = Firebase.firestore
        db.collection("user").document(userId)
            .set(mapOf(
                "user_phone" to null
                )
            )
            .addOnSuccessListener { Log.d("RegisterActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("RegisterActivity", "Error writing document", e) }
    }
}
