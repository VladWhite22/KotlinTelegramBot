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

        when (val userChoice = readln().toInt()) {
            1 -> {
                println("Вы выбрали: Учить слова")
                val notLearnedList = dictionary.filter { it.correctAnswersCount < PASSING_CORRECT_ANSWERS }
                if (notLearnedList.size == 0) {
                    println("\nВсе слова в словаре выучены\n")
                    continue
                }

                val questionWords = notLearnedList.shuffled().take(NUMBER_OF_OPTIONS)
                val correctAnswer = questionWords.random()
                val correctAnswerId = questionWords.indexOf(correctAnswer)
                println(
                    questionWords.mapIndexed{index, p-> "${index+1}-${p.translete}"}.
                    joinToString(separator = "\n", prefix = "${correctAnswer.original}:\n", postfix = "\n----------\n0 - Меню")
                )
                val userAnswerInput = readln().toInt()
                if (userAnswerInput == 0) continue
                else if (userAnswerInput == correctAnswerId){
                    correctAnswer.correctAnswersCount += 1
                    saveDictionary(dictionary)
                }

            }

            2 -> {
                println("Вы выбрали: Статистика")
                val learnedCount = dictionary.filter { it.correctAnswersCount >= PASSING_CORRECT_ANSWERS }.size
                val totalCount = dictionary.size
                val percent = (learnedCount.toFloat() / totalCount.toFloat() * 100).toInt()
                println("Выучено $learnedCount из $totalCount слов | $percent%" + "\n")
            }

            0 -> break
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

fun saveDictionary( dictionary: List<Word>){

File("words.txt").writeText("")
    File("words.txt").writeText(dictionary.toString())

}