package tw.waterballsa.utopia.utopiagamification.repositories.inmemory.repositoryimpl

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamification.quest.domain.Quest
import tw.waterballsa.utopia.utopiagamification.quest.domain.quests.*
import tw.waterballsa.utopia.utopiagamification.repositories.QuestRepository

@Component
class InMemoryQuestRepository(val wsa: WsaDiscordProperties) : QuestRepository {

    private val quests = mutableListOf<Quest>()

    init {
        unlockAcademyQuest
        selfIntroductionQuest
        firstMessageActionQuest
        sendContainsImageMessageInEngineerLifeChannelQuest
        replyToAnyoneInCareerAdvancementTopicChannelQuest
        watchVideoQuest
        flagPostQuest
        sendMessageInVoiceChannelQuest
        joinActivityQuest
        quizQuest
    }

    override fun findById(id: Int): Quest? = quests.find { it.id == id }

    override fun save(quest: Quest): Quest {
        quests.add(quest)
        return quest
    }

    fun String.toLink(): String = "https://discord.com/channels/${wsa.guildId}/${this}"
}
