package com.jnsoft.instatagg.presentation.fragments

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.viewmodel.TaggsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class CreateTaggFragment : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: TaggsViewModel by viewModel()
    private lateinit var firebaseAnalytics: FirebaseAnalytics

    @SuppressLint("RestrictedApi")
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        _binding = FragmentCreateEditTaggBinding.inflate(layoutInflater)
        firebaseAnalytics = Firebase.analytics
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

    private fun eventCreateTagg() {
        val params = Bundle()
        params.putString("tagg", "create_tagg")
        firebaseAnalytics.logEvent("create_tagg", params)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}