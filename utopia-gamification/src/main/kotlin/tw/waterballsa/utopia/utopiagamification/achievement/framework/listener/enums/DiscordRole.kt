package tw.waterballsa.utopia.utopiagamification.achievement.framework.listener.enums

import org.springframework.stereotype.Component
import org.testcontainers.shaded.org.bouncycastle.asn1.x500.style.RFC4519Style.name
import tw.waterballsa.utopia.commons.config.WsaDiscordProperties
import tw.waterballsa.utopia.utopiagamification.quest.domain.RoleType

/**
 *  把 DiscordRole 和 Domain RoleType 用 RoleName 作為 key 連結起來
 *  1. Map<RoleType.name, WsaProperties.roleId>
 *  2. 從 wsa properties 讀取 jda role id
 */
@Component
class DiscordRole(
    private val properties: WsaDiscordProperties
) {
    private val roleTypeToId = mutableMapOf<RoleType, String>()

    init {
        roleTypeToId[RoleType.LONG_ARTICLE] = properties.wsaLongArticleRoleId
        roleTypeToId[RoleType.TOPIC_MASTER] = properties.wsaTopicMasterRoleId
    }

    fun getRoleId(roleType: RoleType): String =
        roleTypeToId[roleType] ?: throw IllegalArgumentException("The role type $name is not valid, as the role type has not been defined.")
}
