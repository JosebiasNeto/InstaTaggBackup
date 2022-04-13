package com.jnsoft.instatagg.presentation.taggs

import android.os.Bundle
import android.os.Handler
import android.util.DisplayMetrics
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.jnsoft.instatagg.R
import com.jnsoft.instatagg.databinding.FragmentTaggsBinding
import com.jnsoft.instatagg.domain.model.Tagg
import com.jnsoft.instatagg.utils.OnItemClickListener
import com.jnsoft.instatagg.utils.addOnItemClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

class TaggsFragment : Fragment(), CreateTaggDialog.CreatedTagg {

    private lateinit var binding: FragmentTaggsBinding
    private val viewModel: TaggsViewModel by viewModel()
    private lateinit var adapter: TaggsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTaggsBinding.inflate(inflater, container, false)

        setupTaggs()

        binding.btnCreateTagg.setOnClickListener {
            val createTaggFragment = CreateTaggDialog.newInstance(this)
            createTaggFragment.show(requireActivity().supportFragmentManager,"createTagg")
        }

        binding.rvTaggs.addOnItemClickListener(object : OnItemClickListener{
            override fun onItemClicked(position: Int, view: View) {
                view.findNavController().navigate(TaggsFragmentDirections
                    .actionTaggsFragmentToPhotosFragment(adapter.getTagg(position).id!!))
            }
        })

        setOnBackPressed()

        return binding.root
    }

    private fun setupTaggs() {
        binding.rvTaggs.layoutManager = GridLayoutManager(context,3)
        val outMetrics = DisplayMetrics()
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = requireContext().display
            display?.getRealMetrics(outMetrics)
        } else {
            @Suppress("DEPRECATION")
            val display = requireActivity().windowManager.defaultDisplay
            @Suppress("DEPRECATION")
            display.getMetrics(outMetrics)
        }
        adapter = TaggsAdapter(arrayListOf(),outMetrics.widthPixels)
        binding.rvTaggs.adapter = adapter

        viewModel.getTaggs()
        viewModel.taggs.observe(viewLifecycleOwner) {
            refreshAdapter(it.reversed())
            setTotalSize(it)
        }
    }

    private fun setTotalSize(taggs: List<Tagg>) {
        val totalSize = arrayListOf<Int>()
        taggs.map { totalSize.add(it.size) }
        if(totalSize.sum().toString().length > 6){
            binding.tvTotalSize.text = totalSize.sum().toString().substring(0,
                totalSize.sum().toString().length - 6)
        } else binding.tvTotalSize.text = "0"
    }

    private fun refreshAdapter(taggs: List<Tagg>){
        adapter.apply {
            addTaggs(taggs)
            notifyDataSetChanged()
        }
    }

    override fun createTagg(name: String, color: Int) {
        viewModel.insertTagg(Tagg(null, name, color, 0))
        Handler().postDelayed({
            viewModel.getTaggs()
        }, 500)
    }

    private fun setOnBackPressed(){
        requireActivity().onBackPressedDispatcher.addCallback(this){
            requireView().findNavController().navigate(R.id.action_taggsFragment_to_cameraFragment)
        }
    }
}