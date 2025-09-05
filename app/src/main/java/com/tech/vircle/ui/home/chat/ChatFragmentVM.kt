package com.tech.vircle.ui.home.chat

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
import javax.inject.Inject

@HiltViewModel
class ChatFragmentVM @Inject constructor(private val apiHelper: ApiHelper): BaseViewModel(){
    val observeCommon = SingleRequestEvent<JsonObject>()
    // get chat api
    fun getChatApi(url: String) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetOnlyAuthToken(url).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getChatApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getChatApi", "getChatApi: $e")
            }
        }
    }

    // search chat api
    fun searchChatApi(url: String,data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetQueryToken(url,data).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("searchChatApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("searchChatApi", "searchChatApi: $e")
            }
        }
    }

    // get chat details api
    fun getChatDetailsApi(url: String,data: HashMap<String, Any>) {
        CoroutineScope(Dispatchers.IO).launch {
            observeCommon.postValue(Resource.loading(null))
            try {
                apiHelper.apiGetQueryToken(url,data).let {
                    if (it.isSuccessful) {
                        observeCommon.postValue(Resource.success("getChatDetailsApi", it.body()))
                    } else observeCommon.postValue(
                        Resource.error(handleErrorResponse(it.errorBody(), it.code()), null)
                    )
                }
            } catch (e: Exception) {
                Log.d("getChatDetailsApi", "getChatDetailsApi: $e")
            }
        }
    }



}