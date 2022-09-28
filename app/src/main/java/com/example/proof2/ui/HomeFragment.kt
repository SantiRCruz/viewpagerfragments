package com.example.proof2.ui

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.example.proof2.R
import com.example.proof2.core.Constants
import com.example.proof2.data.get
import com.example.proof2.data.models.History
import com.example.proof2.databinding.FragmentHomeBinding
import com.example.proof2.databinding.FragmentMapBinding
import com.google.android.material.snackbar.Snackbar
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import org.json.JSONObject
import org.json.JSONTokener
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.stream.Collectors


class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var binding: FragmentHomeBinding
    private var dates = mutableListOf<Date>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        obtainHistory()
        clicks()
    }

    private fun clicks() {
        binding.sw.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                val datesDesc = dates.sortedDescending()
                binding.spDates.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    datesDesc
                )
            }else{
                val datesAsc = dates.sorted()
                binding.spDates.adapter = ArrayAdapter(
                    requireContext(),
                    android.R.layout.simple_spinner_dropdown_item,
                    datesAsc
                )
            }
        }
    }

    private fun obtainHistory() {
        val sharedPreferences =
            requireActivity().getSharedPreferences(Constants.USER, Context.MODE_PRIVATE)
        val id = sharedPreferences.getString("id", "")
        Constants.okhttp.newCall(get("symptoms_history?user_id=$id")).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("onFailure: ", e.message.toString())
            }

            override fun onResponse(call: Call, response: Response) {
                val json = JSONTokener(response.body!!.string()).nextValue() as JSONObject
                val data = json.getJSONArray("data")
                for (i in 0 until data.length()) {
                    val item = data.getJSONObject(i)
                    dates.add(
                        SimpleDateFormat("yyyy-mm-dd hh:mm:ss").parse(item.getString("date"))
                    )
                }
                requireActivity().runOnUiThread {
                    if (binding.sw.isChecked) {
                        val datesDesc = dates.sortedDescending()
                        binding.spDates.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            datesDesc
                        )
                    } else {
                        val datesAsc = dates.sorted()
                        binding.spDates.adapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_spinner_dropdown_item,
                            datesAsc
                        )
                    }
                }
            }
        })
    }
}