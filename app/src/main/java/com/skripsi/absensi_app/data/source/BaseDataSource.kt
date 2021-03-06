package com.skripsi.absensi_app.data.source

import androidx.lifecycle.LiveData
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody

interface BaseDataSource {
    suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun logoutUser(token: String): LiveData<ApiResponse<UserEntity>>
    suspend fun getUser(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun editProfile(
        header: HashMap<String, String>,
        imageProfile: MultipartBody.Part?,
        userId: RequestBody?,
        name: RequestBody?,
        password: RequestBody?
    ): LiveData<ApiResponse<UserEntity>>
    suspend fun getAllTaskData(
        token: String,
        userId: Int?,
        date: String? = null,
        isAdmin: Int? = null
    ): LiveData<ApiResponse<List<TaskEntity>>>
    suspend fun insertTask(
        token: String,
        userId: Int?,
        descTask: String? = null,
        isAdmin: Int? = 0
    ): LiveData<ApiResponse<TaskEntity>>
    suspend fun markCompleteTask(
        header: HashMap<String, String>,
        idTask: RequestBody?,
        userId: RequestBody?,
        file: MultipartBody.Part?
    ): LiveData<ApiResponse<TaskEntity>>
    suspend fun getAllAttendance(
        token: String,
        userId: Int?,
    ): LiveData<ApiResponse<List<AttendanceEntity>>>
    suspend fun getAttendanceToday(
        token: String,
        employeeId: Int?,
    ): LiveData<ApiResponse<AttendanceEntity>>
    suspend fun attendanceScan(
        token: String,
        userId: Int?,
        qrCode: String?,
        latitude: String?,
        longitude: String?,
    ): LiveData<ApiResponse<AttendanceEntity>>
    suspend fun getEmployee(
        token: String
    ): LiveData<ApiResponse<List<UserEntity>>>
    suspend fun getMyPoint(
        token: String,
        userId: Int?,
    ): LiveData<ApiResponse<UserEntity>>
}