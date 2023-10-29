package tw.waterballsa.utopia.utopiagamification.achievement.framework.dao

import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.achievement.application.repository.AchievementRepository
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.Achievement.Rule
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.LongArticleAchievement
import tw.waterballsa.utopia.utopiagamification.achievement.domain.achievements.TopicMasterAchievement
import tw.waterballsa.utopia.utopiagamification.quest.domain.Reward
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType.LONG_ARTICLE
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType.TOPIC_MASTER

@Component
class AchievementDao : AchievementRepository {

    private val achievements = mutableListOf<Achievement>()

    init {
        achievements.addAll(
            listOf(
                LongArticleAchievement(
                    LongArticleAchievement.Condition(800),
                    Rule(LONG_ARTICLE, 1),
                    Reward(1000u, LONG_ARTICLE)
                ),
                TopicMasterAchievement(
                    TopicMasterAchievement.Condition(),
                    Rule(TOPIC_MASTER, 300),
                    Reward(2500u, TOPIC_MASTER)
                )
            )
        )
    }

    override fun findByType(type: Achievement.Type): List<Achievement> = achievements.filter { it.type == type }
}
