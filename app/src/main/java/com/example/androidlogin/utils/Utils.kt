package com.example.androidlogin.utils

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import com.example.androidlogin.R
import com.example.androidlogin.databinding.LayoutCityDialogBinding
import com.example.androidlogin.databinding.LayoutLoadingBinding

class Utils {
    companion object {
        fun showLoading(context: Context): AlertDialog? {
            val mLoadingDialog = AlertDialog.Builder(context)
            val dialogBinding = DataBindingUtil.inflate<LayoutLoadingBinding>(
                LayoutInflater.from(context),
                R.layout.layout_loading, null, false
            )
            mLoadingDialog.setView(dialogBinding.root)
            val loading = mLoadingDialog.show()
            loading?.window?.setBackgroundDrawableResource(R.color.fui_transparent)
            return loading
        }
        fun showCityDialog(context: Context): AlertDialog? {
            val mLoadingDialog = AlertDialog.Builder(context)
            val dialogBinding = DataBindingUtil.inflate<LayoutCityDialogBinding>(
                LayoutInflater.from(context),
                R.layout.layout_city_dialog, null, false
            )
            mLoadingDialog.setView(dialogBinding.root)
            val loading = mLoadingDialog.show()
            loading?.window?.setBackgroundDrawableResource(R.color.fui_transparent)
            return loading
        }
    }
}