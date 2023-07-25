package tw.waterballsa.utopia

import tw.waterballsa.utopia.domain.CherryPick

class CherryPickRepository {

    private val cherryPickerIdToCherryPick = mutableMapOf<String, CherryPick>()

    fun save(cherryPick: CherryPick) {
        with(cherryPick) {
            cherryPickerIdToCherryPick[cherryPickerId] = this
        }
    }

    fun delete(cherryPickerId: String) {
        cherryPickerIdToCherryPick.remove(cherryPickerId)
    }

    fun exists(cherryPickerId: String): Boolean = cherryPickerIdToCherryPick.containsKey(cherryPickerId)

    fun findById(cherryPickerId: String): CherryPick? = cherryPickerIdToCherryPick[cherryPickerId]
}
