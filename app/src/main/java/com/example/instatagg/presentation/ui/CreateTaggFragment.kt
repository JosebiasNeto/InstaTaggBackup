package com.example.instatagg.presentation.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.instatagg.R
import com.example.instatagg.databinding.CreateTaggBinding
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch


class CreateTaggFragment : DialogFragment() {

    private lateinit var binding: CreateTaggBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = CreateTaggBinding.inflate(layoutInflater)
        return activity.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            binding.btnChoseColor.setOnClickListener {
                activity?.let { it1 ->
                    MaterialColorPickerDialog
                        .Builder(it1.applicationContext)        					// Pass Activity Instance
                        .setTitle("Pick Theme")           		// Default "Choose Color"
                        .setColorShape(ColorShape.SQAURE)   	// Default ColorShape.CIRCLE
                        .setColorSwatch(ColorSwatch._300)   	// Default ColorSwatch._500
                        .setDefaultColor(R.color.gray) 		// Pass Default Color
                        .setColorListener { color, colorHex ->
                        }
                        .show()
                }
            }
            builder.setView(inflater.inflate(R.layout.create_tagg, null))
                .setPositiveButton(R.string.txt_create,
                    DialogInterface.OnClickListener { dialog, id ->

                })
                .setNegativeButton(R.string.txt_cancel,
                DialogInterface.OnClickListener { dialog, id ->
                    getDialog()?.cancel()
                })
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}