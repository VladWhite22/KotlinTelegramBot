package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")
    val dictionary = mutableListOf<Word>()

    wordsFile.forEachLine {
        val line = it.split("|")
        val word = Word(original = line[0], translete = line[1], correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0)
        dictionary.add(word)
    }

    dictionary.forEach { println("${it.original} ${it.translete} ${it.correctAnswersCount}") }
}