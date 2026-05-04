package com.example.taskflow.data

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String,
    val category: String,
    val createdAt: Long = System.currentTimeMillis()
)
