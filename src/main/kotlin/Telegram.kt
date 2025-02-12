package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Update(
    @SerialName("update_id")
    val updateId: Long,
    @SerialName("message")
    val message: Message? = null,
    @SerialName("callback_query")
    val callbackQuery: CallbackQuery? = null,
)

@Serializable
data class Response(
    @SerialName("result")
    val result: List<Update>,
)

@Serializable
data class Message(
    @SerialName("text")
    val text: String,
    @SerialName("chat")
    val chat: Chat,
)

@Serializable
data class CallbackQuery(
    @SerialName("data")
    val data: String? = null,
    @SerialName("message")
    val message: Message? = null,
)

@Serializable
data class Chat(
    @SerialName("id")
    val id: Long,
)

@Serializable
data class SendMessageRequest(
    @SerialName("chat_id")
    val chatId: Long?,
    @SerialName("text")
    val text: String,
    @SerialName("reply_markup")
    val replyMarkup: ReplyMarkup? = null,
)

@Serializable
data class ReplyMarkup(
    @SerialName("inline_keyboard")
    val inlineKeyboard: List<List<InlineKeyboard>>,
)

@Serializable
data class InlineKeyboard(
    @SerialName("text")
    val text: String,
    @SerialName("callback_data")
    val callbackData: String,
)

fun main(args: Array<String>) {

    val botToken = args[0]
    var lastUpdateId = 0L
    val telegramService = TelegramBotService(botToken)
    val trainer = LearnWordsTrainer()
    val statistics = trainer.getStatistics()

    while (true) {
        Thread.sleep(2000)
        val response: Response = telegramService.getUpdates(botToken, lastUpdateId)
        val updates = response.result
        val firstUpdate = updates.firstOrNull() ?: continue
        val updateId = firstUpdate.updateId
        lastUpdateId = updateId + 1

        val message = firstUpdate.message?.text
        println(message)
        val chatId = firstUpdate.message?.chat?.id ?: firstUpdate.callbackQuery?.message?.chat?.id ?: continue
        println(chatId)
        val data = firstUpdate.callbackQuery?.data
        println(data)

        if (message?.lowercase() == "/start") telegramService.sendMenu(botToken, chatId)
        when (data) {
            STATISTIC_CLICKED -> telegramService.sendMessage(
                chatId,
                "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | " +
                        "${statistics.percent}%" + "\n"
            )
            WORDS_CLICKED -> checkNextQuestionAndSend(trainer, telegramService, chatId)
            EXIT_MENU -> telegramService.sendMenu(botToken, chatId)
        }
        if (data?.startsWith(DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfterLast(delimiter = DATA_ANSWER_PREFIX).toInt()
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

fun checkNextQuestionAndSend(trainer: LearnWordsTrainer, telegramService: TelegramBotService, chatId: Long) {
    val question = trainer.getNextQuestion()
    if (question == null) telegramService.sendMessage( chatId, "Вы выучили все слова")
    else telegramService.sendQuestion( chatId, question)
}