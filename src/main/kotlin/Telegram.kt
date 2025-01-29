package org.example

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0
    val telegramService = TelegramBotService(botToken)

    val updateIdRegex = "\"update_id\":(.+?),".toRegex()
    val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
    val chatIdRegex = "\"id\":(.+?),".toRegex()

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

        if (text =="Hello") telegramService.sendMessage(chatId, "Hello")
    }
}

