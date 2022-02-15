package com.jnsoft.instatagg.presentation.taggs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentCreateEditTaggBinding
import com.jnsoft.instatagg.domain.model.Tagg
import java.io.Serializable

class EditTaggFragment : BaseTaggFragment() {

    private var _binding: FragmentCreateEditTaggBinding? = null
    private val binding get() = _binding
    private lateinit var tagg: Tagg
    private lateinit var editedTagg: EditedTagg

    interface EditedTagg:Serializable{
        fun editTagg(name: String, color: Int)
    }

    companion object {
        fun newInstance(tagg: Tagg, editedTagg: EditedTagg): EditTaggFragment {
            val fragment = EditTaggFragment()
            val save = Bundle()
            save.putParcelable("tagg", tagg)
            save.putSerializable("editedTagg", editedTagg)
            fragment.arguments = save
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        tagg = requireArguments().getParcelable<Tagg>("tagg")!!
        editedTagg = requireArguments().getSerializable("editedTagg") as EditedTagg
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
                if(binding!!.etTaggName.text.isEmpty()){
                    Toast.makeText(context, "Name can't be empty!",Toast.LENGTH_SHORT).show()
                } else {
                    editedTagg.editTagg(binding!!.etTaggName.text.toString(),
                    binding!!.btnChoseColor.currentHintTextColor)
                    dialog?.cancel()
                }
            }
            _binding!!.cancelButton.setOnClickListener {
                dialog?.cancel()
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