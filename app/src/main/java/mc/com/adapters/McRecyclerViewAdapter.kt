package mc.com.adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import mc.com.R

class McRecyclerViewAdapter(private var dataset: Array<String>, private val listener: OnItemClickListener) :
    RecyclerView.Adapter<McRecyclerViewAdapter.ViewHolder>() {

    fun updateDataSet(data: Array<String>){
        this.dataset=data
    }

    interface OnItemClickListener {
        fun onItemClick(position:Int, item: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.recyclerview_item_layout, parent, false)

        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = dataset[position]
        holder.itemView.setOnClickListener { listener.onItemClick(position, dataset[position]) }

        //holder.bind(dataset[position], listener);
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.item_title)

        /* public void bind(final String item, final OnItemClickListener listener) {
            title.setText(item);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }*/
    }
}
