package com.example.androidlogin.home_navigation.user_profile

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.androidlogin.R
import com.example.androidlogin.authentication.LoginActivity
import com.example.androidlogin.databinding.FragmentProfileBinding
import com.example.androidlogin.home_navigation.HomeActivity
import com.example.androidlogin.home_navigation.weather_detail.WeatherDetailFragment
import com.example.androidlogin.model.user_model.UserModel
import com.example.androidlogin.utils.Utils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.ktx.storage
import com.vansuita.pickimage.bundle.PickSetup
import com.vansuita.pickimage.dialog.PickImageDialog
import retrofit2.Callback
import java.io.File


class ProfileFragment : Fragment() {

    private var mActivity : HomeActivity? = null
    private val mUser = FirebaseAuth.getInstance().currentUser
    private var mUserPhone: String? = null

    private lateinit var binding: FragmentProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        dataBinding(container)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        cloudFirebase()
        updateUserInfo()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivEditImage.setOnClickListener {
            PickImageDialog.build(PickSetup()).setOnPickResult { result ->
                mActivity?.binding?.viewPager?.currentItem = 1
                uploadImage(result.path)
               binding.rlLoading.visibility = View.VISIBLE
            }.setOnPickCancel { }.show(activity!!.supportFragmentManager)
        }
        binding.tvLogout.setOnClickListener {

            val dialogBuiler = AlertDialog.Builder(context!!)
            dialogBuiler
                .setMessage("")
                .setPositiveButton("Đăng xuất", DialogInterface.OnClickListener{
                        dialog, id -> FirebaseAuth.getInstance().signOut()
                    activity!!.finish()
                    startActivity(Intent(context!!, LoginActivity::class.java))
                }).setNegativeButton("Hủy", DialogInterface.OnClickListener { dialog, which ->  })
                .setTitle("Đăng xuất khỏi Weather app?")
                .create()
                .show()
        }

        binding.ivUpdate.setOnClickListener {
            val intent = Intent(view.context, EditActivity::class.java)
            intent.putExtra("displayName", mUser?.displayName)
            intent.putExtra("phoneNumber", mUserPhone)
            startActivity(intent)
        }

        cloudFirebase()
    }

    private fun dataBinding(container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
                LayoutInflater.from(context),
                R.layout.fragment_profile,
                container,
                false
        )
    }

    private fun updateUserInfo() {
        binding.tvName.text = mUser?.displayName
        binding.tvEmail.text = mUser?.email

        if(mUserPhone.isNullOrEmpty()) {
            binding.tvPhone.text = "not available"
        } else {
            binding.tvPhone.text = mUserPhone
        }
        Glide.with(view!!.context).load("${mUser?.photoUrl}").error(R.drawable.avatar).into(binding.ivAvatar)
        binding.rlLoading.visibility = View.GONE
    }

    private fun uploadImage(image: String) {
        val storage = Firebase.storage("gs://kotlinweatherapp-986b6.appspot.com")
        val storageRef = storage.reference
        val file = Uri.fromFile(File(image))
        val imageRef = storageRef.child("images/${file.lastPathSegment}")
        val uploadTask = imageRef.putFile(file)

        uploadTask.addOnFailureListener {
            Log.d("UploadImage", "Upload Fail")
        }.addOnSuccessListener {
            Log.d("UploadImage", "Upload Successful")
        }.continueWithTask {task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            imageRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d("UploadImage", "Url: $downloadUri")
                updateUserAvatar(downloadUri.toString())
            }
        }

    }

    private fun updateUserAvatar(url: String) {
        val user = FirebaseAuth.getInstance().currentUser
        val profileUpdates = UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(url))
                .build()

        user?.updateProfile(profileUpdates)
                ?.addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("UploadImage", "Image updated")
                        updateUserInfo()
                    }
                }?.addOnFailureListener {
                Toast.makeText(context,it.message,Toast.LENGTH_SHORT).show()
            }
    }

    @SuppressLint("SetTextI18n")
    private fun cloudFirebase() {
        val db = Firebase.firestore

        db.collection("user").document(mUser?.uid.toString())
                .get()
                .addOnSuccessListener { result ->
                    var user = result.toObject(UserModel::class.java)
                    mUserPhone = user?.user_phone

                    if(mUserPhone.isNullOrEmpty()) {
                        binding.tvPhone.text = "not available"
                    } else {
                        binding.tvPhone.text = user?.user_phone
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("ProfileFragment", "Error getting documents.", exception)
                }
    }

    companion object {
        fun newInstance(mActivity : HomeActivity) : ProfileFragment {
            val fragment = ProfileFragment()
            fragment.mActivity = mActivity
            return fragment
        }
    }
}
