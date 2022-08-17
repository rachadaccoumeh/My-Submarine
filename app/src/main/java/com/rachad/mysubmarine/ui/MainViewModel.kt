package com.rachad.mysubmarine.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.rachad.mysubmarine.pojo.Gyroscope

class MainViewModel : ViewModel() {
    private val database = Firebase.database
    val gyroscope: MutableLiveData<Gyroscope> = MutableLiveData()

    init {
        database.getReference("gyroscope").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gyroscope.value = Gyroscope(
                    snapshot.child("x").getValue<Int>()!!,
                    snapshot.child("y").getValue<Int>()!!,
                    snapshot.child("x").getValue<Int>()!!
                )
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateMotorsSpeed(rightSpeed: Int, leftSpeed: Int) {
        database.getReference("rightSpeed").setValue(rightSpeed)
        database.getReference("leftSpeed").setValue(leftSpeed)
    }

}