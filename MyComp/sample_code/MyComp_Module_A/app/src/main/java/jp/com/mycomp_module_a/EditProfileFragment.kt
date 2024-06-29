package jp.com.mycomp_module_a

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnLayout
import com.google.android.material.transition.MaterialSharedAxis
import jp.com.mycomp_module_a.databinding.FragmentEditProfileBinding
import java.util.Objects


class EditProfileFragment : Fragment() {
private lateinit var b: FragmentEditProfileBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X,true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentEditProfileBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            nameEdit.setText(userData.name)
            if (userData.age != -1) ageEdit.setText(userData.age.toString())
            descEdit.setText(userData.desc)
            editFrame.doOnLayout {
                val initHeight = editFrame.height
                var targetHeight = cardFrame.height - 50
                val frameAnimator = ValueAnimator.ofInt(initHeight,targetHeight).apply {
                    addUpdateListener { anim->
                        val value = anim.animatedValue as Int
                        val params = editFrame.layoutParams
                        params.height = value
                        editFrame.layoutParams = params
                    }
                    duration = 500
                }
                frameAnimator.start()
            }
            createButton.setOnClickListener {
                saveProf()
            }
        }
    }
    private fun saveProf(){
        b.apply {
            if (nameEdit.text.isNotEmpty()&&ageEdit.text.isNotEmpty()&&descEdit.text.isNotEmpty()) {
                val name = nameEdit.text.toString()
                val age = ageEdit.text.toString().toInt()
                val desc = descEdit.text.toString()
                if (age in 18..60) {
                    userData = Profile(name, age, desc)
                    file.outputStream().use {
                        it.write(gson.toJson(userData).toByteArray())
                        it.flush()
                    }
                    parentFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainerView, MyProfileFragment())
                        .commit()
                }
                else Toast.makeText(requireContext(), "Age should enter range of 18 to 60.", Toast.LENGTH_SHORT).show()
            }
            else Toast.makeText(requireContext(), "All input field should not be empty.", Toast.LENGTH_SHORT).show()
        }
    }


}