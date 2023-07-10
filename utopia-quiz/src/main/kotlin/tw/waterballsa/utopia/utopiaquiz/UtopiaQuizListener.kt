package tw.waterballsa.utopia.utopiaquiz

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.components.buttons.Button
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.jda.UtopiaListener
import tw.waterballsa.utopia.utopiaquiz.domain.*
import tw.waterballsa.utopia.utopiaquiz.repositories.QuizRepository
import java.time.LocalDateTime

const val QUIZ_COMMAND_NAME = "quiz"
const val QUIZ_OPTION_NAME = "quiz name"

@Component
class UtopiaQuizListener(
    private val wsa: WsaDiscordProperties,
    private val quizRepository: QuizRepository
) : UtopiaListener() {
    override fun commands(): List<CommandData> {
        return listOf(
            Commands.slash(QUIZ_COMMAND_NAME, "The quiz for utopia.")
                .addOption(OptionType.STRING, QUIZ_OPTION_NAME, "The quiz you want to start.")
        )

    }

    override fun onSlashCommandInteraction(event: SlashCommandInteractionEvent) {
        with(event) {
            if (fullCommandName != QUIZ_COMMAND_NAME) {
                return
            }

            this.user.openPrivateChannel().queue {
                it.sendMessage("考試開始").queue()
            }

            val quiz = Quiz(
                QuizId(this.member!!.id, ""),
                QuizDefinition("", 3, 5),
                QuestionSet(),
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(10)
            )
            val question = quiz.getNextQuestion()
            this.user.openPrivateChannel().queue {
                it.sendMessage(question.description).queue()

                reply(question.options.toString())
                    .addActionRow(Button.primary("1", "A"))
                    .addActionRow(Button.primary("2", "B"))
                    .addActionRow(Button.primary("3", "C"))
                    .addActionRow(Button.primary("4", "D"))
                    .queue()
            }
        }
    }

    override fun onButtonInteraction(event: ButtonInteractionEvent) {
        with(event) {
            // quizId, which question, which option "A,5,3"
            val quizId = button.id!!.split(",")[0]
            val answerQuestion = button.id!!.split(",")[1].toInt()
            val answerOption = button.id!!.split(",")[2].toInt()
//            val answer = quizRepository.findQuizById()
            // QuizRepository 有 findQuizById，裡面會有 QuizId
            // 已經拿到回答的選項id了，但要怎麼拿這個id去和考試的id比對
        }
    }
}
