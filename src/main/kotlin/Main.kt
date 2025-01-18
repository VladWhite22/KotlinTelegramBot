package org.example

fun Question.asConsoleString(): String {
    val variants = this.variants.mapIndexed { index, p -> "${index + 1}-${p.translete}" }.joinToString(
        separator = "\n",
        prefix = "${this.correctAnswer.original}:\n",
        postfix = "\n----------\n0 - Меню"
    )
    return variants
}

fun main() {
    val trainer = LearnWordsTrainer()
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
                val unlernedDictionary =
                    trainer.dictionary.filter { it.correctAnswersCount < PASSING_CORRECT_ANSWERS }.size

                while (unlernedDictionary != 0) {
                    if (unlernedDictionary == 0) {
                        println("\nВсе слова в словаре выучены\n")
                        continue
                    }

                    val question = trainer.getNextQuestin()
                    val correctAnswerId = question?.variants?.indexOf(question.correctAnswer)

                    if (question != null) {
                        println(question.asConsoleString())
                    }
                    val userAnswerInput = readln().toInt()
                    if (userAnswerInput == 0) break
                    else if (correctAnswerId != null) {
                        if (trainer.checkAnsver(userAnswerInput.minus(1))) {
                            println("Правильно!")

                        } else println("Неправильно!  ${question.correctAnswer.original}- это ${question.correctAnswer.translete}")
                    }
                }
            }

            2 -> {
                println("Вы выбрали: Статистика")
                val statistics = trainer.getStatistics()
                println("Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | ${statistics.percent}%" + "\n")
            }

            0 -> break
            else -> println("Введите число 1, 2 или 0")
        }
    }
}

