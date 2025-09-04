package com.tech.vircle.data.model



data class ProfileModelClass(
    val name: String,
    val type: Int,
    var isChecked: Boolean = false
)

data class PackageDetailsClass(
    val name: String,
    val subName: String,
    val image: Int


)


data class CreateAiModelClass(
    val category: String,
    val image: Int,
    val selectImage: Int,
    var check : Boolean = false
)

data class CommonModelClass(
    val category: String,
    var isStatus : Boolean = false
)


data class UploadAvtarClass(
    val image: Int,
    var check : Boolean = false
)


