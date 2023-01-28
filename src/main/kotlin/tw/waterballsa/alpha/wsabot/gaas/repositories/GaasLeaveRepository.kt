package tw.waterballsa.alpha.wsabot.gaas.repositories

import tw.waterballsa.alpha.wsabot.gaas.entities.GaasLeave

interface GaasLeaveRepository {
    fun takeLeave(gaasLeave: GaasLeave)
}