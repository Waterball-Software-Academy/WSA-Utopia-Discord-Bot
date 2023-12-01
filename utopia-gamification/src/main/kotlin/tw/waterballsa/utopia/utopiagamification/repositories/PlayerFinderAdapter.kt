package tw.waterballsa.utopia.utopiagamification.repositories

import tw.waterballsa.utopia.minigames.Player
import tw.waterballsa.utopia.minigames.PlayerFinder

class PlayerFinderAdapter : PlayerFinder {

    // TODO: we need a important repository here, please add it

    override fun findById(id: String): Player {
        TODO("Not yet implemented")
        // TODO: we need to find player by id and transform it into the player dto which we need.
        // TODO: please keep this in mind: the model in utopia gamification can't be access by other module from outside.
    }
}
