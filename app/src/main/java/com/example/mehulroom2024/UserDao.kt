package com.example.mehulroom2024

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addUserData(user: User)

    @Update
    suspend fun updateUserData(user: User)

    @Delete
    suspend fun deleteUserData(user: User)

    @Query("SELECT * FROM UserTable ORDER BY id ASC")
    fun readUserData(): LiveData<List<User>>

    @Query("SELECT * FROM UserTable WHERE email LIKE :email AND password LIKE :password")
    fun login(email: String, password: String): User


}