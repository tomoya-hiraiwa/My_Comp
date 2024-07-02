package jp.com.moduleb

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.File
import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import java.time.temporal.TemporalAdjusters

var dateList = mutableListOf<MutableList<Long>>()

var eventList = mutableListOf<Event>()
var filterList = mutableListOf<Event>()
var userData = Account()
lateinit var eventFile: File

lateinit var pagerAdapter: PagerAdapter
lateinit var eventListAdapter: EventListAdapter

val today = Instant.now().epochSecond

var searchText = ""

var detailData = Event()


data class Event(
    var title: String = "",
    var date: Long = 0L,
    var place: String = "",
    var color: Int = R.color.back_blue,
    var image: String = ""
)

data class Account(var name: String = "",var position: String = "",var number: Int  = 1)


fun getDateList(){
    for (i in 1 until 13){
        var oneMonthData = mutableListOf<Long>()
        val month = i
        val firstDay = LocalDate.of(2024,month,1)
        val startDayOfMonth = firstDay.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY))
        val endDay = firstDay.with(TemporalAdjusters.lastDayOfMonth())
        val lastDayOfMonth = endDay.with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        var nowDay = startDayOfMonth
        while (!nowDay.isAfter(lastDayOfMonth)){
            oneMonthData.add(nowDay.atStartOfDay(ZoneId.systemDefault()).toEpochSecond())
            nowDay = nowDay.plusDays(1)
        }
        dateList.add(oneMonthData)
    }
}

fun getEventData(){
    val eventText = eventFile.inputStream().bufferedReader().use { it.readText() }
    if (eventText.isNotEmpty()){
        eventList = gson.fromJson(eventText,object: TypeToken<MutableList<Event>>(){}.type)
    }
}

val gson = Gson()