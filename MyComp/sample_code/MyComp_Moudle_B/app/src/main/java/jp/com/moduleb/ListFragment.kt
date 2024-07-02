package jp.com.moduleb

import android.app.LauncherActivity.ListItem
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import jp.com.moduleb.databinding.FragmentListBinding
import jp.com.moduleb.databinding.ListItemBinding
import java.text.SimpleDateFormat


class ListFragment : Fragment() {
private lateinit var b: FragmentListBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentListBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            list.layoutManager = LinearLayoutManager(requireContext())
            eventListAdapter = EventListAdapter(filterList,requireContext()){
                detailData = it
                parentFragmentManager.beginTransaction().add(R.id.fragmentContainerView,ItemDetailFragment())
                    .commit()
            }
            list.adapter = eventListAdapter
            todayButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,CalendarFragment())
                    .commit()
            }
            searchEdit.doAfterTextChanged {
                searchText = searchEdit.text.toString()
                filterData()
            }
            accountButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView,AccountFragment())
                    .commit()
            }
            filterData()
        }
    }
private fun filterData(){
    filterList.clear()
    var filterData = mutableListOf<Event>()
    if (searchText !=""){
        println(searchText)
        filterData = eventList.filter { it.title.lowercase().contains(searchText.lowercase()) }.toMutableList()
    }
    else filterData = eventList
    filterList.addAll(filterData)
    eventListAdapter.notifyDataSetChanged()
}

}

class EventListAdapter(private val dataList: MutableList<Event>,private  val context: Context,val onClick:(Event)-> Unit): RecyclerView.Adapter<EventListAdapter.EventListViewHolder>(){
    inner class EventListViewHolder(private val b:ListItemBinding): RecyclerView.ViewHolder(b.root){
        fun bindData(data: Event){
            b.apply {
                root.setCardBackgroundColor(context.getColor(data.color))
                val dateFormat = SimpleDateFormat("d MMM yyyy")
                date.text = "date: ${dateFormat.format(data.date)}"
                place.text = "place: ${data.place }"
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventListViewHolder {
        return EventListViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: EventListViewHolder, position: Int) {
        holder.bindData(data = dataList[position])
    }
}