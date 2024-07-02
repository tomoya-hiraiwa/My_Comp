package jp.com.moduleb

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.reflect.TypeToken
import jp.com.moduleb.databinding.FragmentAccountBinding


class AccountFragment : Fragment() {
private lateinit var b: FragmentAccountBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.X, true).apply {
            duration = 1500
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X, false).apply {
            duration = 1500
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentAccountBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val input = requireContext().assets.open("user.json")
        userData = gson.fromJson(input.bufferedReader().use { it.readText() },object: TypeToken<Account>(){}.type)
        b.apply {
            back.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .remove(this@AccountFragment)
                    .commit()
            }
            name.text = userData.name
            position.text = userData.position
            empNo.text = userData.number.toString()

        }
    }


}