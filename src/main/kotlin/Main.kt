package org.example

import java.io.File

fun main() {
   val wordsFile:File = File("words.txt")
    wordsFile.createNewFile()

    for (words in wordsFile.readLines())
        println(words)
}