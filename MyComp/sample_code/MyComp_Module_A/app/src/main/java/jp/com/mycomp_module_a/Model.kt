package jp.com.mycomp_module_a

import com.google.gson.Gson
import java.io.File

data class Profile(var name: String ="",var age: Int = -1,var desc: String = "")

lateinit var file: File

var userData = Profile()

val gson = Gson()