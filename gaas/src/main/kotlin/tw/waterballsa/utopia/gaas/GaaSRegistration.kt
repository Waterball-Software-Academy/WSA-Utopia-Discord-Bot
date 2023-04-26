package tw.waterballsa.utopia.gaas

import ch.qos.logback.core.util.OptionHelper
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.message.MessageReceivedEvent
import net.dv8tion.jda.api.interactions.components.buttons.Button
import net.dv8tion.jda.api.interactions.components.text.TextInput
import net.dv8tion.jda.api.interactions.components.text.TextInputStyle
import net.dv8tion.jda.api.interactions.modals.Modal
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.commons.config.logger
import tw.waterballsa.utopia.jda.listener
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration

fun listen(wsaDiscordProperties: WsaDiscordProperties) = listener {
    on<MessageReceivedEvent> {
        val wsaGaaSRegistrationThreadId = wsaDiscordProperties.wsaGaaSRegistrationThreadId
        if (channel.id != wsaGaaSRegistrationThreadId) {
            return@on
        }
        if (message.contentRaw == "我要報名遊戲微服務計畫!") {
            val channel = this.message.author.openPrivateChannel().complete();

            this.message
                .reply("點擊下方按鈕報名GaaS")
                .addActionRow(
                    Button.primary("GaaSRegistrationButton", "報名表單")
                    //.withEmoji(Emoji.Type()),  // Link Button with label and emoji
                )
                .queue()
        }
    }
}

fun openForm() = listener {
    on<ButtonInteractionEvent> {
        if (componentId == "GaaSRegistrationButton") {
            val githubEmail = TextInput.create("GithubEmail", "Github Email", TextInputStyle.SHORT)
                .setMaxLength(100)
                .build()
            val techStack = TextInput.create(
                "TechStack",
                "您想用什麼 Tech Stack 來開發一款回合制遊戲服務呢？",
                TextInputStyle.PARAGRAPH
            )
                .setMinLength(10)
                .setMaxLength(300)
                .build()
            val expected = TextInput.create("Expected", "您對於讀書會的期望形式為何呢？", TextInputStyle.PARAGRAPH)
                .setMinLength(10)
                .setMaxLength(300)
                .build()
            val feedback = TextInput.create(
                "Feedback",
                "我們鼓勵您能在讀書會分享過程與知識，且和讀書會成員們交流，您期待在讀書會上得到什麼回饋？",
                TextInputStyle.PARAGRAPH
            )
                .setMinLength(10)
                .setMaxLength(300)
                .build()
            val forward =
                TextInput.create("Forward", "期待在執行完 GaaS 結束之後,自己有什麼變化？", TextInputStyle.PARAGRAPH)
                    .setMinLength(10)
                    .setMaxLength(300)
                    .build()
            val modal = Modal.create("GaaSRegistrationModal", "報名表單")
                .addActionRow(githubEmail)
                .addActionRow(techStack)
                .addActionRow(expected)
                .addActionRow(feedback)
                .addActionRow(forward)
                .build()
            this.replyModal(modal).queue()
        }
    }
}

fun onSubmitModal(wsaDiscordProperties: WsaDiscordProperties) = listener {
    on<ModalInteractionEvent> {
        if (this.modalId != "GaaSRegistrationModal")
            return@on
        val githubEmail = getValue("GithubEmail")!!
        val techStack = getValue("TechStack")!!
        val expected = getValue("Expected")!!
        val feedback = getValue("Feedback")!!
        val forward = getValue("Forward")!!
        // 發送 Github 邀請
        val isSuccessed = sendGithubInvitation(githubEmail.asString)
        // 加入身分組
        val role = this.guild?.getRolesByName("遊戲微服務計畫成員", false)?.get(0)!!
        this.guild?.addRoleToMember(this.user, role)?.complete()
        // 存到資料庫

        // 發送成功訊息
        this.reply("報名成功!").setEphemeral(true).queue()
    }
}

private fun sendGithubInvitation(email: String?): Boolean {
    val request = HttpRequest.newBuilder()
        .uri(URI.create(OptionHelper.getEnv("GITHUB_ORG_INVITATION_API_URL")))
        .header("Content-Type", "application/x-www-form-urlencoded")
        .POST(HttpRequest.BodyPublishers.ofString("org=Game-as-a-service&team_ids=6728142&email=${email}"))
        .build()
    val httpClient: HttpClient = HttpClient.newBuilder()
        .version(HttpClient.Version.HTTP_1_1) // http 1.1
        .connectTimeout(Duration.ofSeconds(5)) // timeout after 5 seconds
        .build()
    val response = httpClient.send(
        request, HttpResponse.BodyHandlers.ofString()
    )
    logger.debug { response.body() }
    //if (response.statusCode() == 201)
    return true

}

