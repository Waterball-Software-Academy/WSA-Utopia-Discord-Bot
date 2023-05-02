package tw.waterballsa.utopia.knowledgeking

import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.scheduleDelay
import tw.waterballsa.utopia.jda.listener
import tw.waterballsa.utopia.knowledgeking.domain.*
import java.util.*
import kotlin.time.Duration.Companion.milliseconds
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

private const val numberOfQuestions = 3
private const val timeBetweenQuestionsInSeconds = 15L
private const val timeBetweenAnswerRevealedAndNextQuestionInSeconds = 8L

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
//    timer.dailyScheduling(announcementTime) {
    val knowledgeKingChannel = jda.getTextChannelById(wsa.knowledgeKingChannelId)
    val topic = generateTopic()
    announceTopic(topic, knowledgeKingChannel!!)

    // todo: 後續可以用 async task 優化，這裡暫時用時間線來對齊公告通知
    val quiz = generateQuizForTopic(topic, chatGpt)

    scheduleFirstQuestion(quiz, wsa, jda, knowledgeKingChannel)
//    }
}

private fun scheduleFirstQuestion(quiz: Quiz, wsa: WsaDiscordProperties, jda: JDA, knowledgeKingChannel: TextChannel) {
    timer.scheduleDelay(prepareDurationInMillis) {
        knowledgeKing = KnowledgeKing(quiz, timeBetweenQuestionsInSeconds)
        val events = knowledgeKing!!.startContest()
        handleEvents(events, wsa, jda)
        scheduleRevealAnswer(events.filterIsInstance<NextQuestionEvent>().first(),
                knowledgeKingChannel)
    }
}

private fun scheduleRevealAnswer(nextQuestionEvent: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
    log.info { "[Revealing next question] \"{\"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds}\"" }
    timer.scheduleDelay(timeBetweenQuestionsInSeconds.seconds.inWholeMilliseconds) {
        val ranking = knowledgeKing!!.rank()
        revealTheAnswer(nextQuestionEvent, knowledgeKingChannel)
        if (nextQuestionEvent.isLastQuestion) {
            knowledgeKing!!.endGame()
            revealFinalRanking(ranking, knowledgeKingChannel)
        } else {
            knowledgeKingChannel.sendMessage(":trophy: 目前排名：\n${rankingToMessage(ranking)}").queue()
            scheduleNextQuestion(knowledgeKingChannel)
        }
    }
}

private fun scheduleNextQuestion(knowledgeKingChannel: TextChannel) {
    log.info { "[Starting next question] {\"number\": ${knowledgeKing!!.currentQuestion!!.number + 1}, \"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds} }\n        " }
    timer.scheduleDelay(timeBetweenAnswerRevealedAndNextQuestionInSeconds.seconds.inWholeMilliseconds) {
        val nextQuestionEvent = knowledgeKing!!.nextQuestion()!!
        handleNextQuestionEvent(nextQuestionEvent, knowledgeKingChannel)
        scheduleRevealAnswer(nextQuestionEvent, knowledgeKingChannel)
    }
}

private fun revealFinalRanking(ranking: Ranking, knowledgeKingChannel: TextChannel) {
    knowledgeKingChannel.sendMessage("本次【全民軟體知識王】活動結束\n感謝大家的參與\n:trophy: 本次的排名如下：\n${rankingToMessage(ranking)}").queue()
}

private fun rankingToMessage(ranking: Ranking): String {
    val rankings = ranking.ranks
            .map { " 第 ${it.rankNumber} 名： <@${it.contestantId}> - ${it.score} 分" }
            .take(5)
    return rankings.joinToString("\n")
}

private fun revealTheAnswer(nextQuestionEvent: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
    // Check answer spec type
    val answerOption: String = when (val answer = nextQuestionEvent.question.answer) {
        is SingleAnswerSpec -> "${'A' + answer.optionNumber}"

        is MultipleAnswerSpec -> answer.optionNumbers.map {
            'A' + it
        }.joinToString(", ")

        else -> throw IllegalStateException("Doesn't support the answer type ${answer.javaClass.simpleName}.")
    }

    // Answer message template
    val answerMessage = """
        :bulb: 正確解答: $answerOption 
    """.trimIndent()

    // Send message to text channel
    log.info { "[Reveal Answer] {\"answerMessage\": \"${answerMessage}\"}" }
    knowledgeKingChannel.sendMessage(answerMessage).queue()
}


private fun generateQuizForTopic(topic: String, chatGpt: ChatGptQuestionParser): Quiz {
    val questions = chatGpt.generateQuestions(topic, numberOfQuestions)
    return Quiz(topic, questions)
}

private fun announceTopic(topic: String, knowledgeKingChannel: TextChannel) {
    knowledgeKingChannel.sendMessage("""
                :loudspeaker: 水球軟體學院的「全民軟體知識王」比賽即將開始啦！
                不管你正好在閒聊，滿腹知識的你、渴望證明自己的你、十年寒窗無人問的你
                都歡迎一起加入知識問答，放輕鬆去玩～
                
                :point_right: 遊戲方式如下：
                題目皆是選擇題，分別有四個答案，裡面只有一個答案是正確的唷！
                參與者透過點擊 A、B、C、D 其中一個按鈕，完成答題
                （你點選的答案不會被他人看見唷 :face_with_peeking_eye:）
                
                本次的比賽主題是「$topic」
                比賽即將在 **${prepareDurationInMillis.milliseconds.inWholeSeconds} 分鐘**後開始唷
                大家趕緊把時間空下來，千萬別錯過唷！
            """.trimIndent()).queue()
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
        this.title = "第 ${question.number} 題"
        this.description = "${question.description}\n${
            question.options.mapIndexed { i, option ->
                "${'A' + i}) $option"
            }.joinToString("\n")
        }"
    }).addActionRow(
            Button.primary(buttonId(question.number, 0), "A"),
            Button.primary(buttonId(question.number, 1), "B"),
            Button.primary(buttonId(question.number, 2), "C"),
            Button.primary(buttonId(question.number, 3), "D")
    ).queue {
        log.info { "[Question Posted] \"question type\":\"${question.type}\",\"question number\" : ${question.number}" }
    }
}

private fun buttonId(questionNumber: Int, optionNumber: Int): String {
    return "${knowledgeKing!!.id}-$questionNumber-$optionNumber"
}














