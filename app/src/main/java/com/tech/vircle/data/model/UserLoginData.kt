package com.tech.vircle.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


/** signup response **/
data class UserRegistrationResponse(
    val `data`: UserRegistrationData?,
    val message: String?,
    val success: Boolean?
)

data class UserRegistrationData(
    val token: String?,
    val user: User?
)

data class User(
    val _id: String?,
    val age: Int?,
    val avatar: String?,
    val createdAt: String?,
    val email: String?,
    val gender: String?,
    val isOnboard: Boolean?,
    val name: String?,
    val updatedAt: String?
)

data class ForgotPasswordResponse(
    val message: String?,
    val success: Boolean?,
    val userId: String?
)
/** get profile response **/
data class GetUserprofileResponse(
    val `data`: UserprofileData?,
    val message: String?,
    val success: Boolean?
)

data class UserprofileData(
    val user: ProfileUser?
)

data class ProfileUser(
    val _id: String?,
    val age: Int?,
    val avatar: String?,
    val characterstics: List<String?>?,
    val createdAt: String?,
    val email: String?,
    val gender: String?,
    val isOnboard: Boolean?,
    val name: String?,
    val personal_details: String?,
    val surname: String?,
    val updatedAt: String?
)





/** get chat response **/
data class GetChatResponse(
    val `data`: ChatData?,
    val message: String?,
    val success: Boolean?
)
@Parcelize
data class ChatData(
    val chats: List<Chat?>?
): Parcelable
@Parcelize
data class Chat(
    val _id: String?,
    val contactId: ContactId?,
    val createdAt: String?,
    val hasUnreadMessages: Boolean?,
    val isDeleted: Boolean?,
    val lastMessage: LastMessage?,
    val unreadCount: Int?,
    val updatedAt: String?,
    val userId: UserId?
): Parcelable
@Parcelize
data class ContactId(
    val _id: String?,
    val aiAvatar: String?,
    val name: String?
): Parcelable
@Parcelize
data class LastMessage(
    val _id: String?,
    val createdAt: String?,
    val message: String?
): Parcelable
@Parcelize
data class UserId(
    val _id: String?,
    val avatar: String?,
    val email: String?,
    val name: String?
): Parcelable

/** get chat response **/
@Parcelize
data class ContactListResponse(
    val `data`: ContactData?,
    val message: String?,
    val success: Boolean?
): Parcelable
@Parcelize
data class ContactData(
    val contacts: List<ContactList?>?
): Parcelable
@Parcelize
data class ContactList(
    val _id: String?,
    val age: Int?,
    val aiAvatar: String?,
    val at: String?,
    val canTextEvery: String?,
    val characterstics: List<String?>?,
    val createdAt: String?,
    val description: String?,
    val chatIds: String?,
    val expertise: String?,
    val gender: String?,
    val isDeleted: Boolean?,
    val name: String?,
    val on: String?,
    val relationship: String?,
    val type: String?,
    val updatedAt: String?,
    val userId: String?,
    val wantToHear: String?
): Parcelable




/** get message response **/
data class GetUserMessageData(
    val `data`: MessageData?,
    val message: String?,
    val success: Boolean?
)

data class MessageData(
    val messages: List<Message?>?,
    val pagination: Pagination?
)

data class Message(
    val _id: String?,
    val aiContactId: String?,
    val chatId: String?,
    val createdAt: String?,
    val isRead: Boolean?,
    val message: String?,
    val type: String?,
    val updatedAt: String?,
    val userId: String?
)

data class Pagination(
    val hasNextPage: Boolean?,
    val limit: Int?,
    val page: Int?,
    val total: Int?,
    val totalPages: Int?
)


/** get Contact response **/
data class ContactCreateListResponse(
    val `data`: AiContactData?,
    val message: String?,
    val success: Boolean?
)
@Parcelize
data class AiContactData(
    val contact: List<AiContactList?>?
): Parcelable
@Parcelize
data class AiContactList(
    val _id: String?,
    val age: Int?,
    val aiAvatar: String?,
    val at: String?,
    val canTextEvery: String?,
    val characterstics: List<String?>?,
    val createdAt: String?,
    val expertise: String?,
    val description: String?,
    val chatIds: String?,
    val gender: String?,
    val isActive: Boolean?,
    val isDeleted: Boolean?,
    val name: String?,
    val on: String?,
    val relationship: String?,
    val subTitle: String?,
    val title: String?,
    val type: String?,
    val updatedAt: String?,
    val wantToHear: String?,
    var check: Boolean = false
): Parcelable



/** create Contact response **/
data class CreateAiContactResponse(
    val `data`: CreateAiData?,
    val message: String?,
    val success: Boolean?
)

data class CreateAiData(
    val contact: AiContact?
)

data class AiContact(
    val _id: String?,
    val age: Int?,
    val aiAvatar: String?,
    val at: String?,
    val canTextEvery: String?,
    val createdAt: String?,
    val expertise: String?,
    val gender: String?,
    val isDeleted: Boolean?,
    val name: String?,
    val on: Any?,
    val relationship: String?,
    val type: String?,
    val updatedAt: String?


)



@Parcelize
data class AiContactDataCreate(
    val name: String,
    val age: String,
    val gender: String,
    val expertise: String,
    val relationship: String,
    val canTextEvery: String?,
    val description: String?,
    val characteristics: List<String>,
    val on: String?,
    val at: String?,
    val wantToHear: String,
    val type: String
) : Parcelable
