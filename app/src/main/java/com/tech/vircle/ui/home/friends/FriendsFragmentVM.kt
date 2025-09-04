package com.tech.vircle.ui.home.friends

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
class FriendsFragmentVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
    val observeCommon = SingleRequestEvent<JsonObject>()
    // get contact list api
    fun getContactApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getContactApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getContactApi", "getContactApi: $e")
            }
        }
    }

    // get contact type api
    fun getContactTypesApi(url: String,data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetQueryToken(url,data).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getContactTypesApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getContactTypesApi", "getContactTypesApi: $e")
            }
        }
    }

    // create ai contact api
    fun createAiContactApi(url: String,data: HashMap<String, RequestBody>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostMultipart1(url,data).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("createAiContactApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("createAiContactApi", "createAiContactApi: $e")
            }
        }
    }

    // update ai contact image  api
    fun updateAiImageApi(url: String,part: MultipartBody.Part?) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForPostOnlyImageMultipart(url,part).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("updateAiImageApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("updateAiImageApi", "createAiImageApi: $e")
            }
        }
    }



    // update ai contact api
    fun updateAiContactApi(url: String,data: HashMap<String, RequestBody>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiForMultipartPut1(url,data).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("updateAiContactApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("updateAiContactApi", "updateAiContactApi: $e")
            }
        }
    }

    // delete ai contact api
    fun deleteAiContactApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiDeleteAccount(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("deleteAiContactApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("deleteAiContactApi", "deleteAiContactApi: $e")
            }
        }
    }

    // clear chat api
    fun clearChatAi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiDeleteAccount(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("clearChatAi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("clearChatAi", "clearChatAi: $e")
            }
        }
    }
}