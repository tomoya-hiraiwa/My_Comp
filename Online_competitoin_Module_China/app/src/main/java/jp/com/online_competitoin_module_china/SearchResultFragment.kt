package jp.com.online_competitoin_module_china

import android.content.Context
import android.os.Bundle
import android.os.RecoverySystem
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.tabs.TabLayout
import jp.com.online_competitoin_module_china.databinding.FragmentSearchResultBinding
import jp.com.online_competitoin_module_china.databinding.SearchListItemBinding
import java.text.SimpleDateFormat


class SearchResultFragment : Fragment() {
    private lateinit var b: FragmentSearchResultBinding
    private lateinit var adapter: SearchResultAdapter
    private var nowData = mutableListOf<ShowNews>()
    private var nowPosition = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentSearchResultBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createSpinner()
        b.apply {
            list.layoutManager = LinearLayoutManager(requireContext())
            adapter = SearchResultAdapter(nowData, requireContext()) {
                detailData = it
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, DetailFragment())
                    .commit()
            }
            list.adapter = adapter
            searchEdit.setText(initSearchText)
            indicator.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab?) {
                    nowPosition = tab?.position ?: 0
                    changeData()
                }

                override fun onTabUnselected(tab: TabLayout.Tab?) {
                }

                override fun onTabReselected(tab: TabLayout.Tab?) {
                }

            })
            tabFront.setOnClickListener {
                nowPosition += 1
                changeData()
            }
            tabBack.setOnClickListener {
                nowPosition -= 1
                changeData()
            }
            home.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView, HomeFragment())
                    .commit()
            }
            search.setOnClickListener {
                onSearch()
            }
            searchEdit.setOnKeyListener { v, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN) {
                    onSearch()
                    true
                } else false
            }
            spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    if (position == 0) {
                        searchResData.sortBy { it.time }
                        initView()
                    } else {
                        searchResData.sortByDescending { it.time }
                        initView()
                    }
                    indicator.getTabAt(0)?.select()
                    adapter.notifyDataSetChanged()
                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                }

            }
        }
        initView()
    }

    private fun initView() {
        b.apply {
            nowPosition = 0
            if (searchResData.isEmpty()) {
                resultFrame.visibility = View.GONE
                noResultFrame.visibility = View.VISIBLE
            } else {
                resultFrame.visibility = View.VISIBLE
                noResultFrame.visibility = View.GONE
                showSearchResData.clear()
                showSearchResData.addAll(searchResData.chunked(5))
                if (showSearchResData.isNotEmpty()) {
                    nowData.clear()
                    nowData.addAll(showSearchResData[nowPosition])
                    println(showSearchResData.lastIndex)
                    tabBack.isVisible = false
                    tabFront.isVisible = showSearchResData.lastIndex != 0
                    createIndicator()
                }
            }

            adapter.notifyDataSetChanged()
        }
    }

    private fun changeData() {
        nowData.clear()
        nowData.addAll(showSearchResData[nowPosition])
        adapter.notifyDataSetChanged()
        b.apply {
            when (nowPosition) {
                0 -> {
                    tabBack.isVisible = false
                    if (showSearchResData.lastIndex != 0){
                        tabFront.isVisible = true
                    }
                }

                showSearchResData.lastIndex -> {
                    tabFront.isVisible = false
                    tabBack.isVisible = true
                }

                else -> {
                    tabBack.isVisible = true
                    tabFront.isVisible = true
                }
            }
            indicator.getTabAt(nowPosition)?.select()
        }
    }

    private fun createIndicator() {
        b.indicator.removeAllTabs()
        for (i in showSearchResData.indices) {
            b.apply {
                val tab = indicator.newTab().apply {
                    text = (i + 1).toString()
                }
                indicator.addTab(tab)
            }
        }
    }

    private fun onSearch() {
        b.apply {
            if (searchEdit.text.isNotEmpty()) {
                searchResData = allNewsData.filter {
                    it.title.lowercase().contains(searchEdit.text.toString().lowercase())
                }.toMutableList()
                initView()
            } else Toast.makeText(
                requireContext(),
                "No input value to search news.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun createSpinner() {
        b.apply {
            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, spinnerData)
            spinner.adapter = adapter
        }
    }
}

class SearchResultAdapter(
    private val dataList: MutableList<ShowNews>,
    private val context: Context,
    val onClick: (ShowNews) -> Unit
) : RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder>() {
    inner class SearchResultViewHolder(private val b: SearchListItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bindData(data: ShowNews) {
            val imageName = data.text.firstOrNull { it.contains(".png") }
            b.apply {
                if (imageName != null) image.load(getBitmap(context, imageName))
                title.text = data.title
                val parseFormat = SimpleDateFormat("yyyy-MM-dd")
                val notImageTextList = data.text.filter { !it.contains(".png") }
                val descText = buildString {
                    notImageTextList.forEach { text ->
                        append(text)
                    }
                }
                desc.text = descText
                val dateValue = parseFormat.parse(data.time)
                val showFormat = SimpleDateFormat("MMM dd, yyyy")
                date.text = showFormat.format(dateValue)
                root.setOnClickListener {
                    onClick(data)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchResultViewHolder {
        return SearchResultViewHolder(
            SearchListItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    override fun onBindViewHolder(holder: SearchResultViewHolder, position: Int) {
        holder.bindData(data = dataList[position])
    }
}