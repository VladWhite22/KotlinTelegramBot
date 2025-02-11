package org.example

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    val json = Json {
        ignoreUnknownKeys = true
    }

    while (true) {
        Thread.sleep(2000)
        val responseString = telegramService.getUpdates(botToken, lastUpdateId)
        println(responseString)

        val response: Response = json.decodeFromString(responseString)
        val updates = response.result
        val ferstUpdate = updates.firstOrNull() ?: continue
        val updateId = ferstUpdate.updateId
        lastUpdateId = updateId + 1

        val message = ferstUpdate.message?.text
        println(message)
        val chatId = ferstUpdate.message?.chat?.id ?: ferstUpdate.callbackQuery?.message?.chat?.id
        println(chatId)
        val data = ferstUpdate.callbackQuery?.data
        println(data)

        if (message?.lowercase() == "/start") telegramService.sendMenu(json, botToken, chatId)
        if (data == STATISTICS_CLICKED) telegramService.sendMessage(
            json,
            chatId,
            "Выучено ${statistics.learnedCount} из ${statistics.totalCount} слов | " +
                    "${statistics.percent}%" + "\n"
        )
        if (data == LEARN_WORDS_CLICKED) checkNextQuestionAndSend(json, trainer, telegramService, chatId)
        if (data == EXIT) telegramService.sendMenu(json, botToken, chatId)
        if (data?.startsWith(CALLBACK_DATA_ANSWER_PREFIX) == true) {
            val userAnswerIndex = data.substringAfterLast(delimiter = CALLBACK_DATA_ANSWER_PREFIX).toInt()
            if (userAnswerIndex != null) {
                if (trainer.checkAnswer(userAnswerIndex)) telegramService.sendMessage(json, chatId, "Правильно!")
                else telegramService.sendMessage(
                    json,
                    chatId,
                    "Неправильно! ${trainer.question?.correctAnswer?.original} – это ${trainer.question?.correctAnswer?.translete} "
                )
                checkNextQuestionAndSend(json, trainer, telegramService, chatId)
            }
        }
    }
}

fun checkNextQuestionAndSend(
    json: Json,
    trainer: LearnWordsTrainer,
    telegramService: TelegramBotService,
    chatId: Long?) {
    val question = trainer.getNextQuestion()
    if (question == null) telegramService.sendMessage(json, chatId, "Вы выучили все слова")
    else telegramService.sendQuestion(json, chatId, question)
}