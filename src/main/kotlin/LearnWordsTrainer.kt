package org.example

import java.io.File

data class Word(
    val original: String,
    val translete: String,
    var correctAnswersCount: Int = 0,
)

data class Statistics(
    val learnedCount: Int,
    val totalCount: Int,
    val percent: Int,
)

data class Question(
    val variants: List<Word>,
    val correctAnswer: Word,
)

class LearnWordsTrainer {
    private var question: Question? = null
    val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= PASSING_CORRECT_ANSWERS }.size
        val totalCount = dictionary.size
        val percent = (learnedCount.toFloat() / totalCount.toFloat() * 100).toInt()
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < PASSING_CORRECT_ANSWERS }

        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < NUMBER_OF_OPTIONS) {
            val lernedList = dictionary.filter { it.correctAnswersCount >= PASSING_CORRECT_ANSWERS }.shuffled()
            notLearnedList.shuffled().take(NUMBER_OF_OPTIONS) +
                    lernedList.take(NUMBER_OF_OPTIONS - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(NUMBER_OF_OPTIONS)
        }.shuffled()

        val correctAnswer = questionWords.random()

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer
        )
        return question
    }

    fun checkAnsver(userAnswerIndex: Int?): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)

            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary(dictionary)
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val dictionary = mutableListOf<Word>()
        val wordsFile: File = File("words.txt")
        wordsFile.forEachLine {
            val line = it.split("|")
            val word =
                Word(
                    original = line[0],
                    translete = line[1],
                    correctAnswersCount = line.getOrNull(2)?.toIntOrNull() ?: 0
                )
            dictionary.add(word)
        }
        return dictionary
    }

    private fun saveDictionary(dictionary: List<Word>) {
        val wordsFile = File("words.txt")
        wordsFile.writeText("")
        for (word in dictionary)
            wordsFile.appendText("${word.original}|${word.translete}|${word.correctAnswersCount}\n")
    }
}

