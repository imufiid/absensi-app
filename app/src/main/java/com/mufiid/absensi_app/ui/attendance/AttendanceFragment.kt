package com.mufiid.absensi_app.ui.attendance

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mufiid.absensi_app.R
import com.mufiid.absensi_app.viewmodel.ViewModelFactory

class AttendanceFragment : Fragment() {

    private lateinit var attendanceViewModel: AttendanceViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val factory = ViewModelFactory.getInstance(requireActivity())
        attendanceViewModel =
                ViewModelProvider(this, factory).get(AttendanceViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_attendance, container, false)
        attendanceViewModel.text.observe(viewLifecycleOwner, {

        })
        return root
    }
}