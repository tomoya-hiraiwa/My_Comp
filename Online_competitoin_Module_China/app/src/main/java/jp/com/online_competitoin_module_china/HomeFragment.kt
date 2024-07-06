package jp.com.online_competitoin_module_china

import android.content.Context
import android.os.Bundle
import android.os.RecoverySystem
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.gson.reflect.TypeToken
import jp.com.online_competitoin_module_china.databinding.FragmentHomeBinding
import jp.com.online_competitoin_module_china.databinding.ListItemBinding
import jp.com.online_competitoin_module_china.databinding.TempItemBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class HomeFragment : Fragment() {
    private lateinit var b: FragmentHomeBinding
    private lateinit var hotAdapter: NewsListAdapter
    private lateinit var newsListAdapter: NewsListAdapter
    private lateinit var tempListAdapter: TempListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentHomeBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getTopNews()
        getAllNews()
        initTempData()
        b.apply {
            topList.layoutManager = GridLayoutManager(requireContext(),2)
            allList.layoutManager = GridLayoutManager(requireContext(),3)
            tempList.layoutManager = LinearLayoutManager(requireContext())
            tempListAdapter = TempListAdapter(showTempData)
            hotAdapter = NewsListAdapter(hotNewsData,requireContext()){
                detailData = it
                changeFragment(DetailFragment())
            }
            newsListAdapter = NewsListAdapter(allNewsData,requireContext()){
                detailData = it
                changeFragment(DetailFragment())
            }
            topList.adapter = hotAdapter
            allList.adapter = newsListAdapter
            tempList.adapter = tempListAdapter
            topTitle.text = topNewsData.title
            val topImageName = topNewsData.text.firstOrNull { it.contains(".png") }
            if (topImageName != null) {
                topImage.load(getBitmap(requireContext(),topImageName))
            }
            val notImageTextList = topNewsData.text.filter { !it.contains(".png") }
            val descText = buildString {
               notImageTextList.forEach {text->
                   append(text)
               }
            }
            topDesc.text = descText
            search.setOnClickListener {
                parentFragmentManager.beginTransaction().add(R.id.fragmentContainerView,SearchFragment()).commit()
            }
            topicItem.setOnClickListener {
                detailData = ShowNews(topNewsData.title, text = topNewsData.text, Source = topNewsData.Source, time = topNewsData.time)
                changeFragment(DetailFragment())
            }
        }
        changeTempData()
    }

    private fun initTempData(){
        showTempData.clear()
        showTempData.addAll(tempData.subList(0,5))
    }

    private fun changeFragment(fm: Fragment){
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView,fm)
            .commit()
    }

    private fun changeTempData(){
        lifecycleScope.launch {
            while (true){
                delay(5000)
                showTempData.removeAt(0)
                tempListAdapter.notifyItemRemoved(0)
                val nowLastData = showTempData.last()
                val index = tempData.indexOf(nowLastData)
                if (index != tempData.lastIndex){
                showTempData.add(tempData[index+1])
            }
                else showTempData.add(tempData[0])
                tempListAdapter.notifyItemInserted(4)
            }
        }
    }

    private fun getTopNews() {
        tempData.clear()
        val input = requireContext().assets.open("top_new.json")
        val inputData = gson.fromJson<TopNew>(input.bufferedReader().use { it.readText() }, object :
            TypeToken<TopNew>() {}.type)
        val textList = textParser(inputData.text)
        topNewsData = ShowTopNew(inputData.title,inputData.time,inputData.Source,textList,inputData.updatalist)
        tempData.addAll(topNewsData.updatalist)
    }

    private fun getAllNews(){
        allNewsData.clear()
        hotNewsData.clear()
        val input= requireContext().assets.open("new_data_list.json")
        val inputData = gson.fromJson<MutableList<News>>(input.bufferedReader().use { it.readText() },object : TypeToken<MutableList<News>>(){}.type)
        inputData.forEach { item ->
           val textList = textParser(item.text)
           allNewsData.add(ShowNews(item.title,item.hot,textList,item.Source,item.time))
        }
        hotNewsData.addAll(allNewsData.filter { it.hot })
    }
}

class NewsListAdapter(private val dataList: List<ShowNews>,private val context: Context,val onClick:(ShowNews) -> Unit): RecyclerView.Adapter<NewsListAdapter.NewsListViewHolder>(){
    inner class NewsListViewHolder(private val b: ListItemBinding): RecyclerView.ViewHolder(b.root){
        fun bindData(data: ShowNews){
            val imageName = data.text.firstOrNull { it.contains(".png") }
            b.apply {
                if (imageName != null) image.load(getBitmap(context,imageName))
                title.text = data.title
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsListViewHolder {
        return NewsListViewHolder(ListItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: NewsListViewHolder, position: Int) {
        holder.bindData(data = dataList[position])
    }
}
class TempListAdapter(private val dataList: List<Temp>,): RecyclerView.Adapter<TempListAdapter.TempListViewHolder>(){
    inner class TempListViewHolder(private val b: TempItemBinding): RecyclerView.ViewHolder(b.root){
        fun bindData(data: Temp){
            b.apply {
               text.text = data.content
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TempListViewHolder {
        return TempListViewHolder(TempItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: TempListViewHolder, position: Int) {
        holder.bindData(data = dataList[position])
    }
}



