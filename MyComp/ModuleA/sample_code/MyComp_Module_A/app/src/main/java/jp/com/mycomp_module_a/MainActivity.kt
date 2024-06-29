package jp.com.mycomp_module_a

import android.graphics.Color
import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.annotation.RequiresApi
import jp.com.mycomp_module_a.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
private lateinit var b: ActivityMainBinding
    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.apply {
            statusBarColor = Color.TRANSPARENT
            setDecorFitsSystemWindows(false)
        }
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)
        b.apply {
            backImage.setRenderEffect(RenderEffect.createBlurEffect(30f,30f,Shader.TileMode.MIRROR))
        }
    }
}