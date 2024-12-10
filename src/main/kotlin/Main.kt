package org.example

import java.io.File

fun main() {
    val wordsFile: File = File("words.txt")

    wordsFile.forEachLine {
        val dictionary = mutableListOf<Word>()

        val line = it.split(" ","|")
        val word = Word(original = line[0], translete = line[1], correctAnswersCount = line[2].toInt()?:0)
        dictionary.add(word)
        for (element in dictionary) println("${element.original} ${element.translete} ${element.correctAnswersCount}")
    }
}