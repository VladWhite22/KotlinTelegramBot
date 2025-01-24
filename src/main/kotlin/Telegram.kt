package org.example

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

fun main(args: Array<String>) {
    val botToken = args[0]
    var updateId = 0


    while (true) {
        Thread.sleep(2000)
        val updates = getUpdates(botToken, updateId.toString())
        println(updates)

        val updateIdRegex = "\"update_id\":(.+?),".toRegex()
        val matchResultId: MatchResult? = updateIdRegex.find(updates)
        val groupsId = matchResultId?.groups
        val id = groupsId?.get(1)?.value?.toInt()

        if (id != null) {
            updateId = id
        }
        println(updateId)


        val messageTextRegex: Regex = "\"text\":\"(.+?)\"".toRegex()
        val matchResult: MatchResult? = messageTextRegex.find(updates)
        val groups = matchResult?.groups
        val text = groups?.get(1)?.value
        println(text)

    }
}

fun getUpdates(botToken: String, updateId: String?): String {
    val urlGetUpdates = "https://api.telegram.org/bot$botToken/getUpdates?offset=$updateId"

    val client: HttpClient = HttpClient.newBuilder().build()
    val request: HttpRequest = HttpRequest.newBuilder().uri(URI.create(urlGetUpdates)).build()
    val response: HttpResponse<String> = client.send(request, HttpResponse.BodyHandlers.ofString())

    return response.body()
}