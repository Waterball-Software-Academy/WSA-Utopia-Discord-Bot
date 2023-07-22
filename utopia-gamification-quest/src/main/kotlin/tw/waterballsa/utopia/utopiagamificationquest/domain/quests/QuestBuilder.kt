package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamificationquest.domain.Criteria
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward

@Component
class Quests(val wsa: WsaDiscordProperties) {
    fun String.toLink(): String = "https://discord.com/channels/${wsa.guildId}/${this}"
}

class QuestBuilder {
    var questId: Int = 0
    lateinit var title: String
    lateinit var description: String
    lateinit var reward: Reward
    lateinit var criteria: Criteria
    var nextQuest: Quest? = null

    fun build() = Quest(questId, title, description, reward, criteria, nextQuest)
}

internal fun quest(block: QuestBuilder.() -> Unit): Quest {

    return QuestBuilder().apply(block).build()
}
