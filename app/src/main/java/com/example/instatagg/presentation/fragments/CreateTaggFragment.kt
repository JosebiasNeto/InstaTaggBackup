package com.example.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.instatagg.R
import com.example.instatagg.databinding.FragmentCreateEditTaggBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.viewmodel.TaggsViewModel
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
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
                            binding?.btnChoseColor!!.currentHintTextColor
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
    }
    private fun choseColor(){
        val pickerColor = context?.let {
            MaterialColorPickerDialog.Builder(it)
                .setTitle(R.string.txt_chose_color)
                .setColorListener { color, colorHex ->
                    changeColor(color)
                }
                .setTickColorPerCard(false)
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}