package com.rose.domain.repository

import com.rose.domain.model.User

interface UserDataSource {
    suspend fun getUserInfo(id: String): User?
    suspend fun saveUserInfo(user: User): Boolean
    suspend fun deleteUser(id: String): Boolean
    suspend fun updateUserInfo(id: String, firstName: String, lastName: String): Boolean
}