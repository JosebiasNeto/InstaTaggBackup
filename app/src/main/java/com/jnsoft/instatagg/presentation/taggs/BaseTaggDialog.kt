package com.jnsoft.instatagg.presentation.taggs

import android.app.AlertDialog
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.jnsoft.instatagg.databinding.DialogCreateEditTaggBinding

open class BaseTaggDialog : DialogFragment() {

    protected fun changeColor(binding: DialogCreateEditTaggBinding, color: Int) {
        binding.btnChoseColor.setBackgroundColor(color)
        binding.btnChoseColor.setHintTextColor(color)
        binding.cvChoseColor.visibility = View.GONE
    }

    protected fun choseColor(binding: DialogCreateEditTaggBinding) {
        binding.cvChoseColor.visibility = View.VISIBLE
        binding.cvChoseColor.setOnClickListener {  }
        binding.cvColor1.setOnClickListener { changeColor(binding, binding.cvColor1.cardBackgroundColor.defaultColor) }
        binding.cvColor2.setOnClickListener { changeColor(binding, binding.cvColor2.cardBackgroundColor.defaultColor) }
        binding.cvColor3.setOnClickListener { changeColor(binding, binding.cvColor3.cardBackgroundColor.defaultColor) }
        binding.cvColor4.setOnClickListener { changeColor(binding, binding.cvColor4.cardBackgroundColor.defaultColor) }
        binding.cvColor5.setOnClickListener { changeColor(binding, binding.cvColor5.cardBackgroundColor.defaultColor) }
        binding.cvColor6.setOnClickListener { changeColor(binding, binding.cvColor6.cardBackgroundColor.defaultColor) }
        binding.cvColor7.setOnClickListener { changeColor(binding, binding.cvColor7.cardBackgroundColor.defaultColor) }
        binding.cvColor8.setOnClickListener { changeColor(binding, binding.cvColor8.cardBackgroundColor.defaultColor) }
        binding.cvColor9.setOnClickListener { changeColor(binding, binding.cvColor9.cardBackgroundColor.defaultColor) }
        binding.cvColor10.setOnClickListener { changeColor(binding, binding.cvColor10.cardBackgroundColor.defaultColor) }
        binding.cvColor11.setOnClickListener { changeColor(binding, binding.cvColor11.cardBackgroundColor.defaultColor) }
        binding.cvColor12.setOnClickListener { changeColor(binding, binding.cvColor12.cardBackgroundColor.defaultColor) }
        binding.cvColor13.setOnClickListener { changeColor(binding, binding.cvColor13.cardBackgroundColor.defaultColor) }
        binding.cvColor14.setOnClickListener { changeColor(binding, binding.cvColor14.cardBackgroundColor.defaultColor) }
        binding.cvColor15.setOnClickListener { changeColor(binding, binding.cvColor15.cardBackgroundColor.defaultColor) }
        binding.cvColor16.setOnClickListener { changeColor(binding, binding.cvColor16.cardBackgroundColor.defaultColor) }
        binding.cvColor17.setOnClickListener { changeColor(binding, binding.cvColor17.cardBackgroundColor.defaultColor) }
        binding.cvColor18.setOnClickListener { changeColor(binding, binding.cvColor18.cardBackgroundColor.defaultColor) }
        binding.cvColor19.setOnClickListener { changeColor(binding, binding.cvColor19.cardBackgroundColor.defaultColor) }
        binding.cvColor20.setOnClickListener { changeColor(binding, binding.cvColor20.cardBackgroundColor.defaultColor) }
    }
    protected fun returnDialog(dialog: AlertDialog): AlertDialog{
        dialog.window!!.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            setLayout(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        return dialog
    }
    protected fun setEditText(binding: DialogCreateEditTaggBinding){
        binding.etTaggName.requestFocus()
    }
}