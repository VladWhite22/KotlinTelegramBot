package org.example

data class Word(
    val original: String,
    val translete: String,
    val correctAnswersCount: Int = 0,
)
