package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)

    val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"chat\":\\{\"id\":(\\d+)".toRegex()
    val dataRegex: Regex = "\"data\":\"(.+?)\"".toRegex()

    val trainer = LearnWordsTrainer()

    fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, telegramService: TelegramBotService, chatId: String) {
        val question = trainer.getNextQuestion()
        if (question == null) telegramService.sendMessage(chatId, "Вы выучили все слова")
        else telegramService.sendQuestion(chatId, question)
    }
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

        val matchResultText: MatchResult? = messageTextRegex.find(updates)
        val groupsText = matchResultText?.groups
        val text = groupsText?.get(1)?.value ?: continue
        println(text)

        val matchChatId = chatIdRegex.find(updates)
        val groupsChatId = matchChatId?.groups
        val chatId = groupsChatId?.get(1)?.value ?: continue
        println(chatId)

        val data = dataRegex.find(updates)?.groups?.get(1)?.value
        println(data)

        if (text.lowercase() == "/start") telegramService.sendMenu(chatId)
        if (data == STATISTICS_CLICKED) telegramService.sendMessage(
            chatId,
            "Выучено ${trainer.getStatistics().learnedCount} из ${trainer.getStatistics().totalCount} слов | " +
                    "${trainer.getStatistics().percent}%" + "\n"
        )
        if (data == LEARN_WORDS_CLICKED) checkNextQuestionAndSend(trainer,telegramService,chatId)
    }
}
