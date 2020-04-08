package com.example.androidlogin.home_navigation.user_profile

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ActivityEditBinding
import com.example.androidlogin.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class EditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditBinding
    val mUser = FirebaseAuth.getInstance().currentUser
    private var mDisplayName: String? = null
    private var mPhoneNumber: String? = null

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_button_action_bar, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.item_edit) {
            updateUserName(binding.outlineTextfieldDisplayName.editText?.text.toString())
            updatePhoneNumber(binding.outlineTextfieldPhoneNumber.editText?.text.toString())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_edit)

        binding.lifecycleOwner = this

        mDisplayName = intent.getStringExtra("displayName")
        mPhoneNumber = intent.getStringExtra("phoneNumber")

        binding.outlineTextfieldDisplayName.editText?.setText(mDisplayName)
        binding.outlineTextfieldPhoneNumber.editText?.setText(mPhoneNumber)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }
    private fun updateUserName(username: String) {
        val profileUpdates = UserProfileChangeRequest.Builder()
            .setDisplayName(username)
            .build()
         val loading = Utils.showLoading(this)
        mUser?.updateProfile(profileUpdates)
            ?.addOnCompleteListener {task ->
                loading?.dismiss()
                if(task.isSuccessful) {
                }
            }?.addOnFailureListener {
                    loading?.dismiss()
                Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
            }
    }
    private fun updatePhoneNumber(phoneNumber: String) {
        val db = Firebase.firestore
        db.collection("user").document(mUser?.uid.toString())
            .update(mapOf(
                "user_phone" to phoneNumber
            ))
            .addOnSuccessListener {
                val dialogBuiler = AlertDialog.Builder(this)
                dialogBuiler.setMessage("Edit Profile successful")
                    .setPositiveButton("Ok", DialogInterface.OnClickListener{
                            dialog, id -> finish()
                    })
                    .setTitle("Congratulation")
                    .create()
                    .show()
                Log.d("EditActivity", "DocumentSnapshot successfully written!")
            }
            .addOnFailureListener { e -> Log.w("EditActivity", "Error writing document", e) }
    }
}