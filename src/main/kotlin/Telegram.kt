package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()
    val statistics = trainer.getStatistics()

    val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    val messageTextRegex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()
    
    while (true) {
        Thread.sleep(2000)
        val updates = telegramService.getUpdates(botToken, updateId.toString())
        println(updates)

        val matchResultId: MatchResult? = updateIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value?.toInt()
        updateId = id ?: continue
        updateId++
        println(updateId)

        val text = messageTextRegex.find(updates)?.groups?.get(1)?.value ?: continue
        println(text)
        val chatId = chatIdRegex.find(updates)?.groups?.get(1)?.value ?: continue
        println(chatId)
        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println(data)

        if (text.lowercase() == "/start") telegramService.sendMenu(chatId)
        if (data == STATISTICS_CLICKED) telegramService.sendMessage(
            chatId,
            "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | " +
                    "${statistics.percent}%" + "\n"
        )
        if (data == LEARN_WORDS_CLICKED) checkNextQuestionAndSend(trainer, telegramService, chatId)
        if (data == EXIT) telegramService.sendMenu(chatId)
        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfterLast(delimiter = CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (userAnswerIndex != null) {
                if (trainer.checkAnswer(userAnswerIndex)) telegramService.sendMessage(chatId, "Правильно!")
                else telegramService.sendMessage(
                    chatId,
                    "Неправильно! ${trainer.question?.correctAnswer?.original} – это ${trainer.question?.correctAnswer?.translete} "
                )
                checkNextQuestionAndSend(trainer, telegramService, chatId)
            }
        }
    }
}

fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, telegramService: TelegramBotService, chatId: String) {
    val question = trainer.getNextQuestion()
    if (question == null) telegramService.sendMessage(chatId, "Вы выучили все слова")
    else telegramService.sendQuestion(chatId, question)
}