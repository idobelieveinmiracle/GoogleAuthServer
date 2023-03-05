package com.rose.data.repository

import com.rose.domain.model.User
import com.rose.domain.repository.UserDataSource
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq
import org.litote.kmongo.setValue

class UserDataSourceImpl(
    database: CoroutineDatabase
) : UserDataSource {

    private val users = database.getCollection<User>()

    override suspend fun getUserInfo(id: String): User? {
        return users.findOne(filter = User::id eq id)
    }

    override suspend fun saveUserInfo(user: User): Boolean {
        val existingUser = users.findOne(filter = User::id eq user.id)
        println(existingUser)
        return if (existingUser == null) {
            users.insertOne(document = user).wasAcknowledged()
        } else {
            true
        }
    }

    override suspend fun deleteUser(id: String): Boolean {
        return users.deleteOne(filter = User::id eq id).wasAcknowledged()
    }

    override suspend fun updateUserInfo(id: String, firstName: String, lastName: String): Boolean {
        return users.updateOne(
            filter = User::id eq id,
            update = setValue(
                property = User::name,
                value = "$firstName $lastName"
            )
        ).wasAcknowledged()
    }
}