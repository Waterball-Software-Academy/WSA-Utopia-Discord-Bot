package tw.waterballsa.utopia.utopiaquiz

import dev.minn.jda.ktx.interactions.components.asDisabled
import dev.minn.jda.ktx.messages.Embed
import mu.KotlinLogging
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.EventPublisher
import tw.waterballsa.utopia.jda.domains.QuizEndEvent
import tw.waterballsa.utopia.jda.domains.QuizPreparationStartEvent
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import tw.waterballsa.utopia.utopiaquiz.domain.*
import tw.waterballsa.utopia.utopiaquiz.repositories.QuizRepository
import java.time.Duration
import java.time.LocalDateTime.now

const val QUIZ_COMMAND_NAME = "quiz"
const val QUIZ_OPTION_NAME = "name"
const val QUIZ_TAG = "utopiaQuiz"
const val YELLOW = 16776960

private val log = KotlinLogging.logger {}


@Component
class UtopiaQuizListener(
    private val quizRepository: QuizRepository,
    private val questionSet: QuestionSet,
    private val eventPublisher: EventPublisher,
    private val wsa: WsaDiscordProperties,
    private val wsaGuild: Guild
) : UtopiaListener() {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(QUIZ_COMMAND_NAME, "The quiz for utopia.")
            .addOptions(
                OptionData(OptionType.STRING, QUIZ_OPTION_NAME, "The quiz you want to start.", true)
                    .addChoice("紳士考題", "紳士考題")
            )
    )

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != QUIZ_COMMAND_NAME) {
                return
            }

            val quizTakerId = user.id
            val quizName = getOption(QUIZ_OPTION_NAME)?.asString ?: return
            if (quizName != "紳士考題") {
                replyEphemerally("無效的考題名稱，輸入[紳士考題]，開始考試.")
                return
            }

            val quizId = QuizId(quizTakerId, quizName)
            if (quizRepository.findQuizById(quizId)?.pass() == true) {
                replyEphemerally("你已通過考試，不必再重考了")
                return
            }

            val quizPreparationStartEvent = object : QuizPreparationStartEvent(quizTakerId) {
                override fun startQuiz() {
                    replyEphemerally("請到私訊頻道開始考試 - $quizName")

                    val quiz = Quiz(
                        quizId,
                        QuizDefinition(4, 5),
                        questionSet,
                        QuizTimeRange(now(), Duration.ofMinutes(10))
                    )

                    val question = quiz.getCurrentQuestion()
                    quizRepository.saveQuiz(quiz)
                    user.publishQuestion(quiz, question)
                }

                override fun reply(message: String) {
                    replyEphemerally(message)
                }
            }

            eventPublisher.broadcastEvent(quizPreparationStartEvent)
        }
    }

    private fun User.publishQuestion(quiz: Quiz, question: Question) {
        openPrivateChannel().queue {
            it.sendMessageEmbeds(
                Embed {
                    title = "第 ${quiz.currentQuestionNumber} 題 - ${question.description}"
                    field {
                        name = "A"
                        value = question.options[0]
                        inline = false
                    }
                    field {
                        name = "B"
                        value = question.options[1]
                        inline = false
                    }
                    field {
                        name = "C"
                        value = question.options[2]
                        inline = false
                    }
                    field {
                        name = "D"
                        value = question.options[3]
                        inline = false
                    }
                }
            ).addActionRow(
                Button.primary(getButtonId(quiz.id.quizName, quiz.currentQuestionNumber, 0), "A"),
                Button.primary(getButtonId(quiz.id.quizName, quiz.currentQuestionNumber, 1), "B"),
                Button.primary(getButtonId(quiz.id.quizName, quiz.currentQuestionNumber, 2), "C"),
                Button.primary(getButtonId(quiz.id.quizName, quiz.currentQuestionNumber, 3), "D")
            ).queue()
        }
    }

    private fun getButtonId(quizName: String, questionNumber: Int, answerChoice: Int): String =
        "$QUIZ_TAG-$quizName-$questionNumber-$answerChoice"

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            val (quizName, questionNumber, answerChoice) = splitButtonId().ifEmpty { return }

            val quizTakerId = user.id
            val quizId = QuizId(quizTakerId, quizName)
            val quiz = quizRepository.findQuizById(quizId) ?: return
            if (quiz.isExpired() || quiz.isAllAnswered()) {
                reply("考試已結束").complete()
                return
            }

            try {
                val answer = Answer(questionNumber.toInt(), answerChoice.toInt())
                val result = quiz.answerQuestion(answer)
                replyAnswerResult(result)
            } catch (e: IllegalArgumentException) {
                reply(e.message ?: "無法回答這一題").queue()
            }

            if (quiz.isExpired() || quiz.isAllAnswered()) {
                if (quiz.pass()) {
                    channel.sendMessage("考試通過，答對題數: ${quiz.correctCount}").complete()
                    wsaGuild.getTextChannelById(wsa.wsaGentlemenBroadcastChannelId)?.sendMessageEmbeds(
                        Embed {
                            title = "🎊 紳士誕生 🎊"
                            description = "恭喜 ${user.asMention} 完成 **$quizName**！！！"
                            color = YELLOW
                            footer {
                                name = user.effectiveName
                                iconUrl = user.avatarUrl
                            }
                        }
                    )?.queue()
                    eventPublisher.broadcastEvent(QuizEndEvent(user.id, quiz.id.quizName, quiz.correctCount))
                } else {
                    channel.sendMessage("考試未通過，答對題數: ${quiz.correctCount}").complete()
                }

            } else {
                val question = quiz.getCurrentQuestion()
                user.publishQuestion(quiz, question)
            }
            quizRepository.saveQuiz(quiz)
        }
    }

    private fun ButtonInteractionEvent.splitButtonId(): List<String> {
        val buttonInfo = button.id?.split("-") ?: return emptyList()
        if (buttonInfo.first() != QUIZ_TAG) return emptyList()
        return buttonInfo.takeLast(3)
    }

    private fun ButtonInteractionEvent.replyAnswerResult(result: Boolean) {
        val button = if (result) {
            Button.success(button.id.toString(), "${button.label} 答對了!")
        } else {
            Button.danger(button.id.toString(), "${button.label} 答錯了!")
        }
        editButton(button).queue {
            val buttons = message.actionRows.asDisabled()
            hook.editMessageComponentsById(messageId, buttons).queue()
        }
    }
}
