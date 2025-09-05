package com.tech.vircle.ui.auth

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
class AuthCommonVM @Inject constructor(
    private val apiHelper: ApiHelper,
) : BaseViewModel() {
    val observeCommon = SingleRequestEvent<JsonObject>()
    // create account api
    fun createAccount(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("createAccount", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("createAccount", "createAccount: $e")
            }
        }
    }
    // login account api
    fun loginAccount(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("loginAccount", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("loginAccount", "loginAccount: $e")
            }
        }
    }
    // social account api
    fun socialLogin(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("socialLogin", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("socialLogin", "socialLogin: $e")
            }
        }
    }
    // complete account api
    fun completeRegistration(
        url: String, map: HashMap<String, RequestBody>, parts: MultipartBody.Part?
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipart(url, map, parts).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("completeRegistration", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("completeRegistration", "loginUser: $e")
            }
        }
    }

    // forgot Email api
    fun forgotEmailApi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("forgotEmailApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("forgotEmailApi", "forgotEmailApi: $e")
            }
        }
    }

    // code verification api
    fun codeVerificationApi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("codeVerificationApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("codeVerificationApi", "codeVerificationApi: $e")
            }
        }
    }

    // change Password  api
    fun changePasswordApi(url: String, request: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForRawBody(request, url).let {
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

    // get avtar api
    fun getAvtarApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getAvtarApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getAvtarApi", "getAvtarApi: $e")
            }
        }
    }

}

