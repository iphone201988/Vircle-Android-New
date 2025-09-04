package com.tech.vircle.data.api

import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.FieldMap
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.PartMap
import retrofit2.http.QueryMap
import retrofit2.http.Url


interface ApiService {

//    @Header("Authorization") token: String,


    @POST
    suspend fun apiForRawBody(
        @Body data: HashMap<String, Any>, @Url url: String
    ): Response<JsonObject>

    @POST
    suspend fun apiPostForRawBody(
        @Header("Authorization") token: String,
        @Url url: String,
        @Body data: HashMap<String, Any>
    ): Response<JsonObject>

    @PUT
    suspend fun apiPutForRawBody(
        @Url url: String,
        @Header("Authorization") token: String,
        @Body data: HashMap<String, Any>,
    ): Response<JsonObject>

    @FormUrlEncoded
    @POST
    suspend fun apiForFormData(
        @FieldMap data: HashMap<String, Any>, @Url url: String
    ): Response<JsonObject>


    @FormUrlEncoded
    @PUT
    suspend fun apiForFormDataPut(
        @FieldMap data: HashMap<String, Any>,
        @Url url: String,
        @Header("Authorization") token: String
    ): Response<JsonObject>


    @GET
    suspend fun apiGetOutWithQuery(@Url url: String): Response<JsonObject>

    @GET
    suspend fun apiGetOnlyAuthToken(
        @Url url: String, @Header("Authorization") token: String
    ): Response<JsonObject>

    @GET
    suspend fun apiGetQueryToken(
        @Url url: String,
        @Header("Authorization") token: String,
        @QueryMap data: HashMap<String, Any>
    ): Response<JsonObject>

    @DELETE
    suspend fun apiDeleteAccount(
        @Url url: String, @Header("Authorization") token: String
    ): Response<JsonObject>


    @GET
    suspend fun apiGetWithQuery(
        @Url url: String, @QueryMap data: HashMap<String, String>
    ): Response<JsonObject>



    @Multipart
    @JvmSuppressWildcards
    @POST
    suspend fun apiForPostMultipart(
        @Url url: String,
        @PartMap data: Map<String, RequestBody>?,
        @Part parts: MultipartBody.Part?
    ): Response<JsonObject>

    @Multipart
    @JvmSuppressWildcards
    @POST
    suspend fun apiForPostMultipart1(
        @Url url: String,
        @PartMap data: Map<String, RequestBody>?,
        @Header("Authorization") token: String,
    ): Response<JsonObject>

    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForPostOnlyImageMultipart(
        @Url url: String,
        @Part profilePic: MultipartBody.Part?,
        @Header("Authorization") token: String,
    ): Response<JsonObject>

    @Headers(Constants.HEADER_API)
    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForMultipartPut(
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>?,
        @Part profilePic: MultipartBody.Part?,
    ): Response<JsonObject>


    @Headers(Constants.HEADER_API)
    @Multipart
    @JvmSuppressWildcards
    @PUT
    suspend fun apiForMultipartPut1(
        @Url url: String,
        @Header("Authorization") token: String,
        @PartMap data: Map<String, RequestBody>?,
    ): Response<JsonObject>


}