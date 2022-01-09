package com.jnsoft.instatagg.presentation.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.presentation.viewmodel.PhotosViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class EditTaggFragment : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private val viewModel: PhotosViewModel by viewModel()
    private lateinit var tagg: Tagg

    companion object {
        fun newInstance(tagg: Tagg): EditTaggFragment {
            val fragment = EditTaggFragment()
            val saveTagg: Bundle = Bundle()
            saveTagg.putParcelable("tagg", tagg)
            fragment.arguments = saveTagg
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        tagg = requireArguments().getParcelable<Tagg>("tagg")!!
        _binding = FragmentCreateEditTaggBinding.inflate(layoutInflater)
        binding!!.confirmButton.text = getString(R.string.txt_edit)
        binding!!.tvCreateTagg.text = getString(R.string.edit_tagg)
        binding?.etTaggName!!.text = Editable.Factory.getInstance().newEditable(tagg.name)
        binding?.btnChoseColor!!.setBackgroundColor(tagg.color)
        binding?.btnChoseColor!!.setHintTextColor(tagg.color)
        binding?.btnChoseColor?.setOnClickListener {
            choseColor(_binding!!)
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
        setEditText(binding!!)
        val dialog = builder.create()
        return returnDialog(dialog)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}