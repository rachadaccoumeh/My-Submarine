package com.rachad.mysubmarine.ui

import androidx.core.math.MathUtils.clamp
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
import com.rachad.mysubmarine.pojo.Ballast
import com.rachad.mysubmarine.pojo.Gyroscope


class MainViewModel : ViewModel() {
    private var database = FirebaseDatabase.getInstance()
    val gyroscope: MutableLiveData<Gyroscope> = MutableLiveData()
    val ballast: MutableLiveData<Ballast> = MutableLiveData()
    val ballastIndex: MutableLiveData<Ballast> = MutableLiveData()

    init {
        ballast.value = Ballast(0, 0, 0, 0, 0)
        ballastIndex.value = Ballast(null, 0, 0, 0, 0)
        database.getReference("data").child("gyroscope").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                gyroscope.value = Gyroscope(
                    snapshot.child("x").getValue<Int>()!!,
                    snapshot.child("y").getValue<Int>()!!
                )
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
        database.getReference("data").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ballastIndex.value = Ballast(
                    null,
                    snapshot.child("realIndex1").getValue<Int>()!!,
                    snapshot.child("realIndex2").getValue<Int>()!!,
                    snapshot.child("realIndex3").getValue<Int>()!!,
                    snapshot.child("realIndex4").getValue<Int>()!!
                )

            }

            override fun onCancelled(error: DatabaseError) {
            }
        })
    }

    fun updateMotorsSpeed(rightSpeed: Int, leftSpeed: Int) {
        database.getReference("data").child("motorRightSpeed").setValue(rightSpeed)
        database.getReference("data").child("motorLeftSpeed").setValue(leftSpeed)
    }

    var ballast1: Int = 0
    var ballast2: Int = 0
    var ballast3: Int = 0
    var ballast4: Int = 0

    fun updateBallastIndex(allIndex: Int?, index1: Int?, index2: Int?, index3: Int?, index4: Int?) {
        if (allIndex != null) {
            ballast.value!!.indexAll = allIndex
            ballast.value!!.index1 = ballast1 + ballast.value!!.indexAll!!
            ballast.value!!.index2 = ballast2 + ballast.value!!.indexAll!!
            ballast.value!!.index3 = ballast3 + ballast.value!!.indexAll!!
            ballast.value!!.index4 = ballast4 + ballast.value!!.indexAll!!
            ballast.value =
                Ballast(ballast.value!!.indexAll, ballast.value!!.index1, ballast.value!!.index2, ballast.value!!.index3, ballast.value!!.index4)
        }
        if (index1 != null) {
            ballast1 = index1 - ballast.value!!.indexAll!!
            ballast.value!!.index1 = ballast1 + ballast.value!!.indexAll!!
        }
        if (index2 != null) {
            ballast2 = index2 - ballast.value!!.indexAll!!
            ballast.value!!.index2 = ballast2 + ballast.value!!.indexAll!!
        }
        if (index3 != null) {
            ballast3 = index3 - ballast.value!!.indexAll!!
            ballast.value!!.index3 = ballast3 + ballast.value!!.indexAll!!
        }
        if (index4 != null) {
            ballast4 = index4 - ballast.value!!.indexAll!!
            ballast.value!!.index4 = ballast4 + ballast.value!!.indexAll!!
        }
        database.getReference("data").child("index1").setValue(clamp(ballast.value!!.index1, 0, 30))
        database.getReference("data").child("index2").setValue(clamp(ballast.value!!.index2, 0, 30))
        database.getReference("data").child("index3").setValue(clamp(ballast.value!!.index3, 0, 30))
        database.getReference("data").child("index4").setValue(clamp(ballast.value!!.index4, 0, 30))
        database.getReference("data").child("indexAll").setValue(ballast.value!!.indexAll)

    }

}