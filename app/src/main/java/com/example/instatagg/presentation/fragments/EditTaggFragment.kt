package com.example.instatagg.presentation.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.example.instatagg.R
import com.example.instatagg.databinding.FragmentCreateEditTaggBinding
import com.example.instatagg.domain.model.Tagg
import com.example.instatagg.presentation.viewmodel.PhotosViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTaggFragment(private var tagg: Tagg) : DialogFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: PhotosViewModel by viewModel()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = FragmentCreateEditTaggBinding.inflate(layoutInflater)
        binding!!.confirmButton.text = "Edit"
        binding!!.tvCreateTagg.text = "Edit Tagg"
        binding?.etTaggName!!.text = Editable.Factory.getInstance().newEditable(tagg.name)
        binding?.btnChoseColor!!.setBackgroundColor(tagg.color)
        binding?.btnChoseColor!!.setHintTextColor(tagg.color)
        binding?.btnChoseColor?.setOnClickListener {
            choseColor()
        }
        val builder = AlertDialog.Builder(context, R.style.style_dialog)
        builder.apply {
            setView(binding?.root)
            _binding!!.confirmButton.setOnClickListener {
                if(binding!!.etTaggName.text.isEmpty()){} else {
                if (binding!!.etTaggName.text.toString() != tagg.name) {
                    viewModel.changeTaggName(
                        tagg.id!!,
                        binding!!.etTaggName.text.toString()
                    )
                }
                if (binding!!.btnChoseColor.currentHintTextColor != tagg.color) {
                    viewModel.changeTaggColor(
                        tagg.id!!,
                        binding!!.btnChoseColor.currentHintTextColor)
                }
                tagg.name = binding!!.etTaggName.text.toString()
                tagg.color = binding!!.btnChoseColor.currentHintTextColor
                activity?.intent?.putExtra("tagg", tagg)
                activity?.finish()
                activity?.overridePendingTransition(0, 0)
                startActivity(activity?.intent)
                activity?.overridePendingTransition(0, 0)
            }}
            _binding!!.cancelButton.setOnClickListener {
                getDialog()?.cancel()
            }
        }
        binding!!.etTaggName.requestFocus()
        val dialog = builder.create()
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

    private fun changeColor(color: Int) {
        _binding?.btnChoseColor?.setBackgroundColor(color)
        _binding?.btnChoseColor?.setHintTextColor(color)
        _binding?.cvChoseColor?.visibility = View.GONE
    }

    private fun choseColor() {
        binding!!.cvChoseColor.visibility = View.VISIBLE
        binding!!.cvColor1.setOnClickListener { changeColor(binding!!.cvColor1.cardBackgroundColor.defaultColor) }
        binding!!.cvColor2.setOnClickListener { changeColor(binding!!.cvColor2.cardBackgroundColor.defaultColor) }
        binding!!.cvColor3.setOnClickListener { changeColor(binding!!.cvColor3.cardBackgroundColor.defaultColor) }
        binding!!.cvColor4.setOnClickListener { changeColor(binding!!.cvColor4.cardBackgroundColor.defaultColor) }
        binding!!.cvColor5.setOnClickListener { changeColor(binding!!.cvColor5.cardBackgroundColor.defaultColor) }
        binding!!.cvColor6.setOnClickListener { changeColor(binding!!.cvColor6.cardBackgroundColor.defaultColor) }
        binding!!.cvColor7.setOnClickListener { changeColor(binding!!.cvColor7.cardBackgroundColor.defaultColor) }
        binding!!.cvColor8.setOnClickListener { changeColor(binding!!.cvColor8.cardBackgroundColor.defaultColor) }
        binding!!.cvColor9.setOnClickListener { changeColor(binding!!.cvColor9.cardBackgroundColor.defaultColor) }
        binding!!.cvColor10.setOnClickListener { changeColor(binding!!.cvColor10.cardBackgroundColor.defaultColor) }
        binding!!.cvColor11.setOnClickListener { changeColor(binding!!.cvColor11.cardBackgroundColor.defaultColor) }
        binding!!.cvColor12.setOnClickListener { changeColor(binding!!.cvColor12.cardBackgroundColor.defaultColor) }
        binding!!.cvColor13.setOnClickListener { changeColor(binding!!.cvColor13.cardBackgroundColor.defaultColor) }
        binding!!.cvColor14.setOnClickListener { changeColor(binding!!.cvColor14.cardBackgroundColor.defaultColor) }
        binding!!.cvColor15.setOnClickListener { changeColor(binding!!.cvColor15.cardBackgroundColor.defaultColor) }
        binding!!.cvColor16.setOnClickListener { changeColor(binding!!.cvColor16.cardBackgroundColor.defaultColor) }
        binding!!.cvColor17.setOnClickListener { changeColor(binding!!.cvColor17.cardBackgroundColor.defaultColor) }
        binding!!.cvColor18.setOnClickListener { changeColor(binding!!.cvColor18.cardBackgroundColor.defaultColor) }
        binding!!.cvColor19.setOnClickListener { changeColor(binding!!.cvColor19.cardBackgroundColor.defaultColor) }
        binding!!.cvColor20.setOnClickListener { changeColor(binding!!.cvColor20.cardBackgroundColor.defaultColor) }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}