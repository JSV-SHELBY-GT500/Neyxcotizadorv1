package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "active_quote_draft")
data class ActiveQuoteDraft(
    @PrimaryKey val id: Int = 1,
    val jsonState: String,
    val lastUpdated: Long = System.currentTimeMillis()
)
