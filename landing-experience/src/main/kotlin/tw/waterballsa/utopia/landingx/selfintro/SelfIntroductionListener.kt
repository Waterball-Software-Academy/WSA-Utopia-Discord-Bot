package tw.waterballsa.utopia.landingx.selfintro

import mu.KotlinLogging

val log = KotlinLogging.logger {}

//
//fun autoCreateThreadWheneverMemberCreateMessageInSelfIntroChannel(wsa: WsaDiscordProperties) = listener("") {
//    on<MessageCreateEvent> {
//        val channelIdValue = message.channelId.value
//        if (wsa.selfIntroChannelId != channelIdValue) {
//            return@on
//        }
//        val author = message.asMessage().author!!
//        val threadName = "【${author.username}】"
//        discord.kord.rest.channel.startThreadWithMessage(
//            message.channelId,
//            message.id,
//            name = threadName,
//            ArchiveDuration.Week
//        ) {}
//        log.info { "[Auto create a thread on new message] {\"threadName\":\"$threadName\"}" }
//    }
//}
