package com.example.test.knowledgeking.commands

import me.jakejmattson.discordkt.commands.commands

fun knowledgeking() = commands("knowledgeking") {
    slash("knowledgeking", "A 'knowledgeking' command.") {
        execute {
            respond("Start knowledgeking!")
        }
    }
}