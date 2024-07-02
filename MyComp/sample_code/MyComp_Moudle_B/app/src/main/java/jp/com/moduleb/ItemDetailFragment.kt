package jp.com.moduleb

import android.app.AlertDialog
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.transition.MaterialSharedAxis
import com.google.gson.reflect.TypeToken
import jp.com.moduleb.databinding.FragmentItemDetailBinding
import java.text.SimpleDateFormat


class ItemDetailFragment : Fragment() {
    private lateinit var b: FragmentItemDetailBinding

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
        b = FragmentItemDetailBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        b.apply {
            title.text = detailData.title
            val dateFormat = SimpleDateFormat("d MMM yyyy")
            date.text = dateFormat.format(detailData.date * 1000)
            place.text = detailData.place
            if (detailData.image != "") {
                image.setImageURI(Uri.parse(detailData.image))
            }

            back.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .remove(this@ItemDetailFragment)
                    .commit()
            }
            deleteButton.setOnClickListener {
                alertDialog()
            }
            editButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView,CraeteItemFragment(false))
                    .commit()
            }
            accountButton.setOnClickListener {
                parentFragmentManager.beginTransaction()
                    .add(R.id.fragmentContainerView,AccountFragment())
                    .commit()
            }
        }
    }

    private fun alertDialog() {
        AlertDialog.Builder(requireContext()).apply {
            setTitle("Caution")
            setMessage("If you delete the event. it deletes in this device completely.")
            setNegativeButton("Cancel") { _, _ -> }
            setPositiveButton("Delete") { _, _ ->
                eventList.remove(detailData)
                filterList.remove(detailData)
                eventFile.outputStream().use {
                    it.write(gson.toJson(eventList).toByteArray())
                    it.flush()
                }
                pagerAdapter.notifyDataSetChanged()
                eventListAdapter.notifyDataSetChanged()
                parentFragmentManager.beginTransaction()
                    .remove(this@ItemDetailFragment)
                    .commit()
            }
        }.show()
    }


}