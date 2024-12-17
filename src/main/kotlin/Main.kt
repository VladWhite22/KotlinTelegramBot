package org.example

import java.io.File

fun main() {
    val dictionary = loadDictionary()
    while (true) {
        println(
            "Меню: \n" +
                    "1 – Учить слова\n" +
                    "2 – Статистика\n" +
                    "0 – Выход"
        )
KTB-06-Implement-statistics-functionality

        val userChoice = readln().toInt()

        when (true) {
            (userChoice == 1) -> println("Вы выбрали: Учить слова")
            (userChoice == 2) -> {
                println("Вы выбрали: Статистика")
                val learnedCount = dictionary.filter { it.correctAnswersCount >= 3 }.size
                val totalCount = dictionary.size
                val percent = (learnedCount.toFloat() / totalCount.toFloat() * 100).toInt()
                println("Выучено $learnedCount из $totalCount слов | $percent%" + "\n")
            }

            (userChoice == 0) -> break


        when (val userChoice = readln().toInt()) {

            1 -> println("Вы выбрали: Учить слова")
            2 -> println("Вы выбрали: Статистика")
            0 -> break
 master
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

fun loadDictionary(): List<Word> {
    val dictionary = mutableListOf<Word>()
    val wordsFile: File = File("words.txt")
    wordsFile.forEachLine {
        val line = it.split("|")
        val word =
            Word(original = line[0], translete = line[1], correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0)
        dictionary.add(word)
    }

    return dictionary
}