package com.example.pdsesimongameapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView



class AdattatoreRV(private val lista: List<SimonGame.SimonGameData>) : RecyclerView.Adapter<AdattatoreRV.PartitaViewHolder>() {

    fun aggiornaLista(){
        if (lista.isNotEmpty()){
            val ultimoIndice = lista.size - 1
            notifyItemInserted(ultimoIndice)
        }
    }
    class PartitaViewHolder(view: View) : RecyclerView.ViewHolder(view){

        private val contatoreTV : TextView = view.findViewById(R.id.contatoreTV)
        private val sequenzaTV : TextView = view.findViewById(R.id.sequenzaInputTV)
                        fun bind(partita: SimonGame.SimonGameData){
                            contatoreTV.text = partita.maxLunghezzaSequenzaCorretta.toString()

                            sequenzaTV.text = TextUtilis.getEditString(partita.sequenza,partita.indexFirstWrongChar)
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

        viewHolder.itemView.setOnClickListener {
            val context = viewHolder.itemView.context

            val intent = Intent(context, DettaglioPartita::class.java)

            intent.putExtra("sequenza", partita.sequenza)
            intent.putExtra("bestscore",partita.maxLunghezzaSequenzaCorretta)
            intent.putExtra("indice",partita.indexFirstWrongChar)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return lista.size
    }
}