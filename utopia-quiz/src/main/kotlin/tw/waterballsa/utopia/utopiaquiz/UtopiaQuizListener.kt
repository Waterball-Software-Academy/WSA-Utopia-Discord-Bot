package tw.waterballsa.utopia.utopiaquiz

import dev.minn.jda.ktx.interactions.components.asDisabled
import dev.minn.jda.ktx.messages.Embed
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.jda.domains.*
import tw.waterballsa.utopia.jda.extensions.replyEphemerally
import tw.waterballsa.utopia.utopiaquiz.domain.*
import tw.waterballsa.utopia.utopiaquiz.repositories.QuizRepository
import java.time.Duration
import java.time.LocalDateTime
import java.time.LocalDateTime.now

const val QUIZ_COMMAND_NAME = "quiz"
const val QUIZ_OPTION_NAME = "name"
const val QUIZ_TAG = "utopiaQuiz"

@Component
class UtopiaQuizListener(
    private val quizRepository: QuizRepository,
    private val questionSet: QuestionSet,
    private val eventPublisher: EventPublisher,
) : UtopiaListener() {

    override fun commands(): List<CommandData> = listOf(
        Commands.slash(QUIZ_COMMAND_NAME, "The quiz for utopia.")
            .addOption(OptionType.STRING, QUIZ_OPTION_NAME, "The quiz you want to start.", true)
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

            val quizCallback = object : QuizCallback {
                override fun startQuiz() {
                    replyEphemerally("請到私訊頻道開始考試 - $quizName")

                    val quiz = Quiz(
                        quizId,
                        QuizDefinition(3, 5),
                        questionSet,
                        QuizTimeRange(now(), Duration.ofMinutes(10))
                    )

                    val question = quiz.getNextQuestion()
                    quizRepository.saveQuiz(quiz)
                    user.publishQuestion(quiz, question)
                }

                override fun reply(message: String) {
                    replyEphemerally(message)
                }
            }

            eventPublisher.broadcastEvent(QuizPreparationStartEvent(quizTakerId, quizCallback))
        }
    }

    private fun User.publishQuestion(quiz: Quiz, question: Question) {
        openPrivateChannel().queue {
            it.sendMessage("").addEmbeds(
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
            if (quiz.isOver()) {
                reply("考試已結束").complete()
                return
            }

            val answer = Answer(questionNumber.toInt(), answerChoice.toInt())
            if (answer.questionNumber != quiz.currentQuestionNumber) {
                return
            }
            val result = quiz.answerQuestion(answer.questionNumber, answer.choice)
            replyAnswerResult(result)

            if (quiz.isOver()) {
                if (quiz.pass()) {
                    channel.sendMessage("考試通過，答對題數: ${quiz.correctCount}").complete()
                } else {
                    channel.sendMessage("考試未通過，答對題數: ${quiz.correctCount}").complete()
                }
                eventPublisher.broadcastEvent(QuizEndEvent(user.id, quiz.id.quizName, quiz.correctCount * 20))
            } else {
                val question = quiz.getNextQuestion()
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
