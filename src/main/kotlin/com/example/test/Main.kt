package com.example.test

import dev.kord.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.dsl.bot
import me.jakejmattson.discordkt.dsl.listeners
import java.util.*

// 1. declare commands
// 2. listen to events
// 3. control bot
fun main(args: Array<String>) {
    bot(getBotToken()) {
        prefix { "+" } // ?
    }
}

fun getBotToken(): String {
    val properties = Properties()
    val file = Thread.currentThread().contextClassLoader.getResourceAsStream("secret.properties")
    properties.load(file)
    return properties.getProperty("token")
}

fun logListeners() = listeners {
    on<MessageCreateEvent> {
        println(message.content)
    }
}