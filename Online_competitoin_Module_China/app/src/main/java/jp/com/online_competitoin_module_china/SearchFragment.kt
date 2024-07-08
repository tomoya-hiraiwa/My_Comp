package jp.com.online_competitoin_module_china

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout.Tab
import com.google.android.material.transition.MaterialSharedAxis
import jp.com.online_competitoin_module_china.databinding.FragmentSearchBinding
import jp.com.online_competitoin_module_china.databinding.PagerItemBinding


class SearchFragment : Fragment() {
    private lateinit var b: FragmentSearchBinding
    private lateinit var pagerAdapter: PagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y,true).apply {
            duration = 500
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y,false).apply {
            duration = 2000
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSearchBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getHotNews()
        createTab()
        b.apply {
            root.setOnClickListener{}
            pagerAdapter = PagerAdapter(searchHotData,requireContext()){
                detailData = it
                parentFragmentManager.beginTransaction().replace(R.id.fragmentContainerView,DetailFragment())
                    .commit()
            }
            pager.adapter  = pagerAdapter
            pager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    indicator.getTabAt(position)?.select()
                }
            })
            close.setOnClickListener {
                parentFragmentManager.beginTransaction().remove(this@SearchFragment)
                    .commit()
            }
            search.setOnClickListener {
                onSearch()
            }
            searchEdit.setOnKeyListener { v, keyCode, event ->
                println(event.keyCode)
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN){
                    onSearch()
                    true
                }
                else false
            }
        }
    }
    private fun getHotNews(){
        searchHotData = hotNewsData.chunked(4).toMutableList()
    }
    private fun createTab(){
        b.apply {
            for (i in searchHotData.indices) {
                val item = indicator.newTab().apply {
                    setIcon(R.drawable.baseline_lens_24)
                }
                indicator.addTab(item)
            }
        }
    }
    private fun onSearch(){
        b.apply {
            if (searchEdit.text.isNotEmpty()){
                searchResData = allNewsData.filter { it.title.lowercase().contains(searchEdit.text.toString().lowercase()) }.toMutableList()
                initSearchText = searchEdit.text.toString()
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,SearchResultFragment())
                    .commit()
            }
            else Toast.makeText(requireContext(), "No input value to search news.", Toast.LENGTH_SHORT).show()
        }
    }
}

class PagerAdapter(private val dataList: MutableList<List<ShowNews>>, private val context: Context, val onClick:(ShowNews)-> Unit): RecyclerView.Adapter<PagerAdapter.PagerViewHolder>(){
    inner class PagerViewHolder(private val b: PagerItemBinding): RecyclerView.ViewHolder(b.root){
        fun bindData(data: List<ShowNews>){
            b.apply {
                list.layoutManager = GridLayoutManager(context,2)
                list.adapter = NewsListAdapter(data,context){
                    onClick(it)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(PagerItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(data =  dataList[position])
    }
}
