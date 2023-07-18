package tw.waterballsa.utopia.utopiagamificationquest.domain.quests

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamificationquest.domain.Action
import tw.waterballsa.utopia.utopiagamificationquest.domain.Quest
import tw.waterballsa.utopia.utopiagamificationquest.domain.Reward

@Component
class Quests(val wsa: WsaDiscordProperties) {
    fun String.toLink(): String = "https://discord.com/channels/${wsa.guildId}/${this}"
}


class QuestBuilder {
    lateinit var title: String
    lateinit var description: String
    lateinit var reward: Reward
    lateinit var criteria: Action.Criteria
    var nextQuest: Quest? = null

    fun build() = Quest(title, description, reward, criteria, nextQuest)
}

internal fun quest(block: QuestBuilder.() -> Unit): Quest = QuestBuilder().apply(block).build()
