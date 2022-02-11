package com.jnsoft.instatagg.presentation.taggs

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.utils.FirebaseAnalytics.eventCreateTagg
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateTaggFragment : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: TaggsViewModel by viewModel()

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
                    viewModel.insertTagg(
                        Tagg(
                            null,
                            binding?.etTaggName!!.text.toString(),
                            binding?.btnChoseColor!!.currentHintTextColor,
                            0
                        )
                    )
                    startActivity(requireActivity().intent)
                    eventCreateTagg()
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