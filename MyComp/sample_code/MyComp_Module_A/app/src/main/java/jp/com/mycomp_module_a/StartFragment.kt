package jp.com.mycomp_module_a

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialSharedAxis
import jp.com.mycomp_module_a.databinding.FragmentStartBinding


class StartFragment : Fragment() {
   private lateinit var b: FragmentStartBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.X,false)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentStartBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.apply {
            startButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainerView,LoadFragment())
                    .commit()
            }
        }
    }


}