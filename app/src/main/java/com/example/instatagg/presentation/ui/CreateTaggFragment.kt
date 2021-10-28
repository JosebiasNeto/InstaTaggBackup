package com.example.instatagg.presentation.ui

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.instatagg.R
import com.example.instatagg.databinding.CreateTaggBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.viewmodel.MainViewModel
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import org.koin.androidx.viewmodel.ext.android.viewModel


class CreateTaggFragment : DialogFragment() {

    private var _binding: CreateTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: MainViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity.let {
            _binding = CreateTaggBinding.inflate(layoutInflater)
            binding?.btnChoseColor?.setOnClickListener {
                choseColor()
            }
            val builder = AlertDialog.Builder(it)
            builder.setView(binding?.root)
                .setPositiveButton(R.string.txt_create,
                    DialogInterface.OnClickListener { dialog, id ->
                        if(binding?.etTaggName!!.text.isEmpty()){
                            Toast.makeText(it,"Your tagg was not created, you need to enter a name!", Toast.LENGTH_LONG).show()
                        } else {
                            viewModel.insertTagg(
                                Tagg(
                                    null,
                                    binding?.etTaggName!!.text.toString(),
                                    binding?.btnChoseColor!!.currentHintTextColor
                                )
                            )
                            startActivity(activity!!.intent)
                        }
                    })
                .setNegativeButton(R.string.txt_cancel,
                    DialogInterface.OnClickListener { dialog, id ->
                        getDialog()?.cancel()
                    })

            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
    private fun changeColor(color: Int){
        _binding?.btnChoseColor?.setBackgroundColor(color)
        _binding?.btnChoseColor?.setHintTextColor(color)
    }
    private fun choseColor(){
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