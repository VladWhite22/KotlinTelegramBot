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

        val userChoice = readln().toInt()

        when (true) {
            (userChoice == 1) -> println("Вы выбрали: Учить слова")
            (userChoice == 2) -> println("Вы выбрали: Статистика")
            (userChoice == 0) -> break
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