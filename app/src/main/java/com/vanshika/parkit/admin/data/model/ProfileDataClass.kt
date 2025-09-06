package com.vanshika.parkit.admin.data.model

data class ProfileDataClass(
    val profileId: String = "",
    val email: String = "",
    val userName: String = "",
    val profilePicUrl: String ?= null
)