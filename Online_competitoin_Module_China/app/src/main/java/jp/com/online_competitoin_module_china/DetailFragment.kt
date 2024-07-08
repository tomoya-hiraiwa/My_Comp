package jp.com.online_competitoin_module_china

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import jp.com.online_competitoin_module_china.databinding.FragmentDetailBinding
import java.text.SimpleDateFormat

class DetailFragment : Fragment() {
private lateinit var b: FragmentDetailBinding
private lateinit var recomAdapter: NewsListAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentDetailBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            titleText.text = detailData.title
            writerText.text = detailData.Source
            showDate()
            setDetailData()
            recomList.layoutManager= LinearLayoutManager(requireContext())
            recomAdapter = NewsListAdapter(allNewsData.shuffled().subList(0,3),requireContext()){
                detailData = it
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,DetailFragment())
                    .commit()
            }
            recomList.adapter = recomAdapter
            home.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,HomeFragment())
                    .commit()
            }
            b.search.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView,SearchFragment())
                    .commit()
            }
        }
    }
    private fun showDate(){
        val parseFormat = SimpleDateFormat("yyyy-MM-dd")
        val date = parseFormat.parse(detailData.time)
        val showFormat = SimpleDateFormat("MMM dd, yyyy")
        b.dateText.text = showFormat.format(date)
    }

    private fun setDetailData(){
        b.detailFrame.removeAllViews()
        for (item in detailData.text.indices){
            val data = detailData.text[item]
        b.apply {
            val spacer = View(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,30)
            }
            if (data.contains(".png")){
                val view = ImageView(requireContext())
                view.load(getBitmap(requireContext(),data))
                detailFrame.addView(view)
                detailFrame.addView(spacer)
            }
            else {
                val view = TextView(requireContext())
                view.text = data
                view.setTextColor(Color.WHITE)
                detailFrame.addView(view)
                detailFrame.addView(spacer)
            }
        }
        }
    }


}