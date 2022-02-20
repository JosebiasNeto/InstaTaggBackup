package com.jnsoft.instatagg.presentation.taggs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.Toast
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.DialogCreateEditTaggBinding
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventCreateTagg
import java.io.Serializable

class CreateTaggDialog() : BaseTaggDialog() {

    private var _binding: DialogCreateEditTaggBinding? = null
    private val binding get() = _binding
    private lateinit var createdTagg: CreatedTagg

    interface CreatedTagg:Serializable{
        fun createTagg(name: String, color: Int)
    }

    companion object {
        fun newInstance(createdTagg: CreatedTagg): CreateTaggDialog {
            val fragment = CreateTaggDialog()
            val save = Bundle()
            save.putSerializable("createdTagg", createdTagg)
            fragment.arguments = save
            return fragment
        }
    }

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = DialogCreateEditTaggBinding.inflate(layoutInflater)
        binding?.btnChoseColor?.setOnClickListener {
            choseColor(_binding!!)
        }
        createdTagg = requireArguments().getSerializable("createdTagg") as CreatedTagg
        val builder = AlertDialog.Builder(context, R.style.style_dialog)
        builder.apply {
        setView(binding?.root)
        _binding?.cancelButton?.setOnClickListener {
                getDialog()?.cancel()
        }
        _binding!!.confirmButton.setOnClickListener {
                if(binding?.etTaggName!!.text.isEmpty()){
                    Toast.makeText(context, "Name can't be empty!", Toast.LENGTH_SHORT).show()
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