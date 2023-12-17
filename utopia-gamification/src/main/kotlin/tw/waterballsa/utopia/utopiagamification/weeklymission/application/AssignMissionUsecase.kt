package tw.waterballsa.utopia.utopiagamification.weeklymission.application

import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel
import org.springframework.stereotype.Component
import tw.waterballsa.utopia.utopiagamification.repositories.MissionRepository
import tw.waterballsa.utopia.utopiagamification.weeklymission.domain.*
import kotlin.random.Random

@Component
class AssignMissionUsecase(
        private val missionRepository: MissionRepository,
        private val sendMessageWeeklyMissionRepository: SendMessageWeeklyMissionRepository,
        private val messageChannelList : MessageChannelList,
        private val voiceChannelList: VoiceChannelList,
        private val joinChannelWeeklyMissionRepository: JoinChannelWeeklyMissionRepository
) {

    private val random : Random = Random(10)

    fun execute(presenter: Presenter) {
        val gentlemanIds = getGentlemanIds()

        val events = mutableListOf<AssignedWeeklyMissionEvent>()

        gentlemanIds.forEach {
            val randomWeeklyMissions = createRandomWeeklyMissions(it)
            events.add(AssignedWeeklyMissionEvent(it, randomWeeklyMissions))
        }
        presenter.present(events)
    }

    private fun getGentlemanIds() = missionRepository.findAllByQuestId(10)
            .filter { it.isCompleted() }
            .map { it.player.id }

    private fun createRandomWeeklyMissions(gentlemanId :String) : List<WeeklyMission> {
        val weeklyMissions = mutableListOf<WeeklyMission>()
        val whichType = random.nextInt(2)
        when(whichType.getType()) {
            WeeklyMissionType.MESSAGE->{
                val messageWeeklyMission = createRandomSendMessageWeeklyMission(gentlemanId, random)
                weeklyMissions.add(messageWeeklyMission)
                sendMessageWeeklyMissionRepository.save(messageWeeklyMission)
            }
            WeeklyMissionType.VOICE->{
                val joinVoiceChannelMission = createRandomJoinVoiceWeeklyMission(gentlemanId, random)
                weeklyMissions.add(joinVoiceChannelMission)
                joinChannelWeeklyMissionRepository.save(joinVoiceChannelMission)
            }
        }
        return weeklyMissions
    }

    private fun Int.getType() : WeeklyMissionType {
        return WeeklyMissionType.getWeeklyMissionType(this)
    }

    private fun createRandomSendMessageWeeklyMission(gentlemanId : String,random: Random) : SendMessageMission {
        return SendMessageMission(
                gentlemanId,
                messageChannelList.getRandomMessageChannelId(),
                random.nextBoolean(),
                random.nextBoolean(),
                random.nextInt(100),
                random.nextInt(5),
                random.nextInt(5))
    }

    private fun createRandomJoinVoiceWeeklyMission(gentlemanId: String, random: Random) : JoinVoiceChannelMission {
        return JoinVoiceChannelMission(
                gentlemanId,
                voiceChannelList.getRandomVoiceChannelId(),
                random.nextInt(150),
                random.nextLong(60),
                null
        )
    }

    interface Presenter{
        fun present(events: List<AssignedWeeklyMissionEvent>)
    }
}
