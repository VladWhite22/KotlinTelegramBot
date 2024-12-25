package org.example

data class Word(
    val original: String,
    val translete: String,
    var correctAnswersCount: Int = 0,
)
