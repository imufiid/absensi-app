package com.skripsi.absensi_app.data.source.remote

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.load.HttpException
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.skripsi.absensi_app.api.ApiConfig
import com.skripsi.absensi_app.api.ApiConfiguration
import com.skripsi.absensi_app.data.source.local.entity.AttendanceEntity
import com.skripsi.absensi_app.data.source.local.entity.TaskEntity
import com.skripsi.absensi_app.data.source.local.entity.UserEntity
import com.skripsi.absensi_app.data.source.remote.response.ApiResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.IOException

class RemoteDataSource(private val api: ApiConfiguration) {

    companion object {
        @Volatile
        private var instance: RemoteDataSource? = null
        fun getInstance(apiConfiguration: ApiConfiguration): RemoteDataSource =
            instance ?: synchronized(this) {
                instance ?: RemoteDataSource(apiConfiguration)
            }
    }

    suspend fun loginUser(
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()

        try {
            val data = api.create().login(email, password)
            when (data.meta?.code) {
                200 -> result.value = ApiResponse.success(data.data)
                404 -> result.value = ApiResponse.empty(data.meta.message)
                else -> result.value = ApiResponse.failed(data.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun registrationUser(
        name: String?,
        nik: String?,
        email: String?,
        password: String?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()

        try {
            val data = api.create().registerUser(name, nik, email, password)
            when (data.meta?.code) {
                201 -> result.value = ApiResponse.success(data.data, data.meta.message)
                404 -> result.value = ApiResponse.empty(data.meta.message)
                else -> result.value = ApiResponse.failed(data.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun logout(
        token: String
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = api.create().logout(token)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun getUser(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = api.create().getUser(token, userId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error(throwable.message)
            }
        }
        return result
    }

    suspend fun getAllAttendance(
        userId: Int?,
        token: String
    ): LiveData<ApiResponse<List<AttendanceEntity>>> {
        val result = MutableLiveData<ApiResponse<List<AttendanceEntity>>>()

        try {
            val response = api.create().showAllAttendance(token, userId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun getAttendanceToday(
        token: String,
        employeeId: Int?
    ): LiveData<ApiResponse<AttendanceEntity>> {
        val result = MutableLiveData<ApiResponse<AttendanceEntity>>()
        try {
            val response = api.create().showAttendance(token, employeeId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            val crash = FirebaseCrashlytics.getInstance()
            crash.log(throwable.localizedMessage)
            throwable.message?.let { crash.log("Message:  $it") }
            crash.recordException(throwable)
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun attendanceCome(
        token: String,
        employeeId: Int?,
        qrCode: String?,
        latitude: String?,
        longitude: String?
    ): LiveData<ApiResponse<AttendanceEntity>> {
        val result = MutableLiveData<ApiResponse<AttendanceEntity>>()
        try {
            val response =
                api.create().attendanceScan(token, employeeId, qrCode, latitude, longitude)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> {
                    result.value = ApiResponse.error("Unknown error")
                    throwable.message?.let { Log.d("UNKNOWN_ERROR", it) }
                }
            }
        }

        return result
    }

    suspend fun getAllTaskData(
        userId: Int?,
        date: String? = null,
        isAdmin: Int? = null,
        token: String
    ): LiveData<ApiResponse<List<TaskEntity>>> {
        val result = MutableLiveData<ApiResponse<List<TaskEntity>>>()

        try {
            val response = api.create().showAllTask(token, userId, date, isAdmin)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            FirebaseCrashlytics.getInstance().recordException(throwable)
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }

        return result
    }

    suspend fun getEmployee(
        token: String
    ): LiveData<ApiResponse<List<UserEntity>>> {
        val result = MutableLiveData<ApiResponse<List<UserEntity>>>()

        try {
            val response = api.create().getEmployee(token)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun insertTask(
        token: String,
        userId: Int?,
        descTask: String?,
        isAdmin: Int?
    ): LiveData<ApiResponse<TaskEntity>> {
        val result = MutableLiveData<ApiResponse<TaskEntity>>()

        try {
            val response = api.create().insertTask(token, userId, descTask, isAdmin)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> result.value = ApiResponse.error("Network Error")
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> result.value = ApiResponse.error("Unknown error")
            }
        }
        return result
    }

    suspend fun markCompleteTask(
        header: HashMap<String, String>,
        idTask: RequestBody?,
        userId: RequestBody?,
        file: MultipartBody.Part?
    ): LiveData<ApiResponse<TaskEntity>> {
        val result = MutableLiveData<ApiResponse<TaskEntity>>()

        try {
            val response = api.create().markComplete(header, idTask, userId, file)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    result.value = ApiResponse.error("Network Error")
                    throwable.message?.let { Log.d("NETWORK_ERROR", it) }
                }
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> {
                    result.value = ApiResponse.error("Unknown error")
                    throwable.message?.let { Log.d("ERROR_NETWORK", it) }
                }
            }
        }
        return result
    }

    suspend fun editProfile(
        header: HashMap<String, String>,
        imageProfile: MultipartBody.Part?,
        userId: RequestBody?,
        name: RequestBody?,
        password: RequestBody?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response =
                api.create().editProfile(header, imageProfile, userId, name, password)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    result.value = ApiResponse.error("Network Error")
                }
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> {
                    result.value = ApiResponse.error("Unknown Error")
                }
            }
        }
        return result
    }

    suspend fun getMyPoint(
        token: String,
        userId: Int?
    ): LiveData<ApiResponse<UserEntity>> {
        val result = MutableLiveData<ApiResponse<UserEntity>>()
        try {
            val response = api.create().getMyPoint(token, userId)
            when (response.meta?.code) {
                200 -> result.value = ApiResponse.success(response.data, response.meta.message)
                404 -> result.value = ApiResponse.empty(response.meta.message)
                else -> result.value = ApiResponse.failed(response.meta?.message)
            }
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> {
                    result.value = ApiResponse.error("Network Error")
                }
                is HttpException -> {
                    val code = throwable.statusCode
                    val errorResponse = throwable.message
                    result.value = ApiResponse.error("Error $errorResponse")
                }
                else -> {
                    result.value = ApiResponse.error("Unknown Error")
                }
            }
        }
        return result
    }

}