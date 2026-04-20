package com.example.pdsesimongameapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class AdattatoreRV(private val lista: List<Partita>) : RecyclerView.Adapter<AdattatoreRV.PartitaViewHolder>() {
                    //al posto di un array come dataset devo usare qualcos'altro
    class PartitaViewHolder(view: View) : RecyclerView.ViewHolder(view){
        private val contatoreTV : TextView = view.findViewById(R.id.contatoreTV)
        private val sequenzaTV : TextView = view.findViewById(R.id.sequenzaInputTV)
                        fun bind(partita: Partita){
                            //TODO: togliere hardcoded string "Count: " non è richiesto
                            contatoreTV.text = "Count: ${partita.contatoreRettangoliPremuti}"
                            sequenzaTV.text = partita.sequenza
                        }
    }

    //crea una view rispetto al layout di un singolo elemento che rappresenta una partita (file layout_partita.xml)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PartitaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_partita,parent,false)
        return PartitaViewHolder(view)
    }
    override fun onBindViewHolder(viewHolder: PartitaViewHolder, position: Int){
        val partita = lista[position]
        viewHolder.bind(partita)
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}