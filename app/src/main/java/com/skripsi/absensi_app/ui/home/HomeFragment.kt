package com.skripsi.absensi_app.ui.home

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.snackbar.Snackbar
import com.skripsi.absensi_app.R
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.databinding.FragmentHomeBinding
import com.skripsi.absensi_app.ui.bsuploadfile.BottomSheetUploadFileTask
import com.skripsi.absensi_app.utils.pref.UserPref
import com.skripsi.absensi_app.viewmodel.ViewModelFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class HomeFragment : Fragment() {
    private lateinit var _bind: FragmentHomeBinding
    private var filePath: String? = null
    private var taskEntity: TaskEntity? = null
    private var part: MultipartBody.Part? = null

    private val homeViewModel: HomeViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _bind = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return _bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        init()
        // observe view model
        observerViewModel()
        getDateTimeToday()
    }

    private fun init() {
        setViewModel()
    }

    private fun setViewModel() {
        // set greeting name
        val name = context?.let { UserPref.getUserData(it)?.name }
        homeViewModel.setGreeting(name)

        // get all task
        val userPref = context?.let { UserPref.getUserData(it) }
        userPref?.token?.let { token ->
            homeViewModel.attendanceToday(
                token, userPref.id
            )
        }
    }

    internal var buttonListener: BottomSheetUploadFileTask.ButtonListener =
        object : BottomSheetUploadFileTask.ButtonListener {
            override fun pickFile() {
                openFile()
            }

            override fun send() {
                val headers = HashMap<String, String>()
                headers["Authorization"] =
                    "Bearer ${context?.let { context -> UserPref.getUserData(context)?.token }}"
                val taskId =
                    taskEntity?.id.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val userId =
                    context?.let { context ->
                        UserPref.getUserData(context)?.id.toString()
                            .toRequestBody("text/plain".toMediaTypeOrNull())
                    }

                if (filePath != null) {
                    val file = File(filePath)
                    val reqFile = file.asRequestBody("*/*".toMediaTypeOrNull())
                    part = MultipartBody.Part.createFormData("file", file.name, reqFile)
                }

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 1000) {
            val uri = data?.data
            val filePath = arrayOf(MediaStore.Images.Media.DATA)
            val cursor =
                uri?.let { uri -> context?.contentResolver?.query(uri, filePath, null, null, null) }
            cursor?.moveToFirst()
            val columnIndex = cursor?.getColumnIndex(filePath[0])
            val picturePath = columnIndex?.let { columnIndex -> cursor.getString(columnIndex) }
            cursor?.close()
            this.filePath = picturePath.toString()
            val splitFileName = this.filePath?.split("/")
            val bundle = Bundle().apply {
                putString(BottomSheetUploadFileTask.FILENAME, splitFileName?.last())
            }
            BottomSheetUploadFileTask().apply {
                arguments = bundle
            }.show(
                childFragmentManager,
                BottomSheetUploadFileTask.TAG
            )
        }
    }

    private fun pickFile() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "*/*"
        startActivityForResult(intent, 1000)
    }

    private fun openFile() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context?.let { context ->
                    ActivityCompat.checkSelfPermission(
                        context,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                activity?.let { activity ->
                    ActivityCompat.requestPermissions(
                        activity,
                        arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                        1
                    )
                }
            } else {
                pickFile()
            }
        } else {
            pickFile()
        }
    }

    private fun getDateTimeToday() {
        var date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.getDefault())
            current.format(formatter)
        } else {
            Date().toString()
        }

        _bind.detailAttendance.dateToday.text = date
    }

    private fun observerViewModel() {

        homeViewModel.greeting.observe(viewLifecycleOwner, {
            _bind.txtGreeting.text = getString(R.string.title_greeting_name, it)
        })

        homeViewModel.loading.observe(viewLifecycleOwner, {
            if (it) {
                 _bind.detailAttendance.progressBar.visibility = View.VISIBLE
            } else {
                 _bind.detailAttendance.progressBar.visibility = View.GONE
            }
        })

        homeViewModel.msgGetTaskData.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.msgAttendanceToday.observe(viewLifecycleOwner, {
            if (it != null) {
                Snackbar.make(_bind.root, it, Snackbar.LENGTH_SHORT).show()
            }
        })

        homeViewModel.msgPoint.observe(viewLifecycleOwner, {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
        })

        homeViewModel.attendanceToday.observe(viewLifecycleOwner, {
            if (it != null) {
                _bind.detailAttendance.timeIn.text =
                    if (it.timeComes == "0") getString(R.string.time) else it.timeComes
                _bind.detailAttendance.timeOut.text =
                    if (it.timeGohome == "0") getString(R.string.time) else it.timeGohome
            }
        })

    }
}