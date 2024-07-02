package jp.com.moduleb

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import com.google.android.material.transition.MaterialSharedAxis
import jp.com.moduleb.databinding.ColorItemBinding
import jp.com.moduleb.databinding.FragmentCraeteItemBinding
import java.io.File
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZoneOffset
import java.util.Calendar
import java.util.TimeZone
import java.util.UUID


class CraeteItemFragment(private val isCreate: Boolean = true) : Fragment() {
    private lateinit var b: FragmentCraeteItemBinding
    private var uri: Uri? = null

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { bool ->
            if (bool) {
                val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                    addCategory(Intent.CATEGORY_OPENABLE)
                    type = "image/*"
                }
                getImageLauncher.launch(intent)
            }
        }

    private val getImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
            if (res.resultCode == Activity.RESULT_OK) {
                res.data?.let {
                    uri = it.data
                    b.imagePick.setCompoundDrawablesRelativeWithIntrinsicBounds(
                        null,
                        null,
                        requireContext().getDrawable(R.drawable.baseline_check_24),
                        null
                    )
                }
            }
        }
    private val colorList = listOf<Int>(
        R.color.back_red,
        R.color.back_blue,
        R.color.back_yellow,
        R.color.back_purple,
        R.color.back_green,
        R.color.back_orange
    )

    private var selectColor = R.color.back_red
    private var selectDate = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Y, true).apply {
            duration = 1500
        }
        exitTransition = MaterialSharedAxis(MaterialSharedAxis.Y, false).apply {
            duration = 1500
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        b = FragmentCraeteItemBinding.inflate(inflater)
        return b.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.apply {
            back.setOnClickListener {
                parentFragmentManager.beginTransaction().remove(this@CraeteItemFragment).commit()
            }
            imagePick.setOnClickListener {
                permissionLauncher.launch(android.Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            dateText.setOnClickListener {
                datePicker()
            }
            saveButton.setOnClickListener {
                if (isCreate) {
                    createItem()
                }
                else updateItem()
            }
            if (!isCreate){
               imageFrame.isVisible = false
                titleEdit.setText(detailData.title)
                dateText.text = SimpleDateFormat("d MMM yyyy").format(detailData.date*1000)
                placeEdit.setText(detailData.place)
                selectColor = detailData.color
                selectDate = detailData.date
            }
        }
        colorPicker()
    }

    private fun datePicker() {
        val calendar = Calendar.getInstance(TimeZone.getDefault())
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        DatePickerDialog(requireContext(), object : DatePickerDialog.OnDateSetListener {
            override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
                val pickDate = String.format("%02d-%02d-%02d", year, month + 1, dayOfMonth)
                println(pickDate)
                val parseFormat = SimpleDateFormat("yyyy-MM-dd")
                val parseDate = parseFormat.parse(pickDate)
                selectDate = parseDate.time / 1000
                val showFormat = SimpleDateFormat("d MMM yyyy")
                b.dateText.text = showFormat.format(parseDate)
            }

        }, year, month, day).show()
    }

    private fun createItem() {
        b.apply {
            val title = titleEdit.text.toString()
            val place = placeEdit.text.toString()
            var filePath = ""
            if (title.isNotEmpty() && place.isNotEmpty() && selectDate != 0L) {
                if (uri != null) {
                    filePath = saveImage()
                }
                val addData = Event(title, selectDate, place, selectColor, filePath)
                eventList.add(addData)
                pagerAdapter.notifyDataSetChanged()
                eventFile.outputStream().use {
                    it.write(gson.toJson(eventList).toByteArray())
                    it.flush()
                }
                parentFragmentManager.beginTransaction()
                    .remove(this@CraeteItemFragment)
                    .commit()
            } else Toast.makeText(
                requireContext(),
                "Please enter Title, date and Place.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun updateItem(){
        b.apply {
            val title = titleEdit.text.toString()
            val place = placeEdit.text.toString()
            var filePath = detailData.image
            if (title.isNotEmpty() && place.isNotEmpty() && selectDate != 0L) {
                val changeData = Event(title, selectDate, place, selectColor, filePath)
                val index = eventList.indexOf(detailData)
                eventList[index] = changeData
                pagerAdapter.notifyDataSetChanged()
                eventFile.outputStream().use {
                    it.write(gson.toJson(eventList).toByteArray())
                    it.flush()
                }
                parentFragmentManager.beginTransaction()
                    .remove(this@CraeteItemFragment)
                    .commit()
            } else Toast.makeText(
                requireContext(),
                "Please enter Title, date and Place.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun saveImage(): String {
        val input = requireContext().contentResolver.openInputStream(uri!!)
        val bitmap = BitmapFactory.decodeStream(input)
        val file = File(requireContext().filesDir, UUID.randomUUID().toString())
        file.outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
        return file.absolutePath
    }

    private fun colorPicker() {
        b.apply {
            colorFrame.removeAllViews()
            for (i in colorList.indices) {
                val colorData = colorList[i]
                val view = ColorItemBinding.inflate(layoutInflater)
                view.apply {
                    color.setCardBackgroundColor(requireContext().getColor(colorData))
                    if (colorData == selectColor) {
                        val params = color.layoutParams as FrameLayout.LayoutParams
                        params.setMargins(5)
                        color.layoutParams = params
                    }
                    root.setOnClickListener {
                        selectColor = colorData
                        colorPicker()
                    }
                }
                val spacer = View(requireContext()).apply {
                    layoutParams = LinearLayout.LayoutParams(30, 30)
                }
                colorFrame.addView(view.root)
                colorFrame.addView(spacer)
            }

        }
    }
}