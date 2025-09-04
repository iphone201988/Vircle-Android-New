package com.tech.vircle.ui.home.settings

import android.util.Log
import com.google.gson.JsonObject
import com.tech.vircle.base.BaseViewModel
import com.tech.vircle.data.api.ApiHelper
import com.tech.vircle.utils.Resource
import com.tech.vircle.utils.event.SingleRequestEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject

@HiltViewModel
class SettingsFragmentVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
    val observeCommon = SingleRequestEvent<JsonObject>()
    // get profile api
    fun getProfileApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getProfileApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getProfileApi", "getProfileApi: $e")
            }
        }
    }

    // update profile api
    fun updateProfileApi(url: String,map: HashMap<String, RequestBody>?, part: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForMultipartPut(url,map,part).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("updateProfileApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("updateProfileApi", "updateProfileApi: $e")
            }
        }
    }
    // logout api
    fun logoutApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("logoutApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("logoutApi", "logoutApi: $e")
            }
        }
    }

    // delete api
    fun deleteApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiDeleteAccount(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("deleteApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("deleteApi", "deleteApi: $e")
            }
        }
    }

    // change password api
    fun changePasswordApi(url: String,request:HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiPutForRawBody(url,request).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("changePasswordApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("changePasswordApi", "changePasswordApi: $e")
            }
        }
    }
}