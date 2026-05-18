package com.alexander.solarstream.data

import com.alexander.solarstream.model.Telemetry
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class SolarRepository private constructor() {

    // Pointing to the node our Node.js simulator is writing to
    private val database = FirebaseDatabase.getInstance().getReference("telemetry/current")
    companion object {
        @Volatile
        private var instance: SolarRepository? = null

        fun getInstance(): SolarRepository {
            return instance ?: synchronized(this) {
                instance ?: SolarRepository().also { instance = it }
            }
        }
    }

    fun getSolarData(): Flow<Telemetry?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val telemetry = snapshot.getValue(Telemetry::class.java)
                trySend(telemetry)
            }
            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }
        database.addValueEventListener(listener)
        awaitClose { database.removeEventListener(listener) }
    }
}