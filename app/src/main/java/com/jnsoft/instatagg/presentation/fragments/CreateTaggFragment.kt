package com.jnsoft.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.viewmodel.TaggsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel





class CreateTaggFragment : DialogFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: TaggsViewModel by viewModel()

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = FragmentCreateEditTaggBinding.inflate(layoutInflater)
        binding?.btnChoseColor?.setOnClickListener {
            choseColor()
        }
        val builder = AlertDialog.Builder(context, R.style.style_dialog)
        builder.apply {
        setView(binding?.root)
        _binding?.cancelButton?.setOnClickListener {
                getDialog()?.cancel()
        }
        _binding!!.confirmButton.setOnClickListener {
                if(binding?.etTaggName!!.text.isEmpty()){
                } else {
                    viewModel.insertTagg(
                        Tagg(
                            null,
                            binding?.etTaggName!!.text.toString(),
                            binding?.btnChoseColor!!.currentHintTextColor,
                            0
                        )
                    )
                    startActivity(requireActivity().intent)
                }
        }}
        binding!!.etTaggName.requestFocus()
        val dialog = builder.create()
        dialog.window!!.apply {
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
            setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }
        return dialog
    }

    private fun changeColor(color: Int){
        _binding?.btnChoseColor?.setBackgroundColor(color)
        _binding?.btnChoseColor?.setHintTextColor(color)
        _binding?.cvChoseColor?.visibility = View.GONE
    }
    private fun choseColor(){
        binding!!.cvChoseColor.visibility = View.VISIBLE
        binding!!.cvColor1.setOnClickListener { changeColor(binding!!.cvColor1.cardBackgroundColor.defaultColor)}
        binding!!.cvColor2.setOnClickListener { changeColor(binding!!.cvColor2.cardBackgroundColor.defaultColor)}
        binding!!.cvColor3.setOnClickListener { changeColor(binding!!.cvColor3.cardBackgroundColor.defaultColor)}
        binding!!.cvColor4.setOnClickListener { changeColor(binding!!.cvColor4.cardBackgroundColor.defaultColor)}
        binding!!.cvColor5.setOnClickListener { changeColor(binding!!.cvColor5.cardBackgroundColor.defaultColor)}
        binding!!.cvColor6.setOnClickListener { changeColor(binding!!.cvColor6.cardBackgroundColor.defaultColor)}
        binding!!.cvColor7.setOnClickListener { changeColor(binding!!.cvColor7.cardBackgroundColor.defaultColor)}
        binding!!.cvColor8.setOnClickListener { changeColor(binding!!.cvColor8.cardBackgroundColor.defaultColor)}
        binding!!.cvColor9.setOnClickListener { changeColor(binding!!.cvColor9.cardBackgroundColor.defaultColor)}
        binding!!.cvColor10.setOnClickListener { changeColor(binding!!.cvColor10.cardBackgroundColor.defaultColor)}
        binding!!.cvColor11.setOnClickListener { changeColor(binding!!.cvColor11.cardBackgroundColor.defaultColor)}
        binding!!.cvColor12.setOnClickListener { changeColor(binding!!.cvColor12.cardBackgroundColor.defaultColor)}
        binding!!.cvColor13.setOnClickListener { changeColor(binding!!.cvColor13.cardBackgroundColor.defaultColor)}
        binding!!.cvColor14.setOnClickListener { changeColor(binding!!.cvColor14.cardBackgroundColor.defaultColor)}
        binding!!.cvColor15.setOnClickListener { changeColor(binding!!.cvColor15.cardBackgroundColor.defaultColor)}
        binding!!.cvColor16.setOnClickListener { changeColor(binding!!.cvColor16.cardBackgroundColor.defaultColor)}
        binding!!.cvColor17.setOnClickListener { changeColor(binding!!.cvColor17.cardBackgroundColor.defaultColor)}
        binding!!.cvColor18.setOnClickListener { changeColor(binding!!.cvColor18.cardBackgroundColor.defaultColor)}
        binding!!.cvColor19.setOnClickListener { changeColor(binding!!.cvColor19.cardBackgroundColor.defaultColor)}
        binding!!.cvColor20.setOnClickListener { changeColor(binding!!.cvColor20.cardBackgroundColor.defaultColor)}
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}