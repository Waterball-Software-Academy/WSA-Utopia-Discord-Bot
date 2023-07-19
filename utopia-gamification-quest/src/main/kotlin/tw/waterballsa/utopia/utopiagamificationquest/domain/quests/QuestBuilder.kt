package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward

@Component
class Quests(val wsa: WsaDiscordProperties) {
    private val quests = mutableListOf<Quest>()

    init {
        quests.addAll(
            listOf(
                unlockAcademyQuest,
                selfIntroductionQuest,
                SendContainsImageMessageInEngineerLifeChannelQuest,
                firstMessageActionQuest,
                flagPostQuest,
                participateInDiscussionQuest,
                ReplyToAnyoneInCareerAdvancementTopicChannelQuest,
                quizQuest
            )
        )
    }

    fun findById(questId: Int): Quest = quests.first { it.id == questId }
    fun String.toLink(): String = "https://discord.com/channels/${wsa.guildId}/${this}"

}


class QuestBuilder {
    var questId: Int = 0
    lateinit var title: String
    lateinit var description: String
    lateinit var reward: Reward
    lateinit var criteria: Action.Criteria
    var nextQuest: Quest? = null

    fun build() = Quest(questId, title, description, reward, criteria, nextQuest)
}

internal fun quest(block: QuestBuilder.() -> Unit): Quest = QuestBuilder().apply(block).build()
