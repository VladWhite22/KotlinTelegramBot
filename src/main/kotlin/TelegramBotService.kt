package org.example

import java.net.URI
import java.net.URLEncoder
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.nio.charset.StandardCharsets

class TelegramBotService(private val botToken: String) {
    private val client: HttpClient = HttpClient.newBuilder().build()

    fun getUpdates(botToken: String, updateId: String?): String {
        val urlGetUpdates = "$API_TELEGRAM$botToken/getUpdates?offset=$updateId"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

        return response.body()
    }

    fun sendMessage(chat_id: String, message: String) {
        val encoded = URLEncoder.encode(
            message,
            StandardCharsets.UTF_8
        )
        println(encoded)

        if (message.length > 4096 || message.length < 1) return println("Недопустимое значение text")

        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage?chat_id=$chat_id&text=$encoded"

        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage)).build()
        client.send(request, HttpResponse.BodyHandlers.ofString())
    }

    fun sendMenu(chat_id: String): String {

        val urlSendMessage = "$API_TELEGRAM$botToken/sendMessage"
        val sendMenuBody = """{
    "chat_id": $chat_id,
    "text": "Основное меню",
    "reply_markup": {
        "inline_keyboard": [
            [
                {
                    "text": "Изучить слова",
                    "callback_data": $LEARN_WORDS_CLICKED
                },
                {
                    "text": "Статистика",
                    "callback_data": $STATISTICS_CLICKED
                }
            ]
        ]
    }
}""".trimIndent()
        val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlSendMessage))
            .header("Content-type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(sendMenuBody))
            .build()

        val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}
