package com.example.androidlogin.home_navigation.google_map

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import com.example.androidlogin.R
import com.example.androidlogin.databinding.ActivityBottomBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import kotlinx.android.synthetic.main.activity_bottom.*
import kotlinx.android.synthetic.main.bottom_sheet_layout.*

class BottomActivity : AppCompatActivity(), View.OnClickListener {

    private lateinit var binding: ActivityBottomBinding
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<CoordinatorLayout>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bottom)
        initView()
    }

    private fun initView() {
        buttonSlideUp.setOnClickListener(this)

        textViewFacebook.setOnClickListener {
            Toast.makeText(this, "Facebook", Toast.LENGTH_SHORT).show()
        }
        textViewTwitter.setOnClickListener {
            Toast.makeText(this, "Twitter", Toast.LENGTH_SHORT).show()
        }
        textViewInstagram.setOnClickListener {
            Toast.makeText(this, "Instagram", Toast.LENGTH_SHORT).show()
        }
        textViewLinkedin.setOnClickListener {
            Toast.makeText(this, "Linkedin", Toast.LENGTH_SHORT).show()
        }
        bottomSheetBehavior = BottomSheetBehavior.from<CoordinatorLayout>(bottomSheet)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onSlide(bottomSheet: View, slideOffset: Float) {

            }

            @SuppressLint("SetTextI18n")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_COLLAPSED -> {
                        buttonSlideUp.text = "Slide Up"
                    }
                    BottomSheetBehavior.STATE_HIDDEN -> {

                    }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        buttonSlideUp.text = "Slide Down"
                    }
                    BottomSheetBehavior.STATE_DRAGGING -> {

                    }
                    BottomSheetBehavior.STATE_SETTLING -> {

                    }
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        TODO()
                    }
                }
            }
        })
    }

    override fun onClick(clickView: View?) {
        when (clickView) {
            buttonSlideUp -> {
                slideUpDownBottomSheet()
            }
        }
    }

    /***
     * Manually Slide up and Slide Down
     */
    private fun slideUpDownBottomSheet() {
        if (bottomSheetBehavior.state != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
            buttonSlideUp.text = "Slide Down";
        } else {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED;
            buttonSlideUp.text = "Slide Up"
        }
    }
}
