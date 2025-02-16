package org.example

import kotlinx.serialization.Serializable
import java.io.File
@Serializable
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

class LearnWordsTrainer(
    private val fileName:String = "words.txt",
) {
    var question: Question? = null
   private val dictionary = loadDictionary()

    fun getStatistics(): Statistics {
        val learnedCount = dictionary.filter { it.correctAnswersCount >= PASSING_CORRECT_ANSWER }.size
        val totalCount = dictionary.size
        val percent = (learnedCount.toFloat() / totalCount.toFloat() * 100).toInt()
        return Statistics(learnedCount, totalCount, percent)
    }

    fun getNextQuestion(): Question? {
        val notLearnedList = dictionary.filter { it.correctAnswersCount < PASSING_CORRECT_ANSWER }

        if (notLearnedList.isEmpty()) return null

        val questionWords = if (notLearnedList.size < NUMBER_OF_OPTION) {
            val lernedList = dictionary.filter { it.correctAnswersCount >= PASSING_CORRECT_ANSWER }.shuffled()
            notLearnedList.shuffled().take(NUMBER_OF_OPTION) +
                    lernedList.take(NUMBER_OF_OPTION - notLearnedList.size)
        } else {
            notLearnedList.shuffled().take(NUMBER_OF_OPTION)
        }.shuffled()

        val correctAnswer = questionWords.random()

        question = Question(
            variants = questionWords,
            correctAnswer = correctAnswer
        )
        return question
    }

    fun checkAnswer(userAnswerIndex: Int): Boolean {
        return question?.let {
            val correctAnswerId = it.variants.indexOf(it.correctAnswer)

            if (correctAnswerId == userAnswerIndex) {
                it.correctAnswer.correctAnswersCount++
                saveDictionary()
                true
            } else false
        } ?: false
    }

    private fun loadDictionary(): List<Word> {
        val wordsFile: File = File(fileName)
        if (!wordsFile.exists()){
            File("words.txt").copyTo(wordsFile)
        }
        val dictionary = mutableListOf<Word>()
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

    private fun saveDictionary() {
        val wordsFile = File(fileName)
        wordsFile.writeText("")
        for (word in dictionary)
            wordsFile.appendText("${word.original}|${word.translete}|${word.correctAnswersCount}\n")
    }

     fun resetProgress(){
         dictionary.forEach { it.correctAnswersCount = 0 }
         saveDictionary()
     }
}

