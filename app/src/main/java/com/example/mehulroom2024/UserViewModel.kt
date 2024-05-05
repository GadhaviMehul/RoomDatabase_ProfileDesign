package com.example.mehulroom2024

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel : ViewModel() {

    private val repository: UserRepository
    private val readAllUserData: LiveData<List<User>>

    init {
        val userDao = UserDatabase.getUserDatabase().userDao()
        repository = UserRepository(userDao)
        readAllUserData = repository.readAllUserData()
    }

    fun addUserData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUserData(user)
        }
    }

    fun updateUserData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateUserData(user)
        }
    }

    fun deleteUserData(user: User) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteUserData(user)
        }
    }

    fun login(email: String, password: String): MutableLiveData<User> {
        val observer = MutableLiveData<User>()
        viewModelScope.launch(Dispatchers.IO) {
            observer.postValue(repository.login(email, password))
        }
        return observer
    }
}