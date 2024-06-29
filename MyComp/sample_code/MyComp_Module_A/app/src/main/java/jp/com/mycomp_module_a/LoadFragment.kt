package jp.com.mycomp_module_a

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.reflect.TypeToken
import jp.com.mycomp_module_a.databinding.FragmentLoadBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File


class LoadFragment : Fragment() {
    private lateinit var b: FragmentLoadBinding
    private var loadCount = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentLoadBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        file = File(requireContext().filesDir,"profile.json")
        if (!file.exists()){
            file.createNewFile()
        }

        b.apply {
            lifecycleScope.launch {
                lineInd.max = 100
                while (loadCount < 100) {
                    lineInd.progress = loadCount
                    loadCount += 1
                    delay(50)
                }
                if (loadCount == 100) {
                    val inputData = file.bufferedReader().use { it.readText() }
                    if (inputData.isNotEmpty()) {
                        userData = gson.fromJson(inputData, object : TypeToken<Profile>() {}.type)
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, MyProfileFragment())
                            .commit()
                    } else {
                        parentFragmentManager.beginTransaction()
                            .replace(R.id.fragmentContainerView, EditProfileFragment())
                            .commit()
                    }
                }
            }
        }
    }


}