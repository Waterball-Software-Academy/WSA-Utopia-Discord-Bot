package tw.waterballsa.utopia.gamification.quest.domain.quests

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.gamification.quest.domain.*

@Component
class Quests(val wsa: WsaDiscordProperties) {
    private val quests = mutableListOf<Quest>()

    //TODO 因為現在好測試功能正確性，之後會再全面重新做設計。包括 quest id 的設計。
    init {
        quests.addAll(
            listOf(
                unlockAcademyQuest,
                selfIntroductionQuest,
                firstMessageActionQuest,
                SendContainsImageMessageInEngineerLifeChannelQuest,
                watchVideoQuest,
                flagPostQuest,
                ReplyToAnyoneInCareerAdvancementTopicChannelQuest,
                SendMessageInVoiceChannelQuest,
                JoinActivityQuest,
                quizQuest
            )
        )
    }

    fun findById(questId: Int): Quest = quests.first { it.id == questId }

    fun String.toLink(): String = "https://discord.com/channels/${wsa.guildId}/${this}"

}

private const val completeMessage = "任務完成！"

class QuestBuilder {

    var id: Int = 0
    lateinit var title: String
    lateinit var description: String
    lateinit var reward: Reward
    var roleType: RoleType = RoleType.EVERYONE
    var periodType: PeriodType = PeriodType.NONE
    lateinit var criteria: Action.Criteria
    var preCondition: PreCondition = EmptyPreCondition()
    var nextQuest: Quest? = null
    var postMessage: String = completeMessage
    fun build() =
        Quest(id, title, description, preCondition, roleType, periodType, criteria, reward, nextQuest, postMessage)
}

internal fun quest(block: QuestBuilder.() -> Unit): Quest = QuestBuilder().apply(block).build()
