package com.example.instatagg.presentation.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.instatagg.R
import com.example.instatagg.domain.model.Tagg

class MainAdapter(private val taggs: ArrayList<Tagg>):
 RecyclerView.Adapter<MainAdapter.MainHolder>(){

    class MainHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(tagg: Tagg){
            itemView.apply {
                findViewById<TextView>(R.id.chose_tagg_button).text = tagg.name
                findViewById<TextView>(R.id.chose_tagg_button).setBackgroundColor(tagg.color)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainHolder =
        MainHolder(LayoutInflater.from(parent.context)
            .inflate(R.layout.tagg_item_rv, parent, false))

    override fun onBindViewHolder(holder: MainHolder, position: Int) =
        holder.bind(taggs[position])

    override fun getItemCount(): Int = taggs.size

    fun getTagg(position: Int) = taggs[position]

    fun addTaggs(taggs: List<Tagg>){
        this.taggs.addAll(taggs)
    }

}