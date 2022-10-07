package com.example.chatapp.viwemodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.chatapp.App
import com.example.chatapp.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue

class UserViewModel : ViewModel() {
    private val _result: MutableLiveData<Boolean> = MutableLiveData()
    val result: LiveData<Boolean> get() = _result

    private val _getUsers: MutableLiveData<List<User>> = MutableLiveData()
    val getUsers: LiveData<List<User>> get() = _getUsers

    private val _my: MutableLiveData<User> = MutableLiveData()
    val my: LiveData<User> get() = _my

    private val _searchUsers: MutableLiveData<List<User>> = MutableLiveData()
    val searchUsers: LiveData<List<User>> get() = _searchUsers
    fun userLogin(email: String, password: String) {
        try {
            App.firebaseAuthInstance?.let { instance ->
                instance.signInWithEmailAndPassword(email, password)
                    .addOnFailureListener {}
                    .addOnCompleteListener {
                        if (it.isSuccessful) {
                            instance.currentUser?.let {
                                _result.value = true
                            }
                        } else {
                            _result.value = false
                        }
                    }
            }
        } catch (e: Exception) {
            _result.value = false
        }
    }

    fun getAllUser() {
        val allUsers = ArrayList<User>()
        val myUid = FirebaseAuth.getInstance().currentUser?.uid.toString()

        App.firebaseDatabaseInstance?.getReference("User")?.child("users")
            ?.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {}
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (data in snapshot.children) {
                        val item = data.getValue<User>()
                        item?.let {
                            if (it.uid.equals(myUid)) {
                                _my.value = it
                            } else {
                                allUsers.add(it)
                            }
                        }
                    }
                    _getUsers.value = allUsers
                }
            })
    }

    fun getSearchUsers(searchUser: String) {
        val searchUserList = ArrayList<User>()

        if (searchUser == "") {
            _searchUsers.value = getUsers.value
        } else {
            getUsers.value?.filter { it.name?.contains(searchUser) == true }?.map {
                searchUserList.add(it)
            }
            _searchUsers.value = searchUserList
        }
    }
}