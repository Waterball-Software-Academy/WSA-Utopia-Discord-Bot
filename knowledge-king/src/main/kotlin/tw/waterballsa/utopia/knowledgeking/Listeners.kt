package tw.waterballsa.utopia.knowledgeking

import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.extensions.dailyScheduling
import tw.waterballsa.utopia.commons.extensions.scheduleDelay
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.knowledgeking.domain.*
import java.time.Duration
import java.time.LocalDateTime
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.ceil
import kotlin.time.Duration.Companion.seconds

@Component
class KnowledgeKingListener(
    private val wsa: WsaDiscordProperties,
    private val jda: JDA,
    private val chatGptQuestionParser: ChatGptQuestionParser
) : UtopiaListener() {

    private val log = KotlinLogging.logger {}
    private val timer = Timer()

    // Specifies the duration of time given to each contestant to prepare before the start of the game
    // 10.minutes.inWholeMilliseconds
    private val announcementTime = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 22)
        set(Calendar.MINUTE, 20)
        set(Calendar.SECOND, 0)
    }!!

    private val prepareDurationInMillis = 5.seconds.inWholeMilliseconds
    private val timeBetweenAnnounceAndFirstQuestion = 60.seconds

    private val numberOfQuestions = 8
    private val timeBetweenQuestionsInSeconds = 15L
    private val timeBetweenAnswerRevealedAndNextQuestionInSeconds = 8L
    private val halftimeForBreakInSeconds = 20L
    private val awardRangeWithTopThree = 3

    private var knowledgeKing: KnowledgeKing? = null

    init {
        launchKnowledgeKingScheduling(wsa, jda, chatGptQuestionParser)
    }

    /**
     * 按鈕互動事件
     * */
    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            deferReply(true).queue()
            when {
                // 遊戲中
                knowledgeKing != null && !knowledgeKing!!.isGameOver() -> {
                    val question = knowledgeKing!!.currentQuestion!!
                    val contestantId = member?.id

                    when {
                        // 檢查答題時間
                        knowledgeKing!!.getElapsedTimeInSeconds(question.sentTimeInSeconds!!) > 15 -> {
                            log.info { "[Answer Expired] {\"contestantId\": \"$contestantId\"}" }
                            hook.editOriginal("超過答題時間囉！！").queue()
                        }
                        // 正常答題時間
                        else -> {
                            for (optionNumber in 0..question.options.size) {
                                val buttonId = makeButtonId(question.number, optionNumber)
                                if (buttonId == button.id) {
                                    val answer: Char = 'A' + optionNumber

                                    log.info { "[Answered] {\"contestantId\": \"$contestantId\", \"answer\": \"$answer\"}" }
                                    knowledgeKing!!.answer(contestantId, SingleChoiceAnswer(optionNumber))
                                    hook.editOriginal("已經接受到你的答案。").queue()
                                }
                            }
                        }
                    }
                }
                // 遊戲已結束
                else -> hook.editOriginal("遊戲已經結束囉，請敬請期待下一次「全民軟體知識王」！").queue()
            }
        }
    }

    /**
     * 讀取智慧王排程
     */
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

    /**
     * 排入第一個問題
     */
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


    /**
     * 排入揭曉答案
     */
    private fun scheduleRevealAnswer(nextQuestionEvent: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
        log.info { "[Revealing next question] \"{\"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds}\"" }

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
                knowledgeKing!!.isGameHalfway() -> {
                    announceGameHalfWay(knowledgeKingChannel)
                    timer.scheduleDelay(halftimeForBreakInSeconds) {
                        scheduleNextQuestion(knowledgeKingChannel)
                    }
                }
                // 其他 -> 下一題
                else -> scheduleNextQuestion(knowledgeKingChannel)
            }
        }
    }

    /**
     * 排入下一個問題
     */
    private fun scheduleNextQuestion(knowledgeKingChannel: TextChannel) {
        log.info { "[Starting next question] {\"number\": ${knowledgeKing!!.currentQuestion!!.number + 1}, \"delayInSeconds\": $timeBetweenAnswerRevealedAndNextQuestionInSeconds} }\n        " }

        timer.scheduleDelay(timeBetweenAnswerRevealedAndNextQuestionInSeconds.seconds.inWholeMilliseconds) {
            val nextQuestionEvent = knowledgeKing!!.nextQuestion()!!
            handleNextQuestionEvent(nextQuestionEvent, knowledgeKingChannel)
            scheduleRevealAnswer(nextQuestionEvent, knowledgeKingChannel)
        }
    }

    /**
     * 揭曉最後排名
     */
    private fun revealFinalRanking(ranking: Ranking, knowledgeKingChannel: TextChannel) {
        announceEndingGame(knowledgeKingChannel)

        val rankGroups = ranking.getRankingGroups()
        var delayTimeInSeconds = 3L

        when (rankGroups.isEmpty()) {
            true -> {
                log.info { "[Reveal Final Ranking] {\"winner\": \"empty\"}" }
                knowledgeKingChannel.sendMessage(":banana: 本屆沒有智慧王 :monkey:").queueAfter(2, TimeUnit.SECONDS)
            }

            else -> {
                log.info { "[Reveal Final Ranking] {\"winner\": \"${rankGroups.size}\"}" }
                knowledgeKingChannel.sendMessage("（奏樂）...:trumpet:..:accordion:.:notes:..:drum:..:drum:. :notes:")
                    .queue()

                // start from 3rd place
                (0..awardRangeWithTopThree).reversed().forEach { index ->
                    when (rankGroups.getOrNull(index)?.rankingNum) {
                        1 -> announceChampion(knowledgeKingChannel, rankGroups.getOrNull(index), delayTimeInSeconds)
                        2 -> announceSecondPlace(knowledgeKingChannel, rankGroups.getOrNull(index), delayTimeInSeconds)
                        3 -> announceThirdPlace(knowledgeKingChannel, rankGroups.getOrNull(index), delayTimeInSeconds)
                    }
                    delayTimeInSeconds += 3
                }
            }
        }

        announcePromotingUtopiaDiscordBot(knowledgeKingChannel, delayTimeInSeconds + 10)
    }

    /**
     * 揭曉答案
     */
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
        knowledgeKingChannel.sendMessage(beautyMessageInBlock(answerMessage)).queue()
    }

    /**
     * 產生題目
     */
    private fun generateQuizForTopic(topic: String, chatGpt: ChatGptQuestionParser): Quiz {
        val questions = chatGpt.generateQuestions(topic, numberOfQuestions)
        return Quiz(topic, questions)
    }

    /**
     * 公佈主題
     */
    private fun announceTopic(topic: String, knowledgeKingChannel: TextChannel) {
        knowledgeKingChannel.sendMessage("""        
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

    /**
     * 公佈開始遊戲
     */
    private fun announceStartingGame(knowledgeKingChannel: TextChannel) {
        knowledgeKingChannel.sendMessage(beautyMessageInBlock(":triangular_flag_on_post: 全民軟體知識王現在開始囉").trimIndent())
            .queue()
    }

    /**
     * 公佈中場休息
     */
    private fun announceGameHalfWay(knowledgeKingChannel: TextChannel) {
        val rankingGroup = knowledgeKing!!.rank().getRankingGroups()
        when (val firstPlaceRankingGroup = rankingGroup.firstOrNull()) {
            null -> knowledgeKingChannel.sendMessage(beautyMessageInBlock(":loudspeaker: 比賽已經走一半了～中場休息一下～"))
            else -> knowledgeKingChannel.sendMessage(
                beautyMessageInBlock(
                    """
                            :loudspeaker: 比賽已經走一半了～中場休息一下～
                            目前的領先者為 ${firstPlaceRankingGroup.asMentionsString()}
                        """.trimIndent()
                )
            ).queue()
        }
    }

    /**
     * 公佈問答已結束
     */
    private fun announceEndingGame(knowledgeKingChannel: TextChannel) {
        knowledgeKingChannel.sendMessage("""
        ┌－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┐
        ｜感謝大家參與本次的「全民軟體知識王」，問答的階段已經結束了｜
        ｜接下來要準備公佈這次答題正確率的排名，將從第三名開始公布！｜
        └－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┘
        """.trimIndent()
        ).queue()
    }


    /**
     * 公佈第一名
     */
    private fun announceChampion(channel: TextChannel, rankingGroup: Ranking.RankingGroup?, delayInSeconds: Long) {
        val candidates = rankingGroup?.ranks?.joinToString(", ") { "<@${it.contestantId}>" }
        when (rankingGroup) {
            is Ranking.RankingGroup -> {
                channel.sendMessage("即將公佈冠軍...")
                channel.sendMessage("冠軍是...").queueAfter(delayInSeconds + 1, TimeUnit.SECONDS)
                channel.sendMessage(":trophy: 本屆的知識王是 $candidates，得分數為 ${rankingGroup.score} 分")
                    .queueAfter(delayInSeconds + 2, TimeUnit.SECONDS)
                channel.sendMessage(":tada: 恭喜脫穎而出，得到第一名的殊榮 :tada:")
                    .queueAfter(delayInSeconds + 3, TimeUnit.SECONDS)
            }
        }
    }

    /**
     * 公佈第二名
     */
    private fun announceSecondPlace(channel: TextChannel, rankGroup: Ranking.RankingGroup?, delayInSeconds: Long) {
        val candidates = rankGroup?.ranks?.joinToString(", ") { "<@${it.contestantId}>" }
        when (rankGroup) {
            null -> channel.sendMessage(":second_place: 第二名從缺 :monkey: :monkey:")
                .queueAfter(delayInSeconds, TimeUnit.SECONDS)

            else -> channel.sendMessage(":second_place: 第二名是 ${candidates}，得分數為 ${rankGroup.score} 分")
                .queueAfter(delayInSeconds, TimeUnit.SECONDS)
        }
    }

    /**
     * 公佈第三名
     */
    private fun announceThirdPlace(channel: TextChannel, rankGroup: Ranking.RankingGroup?, delayInSeconds: Long) {
        val candidates = rankGroup?.ranks?.joinToString(", ") { "<@${it.contestantId}>" }
        when (rankGroup) {
            null -> channel.sendMessage(":third_place: 第三名從缺 :joy:").queueAfter(delayInSeconds, TimeUnit.SECONDS)
            else -> channel.sendMessage(":third_place: 第三名是 ${candidates}，得分數為 ${rankGroup.score} 分")
                .queueAfter(delayInSeconds, TimeUnit.SECONDS)
        }
    }

    /**
     * 公佈 discord bot 推廣資訊
     */
    private fun announcePromotingUtopiaDiscordBot(channel: TextChannel, delayInSeconds: Long) {
        channel.sendMessage("""
        ┌－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┐
        ｜　　　　　　　　:point_right: 全民軟體知識王由 Utopia Discord Bot 提供 :point_left:　　　　　　　　　 ｜
        ｜　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　｜
        ｜　對 Utopia Discord Bot 有興趣的，趕快點擊下面連結加入專案！！　　 　　　　　　　 ｜
        ｜　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　｜
        ｜　:regional_indicator_w: Wiki：https://waterballsa.pse.is/utopia-wiki　　　　　　　　　　　　　　　　　   ｜
        ｜　:link: Discord：https://discord.com/channels/937992003415838761/1089790105369186356 　｜
        ｜　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　｜
        ｜　:loudspeaker: 還等什麼～行動起來，一起加入 Utopia Discord Bot 專案及團隊　　　　　　　　　｜
        └－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－－┘
        """.trimIndent()
        ).queueAfter(delayInSeconds, TimeUnit.SECONDS)
    }

    /**
     * 產生主題
     */
    private fun generateTopic(): String {
        return "Computer Science" // only support for CS in the current version
    }

    /**
     * 處理下一個 event
     */
    private fun handleEvents(events: List<Event>, wsa: WsaDiscordProperties, jda: JDA) {
        val knowledgeKingChannel = jda.getTextChannelById(wsa.knowledgeKingChannelId)!!
        events.forEach { event ->
            when (event) {
                is NextQuestionEvent -> handleNextQuestionEvent(event, knowledgeKingChannel)
            }
        }
    }

    /**
     * 處理「下一個問題」event
     */
    private fun handleNextQuestionEvent(event: NextQuestionEvent, knowledgeKingChannel: TextChannel) {
        log.info { "[Next Question] {\"number\": ${event.questionNumber}, \"question\":\"${event.question.description}\"" }

        val question = event.question

        knowledgeKingChannel.sendMessageEmbeds(Embed {
            this.title = "第 ${question.number} 題 - ${question.description}"
            this.description = question.options.mapIndexed { i, option -> "${'A' + i}) $option" }.joinToString("\n")
        }).addActionRow(
            Button.primary(makeButtonId(question.number, 0), "A"),
            Button.primary(makeButtonId(question.number, 1), "B"),
            Button.primary(makeButtonId(question.number, 2), "C"),
            Button.primary(makeButtonId(question.number, 3), "D")
        ).queue {
            log.info { "[Question Posted] \"question type\":\"${question.type}\",\"question number\" : ${question.number}" }
            question.generateSentTime()
        }
        knowledgeKingChannel.sendMessage(beautyMessageInBlock(":timer: 作答時間開始")).queue()
    }

    /**
     * 產生選項按鈕 id
     */
    private fun makeButtonId(questionNumber: Int, optionNumber: Int): String {
        return "${knowledgeKing!!.id}-$questionNumber-$optionNumber"
    }

    /**
     * 產生文字區塊
     * TODO: more beautiful, support multiple line and half char width
     */
    private fun beautyMessageInBlock(message: String): String {
        val messageLines = message.split("\n")
        val maxWidth = messageLines.maxByOrNull { it.length }?.let {
            ceil(getStringWidth(Regex(":(\\w+):").replace(it) { "　" }) / 2.0).toInt()
        } ?: 20
        return buildString {
            appendLine("┌${"－".repeat(maxWidth)}┐")
            appendLine("　$message")
            appendLine("└${"－".repeat(maxWidth)}┘")
        }
    }

    private fun getStringWidth(string: String): Int {
        val regex = Regex("[\\p{InCJKUnifiedIdeographs}[\\u3000-\\u303F][\\uFF01-\\uFF5E]\n]")
        if (string.isEmpty()) return 0
        return string.map {
            when {
                regex.containsMatchIn(it.toString()) -> 2
                else -> 1
            }
        }.sum()
    }
}
