package com.example.instatagg.presentation.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import androidx.fragment.app.DialogFragment
import com.example.instatagg.R
import com.example.instatagg.databinding.CreateTaggBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.activities.TaggsActivity
import com.example.instatagg.presentation.viewmodel.PhotosViewModel
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTaggFragment(private var tagg: Tagg) : DialogFragment() {

    private var _binding: CreateTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: PhotosViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            _binding = CreateTaggBinding.inflate(layoutInflater)
            binding?.etTaggName!!.text = Editable.Factory.getInstance().newEditable(tagg.name)
            binding?.btnChoseColor!!.setBackgroundColor(tagg.color)
            binding?.btnChoseColor!!.setHintTextColor(tagg.color)
            binding?.btnChoseColor?.setOnClickListener {
                choseColor()
            }
            val builder = AlertDialog.Builder(it)
            builder.setView(binding?.root)
                .setPositiveButton(
                    R.string.txt_edit,
                    DialogInterface.OnClickListener { dialog, id ->

                        if (binding!!.etTaggName.text.toString() != tagg.name) {
                            viewModel.changeTaggName(
                                tagg.id!!,
                                binding!!.etTaggName.text.toString())

                        }
                        if (binding!!.btnChoseColor.currentHintTextColor != tagg.color) {
                            viewModel.changeTaggColor(
                                tagg.id!!,
                                binding!!.btnChoseColor.currentHintTextColor)
                        }
                        startActivity(Intent(activity, TaggsActivity::class.java))

                    })
                .setNegativeButton(
                    R.string.txt_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun changeColor(color: Int) {
        _binding?.btnChoseColor?.setBackgroundColor(color)
        _binding?.btnChoseColor?.setHintTextColor(color)
    }

    private fun choseColor() {
        activity?.let {
            MaterialColorPickerDialog.Builder(it)
                .setTitle(R.string.txt_chose_color)
                .setColorListener { color, colorHex ->
                    changeColor(color)
                }
                .setDefaultColor(R.color.white)
                .setTickColorPerCard(false)
                .show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}