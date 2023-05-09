package tw.waterballsa.utopia.knowledgeking

import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.dailyScheduling
import tw.waterballsa.utopia.commons.extensions.scheduleDelay
import tw.waterballsa.utopia.jda.listener
import tw.waterballsa.utopia.knowledgeking.domain.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.time.Duration.Companion.seconds

private val log = KotlinLogging.logger {}
private val timer = Timer()

// Sets the announcement time for the upcoming Knowledge King game to 10:20 PM
private val announcementTime = Calendar.getInstance().apply {
    set(Calendar.HOUR_OF_DAY, 22)
    set(Calendar.MINUTE, 20)
    set(Calendar.SECOND, 0)
}!!

// Specifies the duration of time given to each contestant to prepare before the start of the game
// 10.minutes.inWholeMilliseconds
private val prepareDurationInMillis = 5.seconds.inWholeMilliseconds
private val timeBetweenAnnounceAndFirstQuestion = 60.seconds

private const val numberOfQuestions = 8
private const val timeBetweenQuestionsInSeconds = 15L
private const val timeBetweenAnswerRevealedAndNextQuestionInSeconds = 8L
private const val halftimeForBreakInSeconds = 20L
private const val awardRangeWithTopThree = 3

var knowledgeKing: KnowledgeKing? = null

fun knowledgeKing(wsa: WsaDiscordProperties, jda: JDA, chatGptQuestionParser: ChatGptQuestionParser) = listener {
    launchKnowledgeKingScheduling(wsa, jda, chatGptQuestionParser)

    on<ButtonInteractionEvent> {
        deferReply(true).queue()
        if (knowledgeKing != null && !knowledgeKing!!.isGameOver()) {
            val question = knowledgeKing!!.currentQuestion!!
            for (optionNumber in 0..question.options.size) {
                val buttonId = buttonId(question.number, optionNumber)
                if (buttonId == button.id) {
                    val contestantId = member?.id
                    val answer: Char = 'A' + optionNumber

                    log.info { "[Answered] {\"contestantId\": \"$contestantId\", \"answer\": \"$answer\"}" }

                    knowledgeKing!!.answer(contestantId, SingleChoiceAnswer(optionNumber))
                    hook.editOriginal("已經接受到你的答案。").queue()
                }
            }
        }
    }
}

private fun launchKnowledgeKingScheduling(wsa: WsaDiscordProperties, jda: JDA, chatGpt: ChatGptQuestionParser) {
    timer.dailyScheduling(announcementTime) {
        val knowledgeKingChannel = jda.getTextChannelById(wsa.knowledgeKingChannelId)
        val topic = generateTopic()
        announceTopic(topic, knowledgeKingChannel!!)

        // TODO: call ChatGPT 需要時間，後續可以用 async task 優化，暫時用 time 對齊剩餘秒數
        val startGenerateQuizTime = LocalDateTime.now()
        val quiz = generateQuizForTopic(topic, chatGpt)
        val endGenerateQuizTime = LocalDateTime.now()

        val durationTimeWithGenerateQuiz = Duration.between(startGenerateQuizTime, endGenerateQuizTime)

        scheduleFirstQuestion(
            quiz,
            wsa,
            jda,
            knowledgeKingChannel,
            maxOf(timeBetweenAnnounceAndFirstQuestion.inWholeSeconds - durationTimeWithGenerateQuiz.seconds, 0)
        )
    }
}

private fun scheduleFirstQuestion(
    quiz: Quiz,
    wsa: WsaDiscordProperties,
    jda: JDA,
    knowledgeKingChannel: TextChannel,
    delayInSeconds: Long
) {
    log.info { "[First question delay seconds] \"{\"delayInSeconds\": $delayInSeconds}\"" }

    timer.scheduleDelay(delayInSeconds.seconds.inWholeMilliseconds) {
        announceStartingGame(knowledgeKingChannel)

        timer.scheduleDelay(prepareDurationInMillis) {
            knowledgeKing = KnowledgeKing(quiz, timeBetweenQuestionsInSeconds)

            val events = knowledgeKing!!.startContest()

            handleEvents(events, wsa, jda)

            scheduleRevealAnswer(
                events.filterIsInstance<NextQuestionEvent>().first(),
                knowledgeKingChannel
            )
        }
    }
}

private fun scheduleRevealAnswer(nextQuestionEvent: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
    log.info { "[Revealing next question] \"{\"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds}\"" }

    // 延遲 15 秒揭曉（答題時間）
    timer.scheduleDelay(timeBetweenQuestionsInSeconds.seconds.inWholeMilliseconds) {
        revealTheAnswer(nextQuestionEvent, knowledgeKingChannel)

        val ranking = knowledgeKing!!.rank()

        when {
            // 已經問完
            nextQuestionEvent.isLastQuestion -> {
                knowledgeKing!!.endGame()
                revealFinalRanking(ranking, knowledgeKingChannel)
            }
            // 問題已經過半
            nextQuestionEvent.questionNumber == kotlin.math.ceil(knowledgeKing!!.size().toDouble() / 2).toInt() -> {
                val rankings = ranking.takeRangeRankings(1)
                val breakMessage = buildString {
                    append(":loudspeaker: 比賽已經走一半了～中場休息一下～")
                    rankings.firstNotNullOfOrNull { ranks ->
                        ranks.value.joinToString(", ") { "<@${it.contestantId}>" }
                    }?.let { append("\n　目前的領先者為 $it") }
                }
                knowledgeKingChannel.sendMessage(beautifulMsgBlock(breakMessage)).queue()

                timer.scheduleDelay(halftimeForBreakInSeconds) {
                    scheduleNextQuestion(knowledgeKingChannel)
                }
            }
            // 其他 -> 下一題
            else -> {
                scheduleNextQuestion(knowledgeKingChannel)
            }
        }
    }
}

private fun scheduleNextQuestion(knowledgeKingChannel: TextChannel) {
    log.info { "[Starting next question] {\"number\": ${knowledgeKing!!.currentQuestion!!.number + 1}, \"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds} }\n        " }

    // 延遲 8 秒出下一題（等待下一題時間）
    timer.scheduleDelay(timeBetweenAnswerRevealedAndNextQuestionInSeconds.seconds.inWholeMilliseconds) {
        val nextQuestionEvent = knowledgeKing!!.nextQuestion()!!
        handleNextQuestionEvent(nextQuestionEvent, knowledgeKingChannel)
        scheduleRevealAnswer(nextQuestionEvent, knowledgeKingChannel)
    }
}

private fun revealFinalRanking(ranking: Ranking, knowledgeKingChannel: TextChannel) {
    // show final message
    knowledgeKingChannel.sendMessage(
        """
        ┌－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┐
        ｜感謝大家參與本次的「全民軟體知識王」，問答的階段已經結束了｜
        ｜接下來要準備公佈這次答題正確率的排名，將從第三名開始公布！｜
        └－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┘
    """.trimIndent()
    ).queue()

    val rankGroups = ranking.takeRangeRankings(awardRangeWithTopThree)

    if (rankGroups.isEmpty()) {
        log.info { "[Reveal Final Ranking] {\"winner\": \"empty\"}" }
        knowledgeKingChannel.sendMessage(":banana: 本屆沒有智慧王 :monkey:")
            .queueAfter(2, TimeUnit.SECONDS)

    } else {
        log.info { "[Reveal Final Ranking] {\"winner\": \"${rankGroups.size}\"}" }

        knowledgeKingChannel.sendMessage("（奏樂）...:trumpet:..:accordion:.:notes:..:drum:..:drum:. :notes:")
            .queueAfter(0, TimeUnit.SECONDS)

        val listOfRankGroup = rankGroups.toList()
        var delayTimeOfSeconds = 3L

        // start from 3rd place
        (0..awardRangeWithTopThree).reversed().forEach { index ->
            val rankGroup = listOfRankGroup.getOrNull(index)

            when (index) {
                2 -> {
                    if (rankGroup != null) {
                        val candidates = rankGroup.second.joinToString(", ") { "<@${it.contestantId}>" }
                        knowledgeKingChannel.sendMessage(":third_place: 第三名是 ${candidates}，得分數為 ${rankGroup.first} 分")
                            .queueAfter(delayTimeOfSeconds, TimeUnit.SECONDS)
                    } else {
                        knowledgeKingChannel.sendMessage(":third_place: 第三名從缺 :joy:")
                            .queueAfter(delayTimeOfSeconds, TimeUnit.SECONDS)
                    }
                }

                1 -> {
                    if (rankGroup != null) {
                        val candidates = rankGroup.second.joinToString(", ") { "<@${it.contestantId}>" }
                        knowledgeKingChannel.sendMessage(":second_place: 第二名是 ${candidates}，得分數為 ${rankGroup.first} 分")
                            .queueAfter(delayTimeOfSeconds, TimeUnit.SECONDS)
                    } else {
                        knowledgeKingChannel.sendMessage(":second_place: 第二名從缺 :monkey: :monkey:")
                            .queueAfter(delayTimeOfSeconds, TimeUnit.SECONDS)
                    }
                }

                0 -> {
                    rankGroup?.second?.let {
                        val candidates = it.joinToString(", ") { "<@${it.contestantId}>" }
                        knowledgeKingChannel.sendMessage("即將公佈冠軍...")
                            .queueAfter(delayTimeOfSeconds, TimeUnit.SECONDS)
                        announceChampion(knowledgeKingChannel, candidates, rankGroup.first, delayTimeOfSeconds + 3)
                    }
                }
            }
            delayTimeOfSeconds += 3
        }
    }
}

private fun announceChampion(channel: TextChannel, candidates: String, score: Long, delaySeconds: Long) {
    channel.sendMessage("冠軍是...".trimIndent()).queueAfter(delaySeconds, TimeUnit.SECONDS)
    channel.sendMessage(":trophy: 本屆的知識王是 $candidates，得分數為 $score 分".trimIndent())
        .queueAfter(delaySeconds + 1, TimeUnit.SECONDS)
    channel.sendMessage(":tada: 恭喜脫穎而出，得到第一名的殊榮 :tada:").queueAfter(delaySeconds + 2, TimeUnit.SECONDS)
}

private fun revealTheAnswer(nextQuestionEvent: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
    // Check answer spec type
    val correctAnswer: String = when (val answer = nextQuestionEvent.question.answer) {
        is SingleAnswerSpec -> "${'A' + answer.optionNumber}"

        is MultipleAnswerSpec -> answer.optionNumbers.map {
            "${'A' + it}"
        }.joinToString(", ")

        else -> throw IllegalStateException("Doesn't support the answer type ${answer.javaClass.simpleName}.")
    }

    // Answer message template
    val answerMessage = ":bulb: 正確解答：${correctAnswer}"

    log.info { "[Reveal Answer] {\"answerMessage\": \"${answerMessage}\"}" }
    knowledgeKingChannel.sendMessage(beautifulMsgBlock(answerMessage)).queue()
}

private fun generateQuizForTopic(topic: String, chatGpt: ChatGptQuestionParser): Quiz {
    val questions = chatGpt.generateQuestions(topic, numberOfQuestions)
    return Quiz(topic, questions)
}

private fun announceTopic(topic: String, knowledgeKingChannel: TextChannel) {
    knowledgeKingChannel.sendMessage(
        """
        ┌－－－－－－－－－－－－－－－－－－－－－－－－－－┐
        ｜:loudspeaker: 水球軟體學院的「全民軟體知識王」比賽即將開始啦！  ｜
        ｜　　　　　　　　　　　　　　　　　　　　　　　　　　｜
        ｜不管你正好在閒聊，還是在工作，學習過程中不忘解放知識｜
        ｜歡迎一起加入知識問答的行列，放輕鬆去玩～　　　　　　｜
        └－－－－－－－－－－－－－－－－－－－－－－－－－－┘
        
        遊戲方式如下：
        :point_right: 總共有 $numberOfQuestions 題知識問答題，題目皆是選擇題
        :point_right: 每一題有 4 個選項，裡面只有 1 個答案是正確的！
        :point_right: 參與者透過點擊 A、B、C、D 其中一個按鈕，完成答題
        （你點選的答案不會被他人看見唷 :face_with_peeking_eye:）
        
        本次的比賽主題是「$topic」
        比賽即將在 **${timeBetweenAnnounceAndFirstQuestion.inWholeMinutes} 分鐘**後開始唷
        大家趕緊把時間空下來，千萬別錯過唷！
    """.trimIndent()
    ).queue()
}

private fun announceStartingGame(knowledgeKingChannel: TextChannel) {
    knowledgeKingChannel.sendMessage(beautifulMsgBlock(":triangular_flag_on_post: 全民軟體知識王現在開始囉").trimIndent())
        .queue()
}

private fun generateTopic(): String {
    return "Computer Science" // only support for CS in the current version
}

private fun handleEvents(events: List<Event>, wsa: WsaDiscordProperties, jda: JDA) {
    val knowledgeKingChannel = jda.getTextChannelById(wsa.knowledgeKingChannelId)!!
    events.forEach { event ->
        when (event) {
            is NextQuestionEvent -> handleNextQuestionEvent(event, knowledgeKingChannel)
        }
    }
}


private fun handleNextQuestionEvent(event: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
    log.info { "[Next Question] {\"number\": ${event.questionNumber}, \"question\":\"${event.question.description}\"" }

    val question = event.question

    knowledgeKingChannel.sendMessageEmbeds(Embed {
        this.title = "第 ${question.number} 題 - ${question.description}"
        this.description = question.options.mapIndexed { i, option -> "${'A' + i}) $option" }.joinToString("\n")
    }).addActionRow(
        Button.primary(buttonId(question.number, 0), "A"),
        Button.primary(buttonId(question.number, 1), "B"),
        Button.primary(buttonId(question.number, 2), "C"),
        Button.primary(buttonId(question.number, 3), "D")
    ).queue {
        log.info { "[Question Posted] \"question type\":\"${question.type}\",\"question number\" : ${question.number}" }
    }
    knowledgeKingChannel.sendMessage(beautifulMsgBlock(":timer: 作答時間開始")).queue()
}

private fun buttonId(questionNumber: Int, optionNumber: Int): String {
    return "${knowledgeKing!!.id}-$questionNumber-$optionNumber"
}

// todo: more beautiful, support multiple line and half char width
private fun beautifulMsgBlock(message: String): String {
    val partOfMessages = message.split("\n")
    val maxLength = partOfMessages.maxByOrNull { it.length }?.let {
        Regex(":(\\w+):").replace(it) { "　" }.length
    } ?: 20
    return buildString {
        appendLine("┌${"－".repeat(maxLength)}┐")
        appendLine("　$message")
        appendLine("└${"－".repeat(maxLength)}┘")
    }
}
