package com.example.mehulroom2024

import androidx.lifecycle.LiveData

class UserRepository(private val userDao: UserDao) {


    suspend fun addUserData(user: User) {
        userDao.addUserData(user)
    }

    suspend fun updateUserData(user: User) {
        userDao.updateUserData(user)
    }

    suspend fun deleteUserData(user: User) {
        userDao.deleteUserData(user)
    }

    fun readAllUserData(): LiveData<List<User>> = userDao.readUserData()

    fun login(email: String, password: String) = userDao.login(email, password)
}