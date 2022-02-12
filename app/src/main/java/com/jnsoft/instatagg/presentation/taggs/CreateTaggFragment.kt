package com.jnsoft.instatagg.presentation.taggs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventCreateTagg

class CreateTaggFragment(val createdTagg: CreatedTagg) : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding

    interface CreatedTagg{
        fun createTagg(name: String, color: Int)
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = FragmentCreateEditTaggBinding.inflate(layoutInflater)
        binding?.btnChoseColor?.setOnClickListener {
            choseColor(_binding!!)
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
                    createdTagg.createTagg(binding?.etTaggName!!.text.toString(),
                        binding?.btnChoseColor!!.currentHintTextColor)
                    eventCreateTagg()
                    dialog!!.cancel()
                }
        }}
        setEditText(binding!!)
        val dialog = builder.create()
        return returnDialog(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}