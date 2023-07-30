package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward

@Component
class Quests(val wsa: WsaDiscordProperties) {
    private val quests = mutableListOf<Quest>()

    //TODO 因為現在好測試功能正確性，之後會再全面重新做設計。包括 quest id 的設計。
    init {
        quests.addAll(
            listOf(
                unlockAcademyQuest,
                selfIntroductionQuest,
                SendContainsImageMessageInEngineerLifeChannelQuest,
                firstMessageActionQuest,
                flagPostQuest,
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
