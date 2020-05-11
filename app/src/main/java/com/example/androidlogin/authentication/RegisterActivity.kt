package com.example.androidlogin.authentication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ActivityRegisterBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_register)

        supportActionBar?.title = "Register"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.outlineTextfieldUsername.editText?.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.outlineTextfieldUsername.editText?.text.toString().isNotEmpty()) {
                    binding.outlineTextfieldUsername.setEndIconDrawable(R.drawable.correct)
                } else {
                    binding.outlineTextfieldUsername.setEndIconDrawable(R.drawable.incorrect)
                }
            }

            override fun afterTextChanged(s: Editable?) {
                if(binding.outlineTextfieldUsername.editText?.text.toString().isNotEmpty()) {
                    binding.outlineTextfieldUsername.setEndIconDrawable(R.drawable.correct)
                } else {
                    binding.outlineTextfieldUsername.setEndIconDrawable(R.drawable.incorrect)
                }
            }
        })

        binding.outlineTextfieldEmail.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isEmailValid(binding.outlineTextfieldEmail.editText?.text.toString())) {
                    binding.outlineTextfieldEmail.setEndIconDrawable(R.drawable.correct)
                } else {
                    binding.outlineTextfieldEmail.setEndIconDrawable(R.drawable.incorrect)
                }
            }
        })

        binding.outlineTextfieldPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(isPasswordValid(binding.outlineTextfieldPassword.editText?.text.toString())) {
                    binding.outlineTextfieldPassword.setEndIconDrawable(R.drawable.correct)
                }
                else {
                    binding.outlineTextfieldPassword.setEndIconDrawable(R.drawable.incorrect)
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(isPasswordValid(binding.outlineTextfieldPassword.editText?.text.toString())) {
                    binding.outlineTextfieldPassword.setEndIconDrawable(R.drawable.correct)
                }
                else {
                    binding.outlineTextfieldPassword.setEndIconDrawable(R.drawable.incorrect)
                }
            }
        })

        binding.outlineTextfieldConfirmPassword.editText?.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(binding.outlineTextfieldPassword.editText?.text.toString() == binding.outlineTextfieldConfirmPassword.editText?.text.toString() && isPasswordValid(binding.outlineTextfieldPassword.editText?.text.toString())) {
                    binding.outlineTextfieldConfirmPassword.setEndIconDrawable(R.drawable.correct)
                }
                else {
                    binding.outlineTextfieldConfirmPassword.setEndIconDrawable(R.drawable.incorrect)
                }
            }
        })

        binding.btRegister.setOnClickListener {
            if(binding.outlineTextfieldUsername.editText?.text.toString().isNotEmpty()) {
                if(isEmailValid(binding.outlineTextfieldEmail.editText?.text.toString())) {
                    if(isPasswordValid(binding.outlineTextfieldPassword.editText?.text.toString())) {
                        if(binding.outlineTextfieldPassword.editText?.text.toString() == binding.outlineTextfieldConfirmPassword.editText?.text.toString() && isPasswordValid(binding.outlineTextfieldPassword.editText?.text.toString())) {
                            var intent = Intent(this, LoginActivity::class.java)

                            FirebaseAuth.getInstance().createUserWithEmailAndPassword(binding.outlineTextfieldEmail.editText?.text.toString(), binding.outlineTextfieldPassword.editText?.text.toString())
                                .addOnCompleteListener {
                                    if(!it.isSuccessful) return@addOnCompleteListener
                                    val profileUpdates = UserProfileChangeRequest.Builder()
                                        .setDisplayName(binding.outlineTextfieldUsername.editText?.text.toString())
                                        .build()
                                    it.result?.user?.updateProfile(profileUpdates)
                                    addUserProfile(it.result?.user?.uid.toString())
                                    Log.d("RegisterActivity", "Successfully created user with uid: ${it.result?.user?.uid}")
                                }
                            startActivity(intent)
                        }
                        else {
                            binding.outlineTextfieldConfirmPassword.editText?.showKeyboard()
                            showToast("Confirm password does not match")
                        }
                    }
                    else {
                        binding.outlineTextfieldPassword.editText?.showKeyboard()
                        showToast("Password must be at least 6 characters")
                    }
                }
                else {
                    binding.outlineTextfieldEmail.editText?.showKeyboard()
                    showToast("Invalid email")
                }
            }
            else {
                binding.outlineTextfieldUsername.editText?.showKeyboard()
                showToast("Please input username")
            }
        }
    }

    private fun addUserProfile(userId: String) {
        val db = Firebase.firestore
        db.collection("user").document(userId)
            .set(mapOf(
                "user_phone" to null,
                "user_lat" to null,
                "user_long" to null
                )
            )
            .addOnSuccessListener { Log.d("RegisterActivity", "DocumentSnapshot successfully written!") }
            .addOnFailureListener { e -> Log.w("RegisterActivity", "Error writing document", e) }
    }

    fun isEmailValid(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 6
    }

    private fun showToast(text: String) {
        val myToast = Toast.makeText(this,text,Toast.LENGTH_SHORT)
        myToast.setGravity(Gravity.TOP,0,80)
        myToast.show()
    }

    private fun EditText.showKeyboard() {
        post {
            requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(this, InputMethodManager.SHOW_IMPLICIT)
        }
    }
}