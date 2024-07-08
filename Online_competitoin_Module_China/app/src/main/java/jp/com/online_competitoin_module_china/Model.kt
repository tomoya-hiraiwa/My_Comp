package jp.com.online_competitoin_module_china

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.google.gson.Gson

data class News(var title: String = "",var hot: Boolean = false,var text: String = "",var Source: String = "",var time:String = "")

data class ShowNews(var title: String = "",var hot: Boolean = false,var text: List<String> = listOf(),var Source: String = "",var time:String = "")

data class Temp(var id: Int = 0,var content: String = "")

data class TopNew(var title: String= "",var time: String = "",var Source: String ="",var text: String = "",var updatalist: List<Temp> = listOf())

data class ShowTopNew(var title: String= "",var time: String = "",var Source: String ="",var text: List<String> = listOf(),var updatalist: List<Temp> = listOf())

var allNewsData = mutableListOf<ShowNews>()

var detailData = ShowNews()

var initSearchText = ""

var topNewsData = ShowTopNew()

var searchResData = mutableListOf<ShowNews>()

var showSearchResData = mutableListOf<List<ShowNews>>()

var searchHotData = mutableListOf<List<ShowNews>>()

var hotNewsData = mutableListOf<ShowNews>()

var tempData = mutableListOf<Temp>()

var showTempData = mutableListOf<Temp>()

val gson = Gson()

val spinnerData = arrayListOf<String>("Time ↓","Time ↑")

fun textParser(text: String): List<String>{
    return text.replace("[","").replace("]","").replace("/images/","").split("\n\n")
}

fun getBitmap(context: Context,text: String): Bitmap{
    val input = context.assets.open(text)
    return BitmapFactory.decodeStream(input)
}