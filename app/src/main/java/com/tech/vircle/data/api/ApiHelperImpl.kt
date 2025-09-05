package com.tech.vircle.data.api

import com.tech.vircle.base.local.SharedPrefManager
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Part
import javax.inject.Inject

class ApiHelperImpl @Inject constructor(private val apiService: ApiService, private val sharedPrefManager: SharedPrefManager) :
    ApiHelper {

    override suspend fun apiForRawBody(request: HashMap<String, Any>,url:String): Response<JsonObject> {
        return apiService.apiForRawBody(request,url)
    }

    override suspend fun apiPostForRawBody(
        url: String,
        request: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPostForRawBody(getTokenFromSPref(), url, request)
    }

    override suspend fun apiForFormData(data: HashMap<String, Any>, url: String): Response<JsonObject> {
        return apiService.apiForFormData(data,url)
    }

    override suspend fun apiForFormDataPut(
        data: HashMap<String, Any>,
        url: String
    ): Response<JsonObject> {
        return apiService.apiForFormDataPut(data,url, getTokenFromSPref())
    }

    override suspend fun apiGetOutWithQuery(url:String): Response<JsonObject> {
        return apiService.apiGetOutWithQuery(url)
    }

    override suspend fun apiGetOnlyAuthToken(url: String): Response<JsonObject> {
        return apiService.apiGetOnlyAuthToken(url,getTokenFromSPref())
    }

    override suspend fun apiGetQueryToken(url: String,data: HashMap<String, Any>): Response<JsonObject> {
        return apiService.apiGetQueryToken(url,getTokenFromSPref(),data)
    }

    override suspend fun apiDeleteAccount(url: String): Response<JsonObject> {
        return apiService.apiDeleteAccount(url,getTokenFromSPref())
    }

    override suspend fun apiGetWithQuery(data: HashMap<String, String>, url: String): Response<JsonObject> {
        return apiService.apiGetWithQuery(url,data)
    }

    override suspend fun apiForPostMultipart(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart(url, map, part)
    }

    override suspend fun apiForPostOnlyImageMultipart(
        url: String,
        part: MultipartBody.Part?,
    ): Response<JsonObject> {
        return apiService.apiForPostOnlyImageMultipart(url, part,getTokenFromSPref())
    }

    override suspend fun apiForPostOnlyAiAvtar(
        url: String,
        map: HashMap<String, RequestBody>?,
    ): Response<JsonObject> {
        return apiService.apiForPostOnlyAiAvtar(url, map,getTokenFromSPref())
    }

    override suspend fun apiForPostMultipart1(
        url: String,
        map: HashMap<String, RequestBody>?,
    ): Response<JsonObject> {
        return apiService.apiForPostMultipart1(url, map,getTokenFromSPref())
    }

    override suspend fun apiForMultipartPut(
        url: String,
        map: HashMap<String, RequestBody>?,
        part: MultipartBody.Part?
    ): Response<JsonObject> {
        return apiService.apiForMultipartPut(url,getTokenFromSPref(), map, part)
    }


    override suspend fun apiForMultipartPut1(
        url: String,
        map: HashMap<String, RequestBody>?,
    ): Response<JsonObject> {
        return apiService.apiForMultipartPut1(url,getTokenFromSPref(), map)
    }

    override suspend fun apiPutForRawBody(
        url: String,
        map: HashMap<String, Any>,
    ): Response<JsonObject> {
        return apiService.apiPutForRawBody(url,getTokenFromSPref(), map)
    }

    private fun getTokenFromSPref(): String {
        return "Bearer ${
            sharedPrefManager.getToken()
        }"
    }

}