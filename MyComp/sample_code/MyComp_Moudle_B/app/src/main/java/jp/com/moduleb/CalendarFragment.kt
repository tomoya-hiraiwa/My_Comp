package jp.com.moduleb

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.CalendarView
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import jp.com.moduleb.databinding.CalendarEventItemBinding
import jp.com.moduleb.databinding.CalendarItemBinding
import jp.com.moduleb.databinding.FragmentCalendarBinding
import jp.com.moduleb.databinding.PagerItemBinding
import java.io.File
import java.security.Key
import java.text.SimpleDateFormat


class CalendarFragment : Fragment() {
    private lateinit var b: FragmentCalendarBinding
    private var isMonthOpen = false
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentCalendarBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        eventFile = File(requireContext().filesDir, "event.json")
        if (!eventFile.exists()) {
            eventFile.createNewFile()
        }
        getDateList()
        getEventData()
        b.apply {
            pagerAdapter = PagerAdapter(dateList, eventList, requireContext()) {
                detailData = it
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, ItemDetailFragment())
                    .commit()
            }
            calendarPager.adapter = pagerAdapter
            setCurrentMonth()
            setSearch()
            setTabMonth()
            addButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, CraeteItemFragment())
                    .commit()
            }
            todayButton.setOnClickListener {
                setCurrentMonth()
                setSearch()
                setTabMonth()
            }
            accountButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView, AccountFragment())
                    .commit()
            }
        }
    }

    private fun setCurrentMonth() {
        val todayCount = SimpleDateFormat("M").format(today * 1000).toInt() - 1
        b.calendarPager.currentItem = todayCount
    }

    private fun setTabMonth() {
        b.apply {
            monthText.text = SimpleDateFormat("MMM").format(today * 1000)
            monthText.setOnClickListener {
                if (isMonthOpen) {
                    month.root.isVisible = false
                    monthTriangle.rotation = monthTriangle.rotation + 180f
                    isMonthOpen = false
                } else {
                    month.root.isVisible = true
                    monthTriangle.rotation = monthTriangle.rotation + 180f
                    isMonthOpen = true
                }
            }
        }
        b.month.apply {
            val getInitChip = monthGroup.getChildAt(
                SimpleDateFormat("M").format(today * 1000).toInt() - 1
            ) as Chip
            getInitChip.isChecked = true
            monthGroup.setOnCheckedChangeListener { group, checkedId ->
                if (checkedId != -1) {
                    val view = group.findViewById<Chip>(checkedId)
                    val index = group.indexOfChild(view)
                    b.calendarPager.currentItem = index

                    root.isVisible = false
                    b.monthTriangle.rotation = b.monthTriangle.rotation + 180f
                    b.monthText.text = view.text
                    isMonthOpen = false
                }
            }
        }
    }

    private fun setSearch() {
        b.apply {
            searchEdit.setOnKeyListener { v, keyCode, event ->
                if (keyCode == EditorInfo.IME_ACTION_DONE || keyCode == KeyEvent.KEYCODE_ENTER) {
                    if (searchEdit.text.isNotEmpty()) {
                        searchText = searchEdit.text.toString()
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, ListFragment())
                            .commit()
                        return@setOnKeyListener true
                    }
                }
                return@setOnKeyListener false
            }
        }
    }
}


class PagerAdapter(
    private val dateList: MutableList<MutableList<Long>>,
    private val eventList: MutableList<Event>,
    private val context: Context,
    val onClick: (Event) -> Unit
) : RecyclerView.Adapter<PagerAdapter.PagerViewHolder>() {
    inner class PagerViewHolder(private val b: PagerItemBinding) : RecyclerView.ViewHolder(b.root) {
        fun bindData(data: MutableList<Long>) {
            b.apply {
                calendar.layoutManager = GridLayoutManager(context, 7)
                val adapter = CalendarAdapter(data, eventList, context) {
                    onClick(it)
                }
                calendar.adapter = adapter
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            PagerItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        holder.bindData(dateList[position])
    }
}

class CalendarAdapter(
    private val dateList: MutableList<Long>,
    private val eventList: MutableList<Event>,
    private val context: Context,
    val onClick: (Event) -> Unit
) : RecyclerView.Adapter<CalendarAdapter.CalendarViewHolder>() {
    inner class CalendarViewHolder(private val b: CalendarItemBinding) :
        RecyclerView.ViewHolder(b.root) {
        fun bindData(data: Long) {
            b.apply {
                eventFrame.removeAllViews()
                val normalFormat = SimpleDateFormat("d")
                val firstDayFormat = SimpleDateFormat("MMM d")
                val functionFormat = SimpleDateFormat("yyyy MM dd")
                val normalText = normalFormat.format(data * 1000)
                date.text =
                    if (normalText.toInt() != 1) normalText else firstDayFormat.format(data * 1000)
                if (functionFormat.format(data * 1000) == functionFormat.format(today * 1000)) {
                    root.background = context.getDrawable(R.drawable.calendar_frame_today)
                }
                val eventData = eventList.filter { it.date == data }
                if (eventData.isNotEmpty()) {
                    for (i in eventData.indices) {
                        val data = eventData[i]
                        val view = CalendarEventItemBinding.inflate(
                            LayoutInflater.from(context),
                            eventFrame,
                            false
                        )
                        view.apply {
                            title.text = data.title
                            root.setCardBackgroundColor(context.getColor(data.color))
                            root.setOnClickListener {
                                onClick(data)
                            }
                        }
                        val spacer = View(context)
                        spacer.layoutParams = LinearLayout.LayoutParams(30, 20)
                        eventFrame.addView(view.root)
                        eventFrame.addView(spacer)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CalendarViewHolder {
        return CalendarViewHolder(
            CalendarItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return dateList.size
    }

    override fun onBindViewHolder(holder: CalendarViewHolder, position: Int) {
        holder.bindData(data = dateList[position])
    }
}