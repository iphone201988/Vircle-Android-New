package com.tech.vircle.data.api

import com.tech.vircle.BuildConfig


object Constants {


    const val API_KEY = BuildConfig.API_KEY

    const val BASE_URL = BuildConfig.BASE_URL

    const val SOCKET_URL = BuildConfig.SOCKET_URL

    const val MEDIA_BASE_URL = BuildConfig.MEDIA_BASE_URL

    /**************** API LIST *****************/
    const val HEADER_API = "X-API-Key:lkcMuYllSgc3jsFi1gg896mtbPxIBzYkEL"
    const val USER_REGISTER = "user/register"
    const val USER_LOGIN = "user/login"
    const val USER_COMPLETE_REGISTER = "user/completeRegistration"
    const val USER_SOCIAL_LOGIN = "user/socialLogin"
    const val USER_FORGOT_PASSWORD = "user/forgetPassword"
    const val USER_VERIFY_OTP = "user/verify-otp"
    const val USER_RESET_PASSWORD = "user/resetPassword"
    const val USER_CHANGE_PASSWORD = "user/changePassword"
    const val USER_LOGOUT = "user/logout"
    const val USER_DELETE_USER = "user/deleteUser"
    const val USER_GET_PROFILE = "user/getUser"
    const val USER_UPDATE_PROFILE = "user/updateProfile"
    const val USER_GET_ALL_ELEMENTS = "user/getElements"
    const val GET_CHATS = "chat/getChats"
    const val GET_CHATS_MESSAGES = "chat/getChatMessages"
    const val AI_CONTACT = "aiContact/getAiContact"
    const val AI_CONTACT_ADMIN = "aiContact/getAdminAiContacts"
    const val AI_CONTACT_ADD = "aiContact/addAiContact"
    const val AI_CONTACT_UPDATE = "aiContact/updateAiContact"
    const val AI_CONTACT_DELETE = "aiContact/deleteAiContact"
    const val CLEAR_CHAT = "chat/deleteChatMessages"




}