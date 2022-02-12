package com.jnsoft.instatagg.presentation.taggs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventCreateTagg

class CreateTaggFragment() : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private lateinit var createdTagg: CreatedTagg

    interface CreatedTagg{
        fun createTagg(name: String, color: Int)
    }

    companion object {
        fun newInstance(createdTagg: CreatedTagg): CreateTaggFragment {
            val fragment = CreateTaggFragment()
            fragment.createdTagg = createdTagg
            return fragment
        }
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